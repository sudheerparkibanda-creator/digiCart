package com.digiCart.cart_service.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.digiCart.cart_service.dto.AddToCartRequest;
import com.digiCart.cart_service.dto.CartItemResponse;
import com.digiCart.cart_service.dto.CartResponse;
import com.digiCart.cart_service.dto.EntryQuantityRequest;
import com.digiCart.cart_service.dto.PlaceOrderResponse;
import com.digiCart.cart_service.dto.ProductDetailsResponse;
import com.digiCart.cart_service.dto.SetExistingDeliveryAddressRequest;
import com.digiCart.cart_service.dto.SetNewDeliveryAddressRequest;
import com.digiCart.cart_service.model.Cart;
import com.digiCart.cart_service.model.CartItem;
import com.digiCart.cart_service.model.CartStatus;
import com.digiCart.cart_service.repository.CartRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final PriceServiceClient priceServiceClient;
    private final StockServiceClient stockServiceClient;
    private final ProductServiceClient productServiceClient;
    private final AddressServiceClient addressServiceClient;
    private final CustomerServiceClient customerServiceClient;
    private final OrderServiceClient orderServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    public CartService(CartRepository cartRepository,
                       PriceServiceClient priceServiceClient,
                       StockServiceClient stockServiceClient,
                       ProductServiceClient productServiceClient,
                       AddressServiceClient addressServiceClient,
                       CustomerServiceClient customerServiceClient,
                       OrderServiceClient orderServiceClient,
                       PaymentServiceClient paymentServiceClient) {
        this.cartRepository = cartRepository;
        this.priceServiceClient = priceServiceClient;
        this.stockServiceClient = stockServiceClient;
        this.productServiceClient = productServiceClient;
        this.addressServiceClient = addressServiceClient;
        this.customerServiceClient = customerServiceClient;
        this.orderServiceClient = orderServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    public CartResponse createCart(String customerId) {
        validateCustomerId(customerId);

        Cart cart = new Cart();
        cart.setCartId(UUID.randomUUID().toString());
        cart.setUserId(customerId);
        cart.setStatus(CartStatus.Active);
        cart.setTotal(0.0);

        System.out.println("Before save: cart = " + cart);
        Cart savedCart = cartRepository.save(cart);
        System.out.println("After save: savedCart = " + savedCart);

        return toResponse(savedCart);
    }

    public void deleteCart(String customerId, String cartId) {
        validateCartOwnershipRequest(customerId, cartId);

        Cart cart = getOwnedCart(customerId, cartId);
        cartRepository.delete(cart);
    }

    public List<CartResponse> getAllCartsForCustomer(String customerId) {
        validateCustomerId(customerId);

        List<Cart> carts = cartRepository.findByUserId(customerId);
        carts.sort(Comparator.comparing(Cart::getCreationTime, Comparator.nullsLast(LocalDateTime::compareTo)).reversed());

        List<CartResponse> responses = new ArrayList<>();
        for (Cart cart : carts) {
            responses.add(toResponse(cart));
        }
        return responses;
    }

    public CartResponse setDeliveryAddressFromNewAddress(String customerId,
                                                         String cartId,
                                                         SetNewDeliveryAddressRequest request) {
        validateNewAddressRequest(customerId, cartId, request);

        Cart cart = getOwnedCart(customerId, cartId);
        String addressId = addressServiceClient.createAddress(request);

        cart.setAddressId(addressId);
        Cart savedCart = cartRepository.save(cart);

        if (Boolean.TRUE.equals(request.getAddToCustomerAddresses())) {
            customerServiceClient.addAddressToCustomer(customerId, addressId);
        }

        return toResponse(savedCart);
    }

    public CartResponse setDeliveryAddressFromExistingAddress(String customerId,
                                                              String cartId,
                                                              SetExistingDeliveryAddressRequest request) {
        validateExistingAddressRequest(customerId, cartId, request);

        if (!addressServiceClient.addressExists(request.getAddressId())) {
            throw new NoSuchElementException("Address not found for id: " + request.getAddressId());
        }

        Cart cart = getOwnedCart(customerId, cartId);
        cart.setAddressId(request.getAddressId());
        Cart savedCart = cartRepository.save(cart);

        if (Boolean.TRUE.equals(request.getSetAsDefaultInCustomer())) {
            customerServiceClient.setDefaultAddress(customerId, request.getAddressId());
        }

        return toResponse(savedCart);
    }

    public PlaceOrderResponse placeOrder(String customerId, String cartId) {
        validateCartOwnershipRequest(customerId, cartId);

        Cart cart = getOwnedCart(customerId, cartId);
        validatePlaceOrderCart(cart);

        assignMissingEntryNumbers(cart.getItems());
        recalculateCartForCheckout(cart);

        if (!addressServiceClient.addressExists(cart.getAddressId())) {
            throw new NoSuchElementException("Address not found for id: " + cart.getAddressId());
        }

        validateStockForCheckout(cart.getItems());

        OrderServiceClient.OrderData orderData = orderServiceClient.createOrderFromCart(cart, "PaymentPending");
        cartRepository.delete(cart);

        PlaceOrderResponse response = new PlaceOrderResponse();
        response.setOrderId(orderData.getOrderId());
        response.setCartId(cart.getCartId());
        response.setStatus(orderData.getStatus());
        response.setPaymentLink(orderData.getPaymentLink());
        response.setTotal(cart.getTotal());
        return response;
    }

    public CartResponse addToCart(String customerId, String cartId, AddToCartRequest request) {
        validateRequest(customerId, cartId, request);

        Cart cart = getOwnedCart(customerId, cartId);

        // Backfill entry numbers for legacy rows created before entry_number support.
        assignMissingEntryNumbers(cart.getItems());

        PriceServiceClient.PriceData priceData = priceServiceClient.getByProductCode(request.getProductId());
        if (priceData == null || priceData.getPrice() == null) {
            throw new NoSuchElementException("Price not found for product: " + request.getProductId());
        }

        CartItem item = findOrCreateItem(cart, request.getProductId());
        int newQuantity = item.getQuantity() == null ? request.getQuantity() : item.getQuantity() + request.getQuantity();

        StockServiceClient.StockData stockData = stockServiceClient.getByProductId(request.getProductId());
        int availableQuantity = stockData == null || stockData.getAvailableQuantity() == null
                ? 0
                : stockData.getAvailableQuantity();

        if (availableQuantity < newQuantity) {
            throw new IllegalStateException("Insufficient stock for product: " + request.getProductId());
        }

        double unitPrice = priceData.getPrice();
        double itemTotal = unitPrice * newQuantity;

        item.setQuantity(newQuantity);
        item.setUnitPrice(unitPrice);
        item.setPrice(unitPrice);
        item.setTotalPrice(itemTotal);

        cart.setTotal(calculateCartTotal(cart.getItems()));

        Cart savedCart = cartRepository.save(cart);
        return toResponse(savedCart);
    }

    public CartResponse addEntryQuantity(String customerId,
                                         String cartId,
                                         Integer entryNumber,
                                         EntryQuantityRequest request) {
        validateQuantityUpdateRequest(customerId, cartId, entryNumber, request);

        Cart cart = getOwnedCart(customerId, cartId);
        assignMissingEntryNumbers(cart.getItems());

        CartItem item = findItemByEntryNumber(cart.getItems(), entryNumber);
        int currentQty = item.getQuantity() == null ? 0 : item.getQuantity();
        int newQty = currentQty + request.getQuantity();

        StockServiceClient.StockData stockData = stockServiceClient.getByProductId(item.getProductId());
        int availableQuantity = stockData == null || stockData.getAvailableQuantity() == null
                ? 0
                : stockData.getAvailableQuantity();
        if (availableQuantity < newQty) {
            throw new IllegalStateException("Insufficient stock for product: " + item.getProductId());
        }

        PriceServiceClient.PriceData priceData = priceServiceClient.getByProductCode(item.getProductId());
        double unitPrice = priceData != null && priceData.getPrice() != null
                ? priceData.getPrice()
                : (item.getUnitPrice() == null ? 0 : item.getUnitPrice());

        item.setQuantity(newQty);
        item.setUnitPrice(unitPrice);
        item.setPrice(unitPrice);
        item.setTotalPrice(unitPrice * newQty);

        cart.setTotal(calculateCartTotal(cart.getItems()));
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeEntryQuantity(String customerId,
                                            String cartId,
                                            Integer entryNumber,
                                            EntryQuantityRequest request) {
        validateQuantityUpdateRequest(customerId, cartId, entryNumber, request);

        Cart cart = getOwnedCart(customerId, cartId);
        assignMissingEntryNumbers(cart.getItems());

        CartItem item = findItemByEntryNumber(cart.getItems(), entryNumber);
        int currentQty = item.getQuantity() == null ? 0 : item.getQuantity();
        int newQty = currentQty - request.getQuantity();

        if (newQty <= 0) {
            cart.getItems().remove(item);
        } else {
            item.setQuantity(newQty);
            double unitPrice = item.getUnitPrice() == null ? 0 : item.getUnitPrice();
            item.setPrice(unitPrice);
            item.setTotalPrice(unitPrice * newQty);
        }

        cart.setTotal(calculateCartTotal(cart.getItems()));
        return toResponse(cartRepository.save(cart));
    }

    public CartResponse removeEntryByEntryNumber(String customerId, String cartId, Integer entryNumber) {
        validateEntryRequest(customerId, cartId, entryNumber);

        Cart cart = getOwnedCart(customerId, cartId);
        assignMissingEntryNumbers(cart.getItems());

        CartItem item = findItemByEntryNumber(cart.getItems(), entryNumber);
        cart.getItems().remove(item);
        cart.setTotal(calculateCartTotal(cart.getItems()));

        return toResponse(cartRepository.save(cart));
    }

    private void validateRequest(String customerId, String cartId, AddToCartRequest request) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId is required");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getProductId() == null || request.getProductId().isBlank()) {
            throw new IllegalArgumentException("productId is required");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }

    private void validateCustomerId(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId is required");
        }
    }

    private void validateNewAddressRequest(String customerId,
                                           String cartId,
                                           SetNewDeliveryAddressRequest request) {
        validateCartOwnershipRequest(customerId, cartId);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new IllegalArgumentException("fullName is required");
        }
        if (request.getMobileNumber() == null || request.getMobileNumber().isBlank()) {
            throw new IllegalArgumentException("mobileNumber is required");
        }
        if (request.getAddressLine1() == null || request.getAddressLine1().isBlank()) {
            throw new IllegalArgumentException("addressLine1 is required");
        }
        if (request.getAddressLine2() == null || request.getAddressLine2().isBlank()) {
            throw new IllegalArgumentException("addressLine2 is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new IllegalArgumentException("city is required");
        }
        if (request.getState() == null || request.getState().isBlank()) {
            throw new IllegalArgumentException("state is required");
        }
        if (request.getPinCode() == null || request.getPinCode().isBlank()) {
            throw new IllegalArgumentException("pinCode is required");
        }
        if (request.getAddressType() == null || request.getAddressType().isBlank()) {
            throw new IllegalArgumentException("addressType is required");
        }
    }

    private void validateExistingAddressRequest(String customerId,
                                                String cartId,
                                                SetExistingDeliveryAddressRequest request) {
        validateCartOwnershipRequest(customerId, cartId);
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (request.getAddressId() == null || request.getAddressId().isBlank()) {
            throw new IllegalArgumentException("addressId is required");
        }
    }

    private void validatePlaceOrderCart(Cart cart) {
        if (cart.getAddressId() == null || cart.getAddressId().isBlank()) {
            throw new IllegalArgumentException("addressId is required before placing order");
        }
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place order for an empty cart");
        }
    }

    private void recalculateCartForCheckout(Cart cart) {
        for (CartItem item : cart.getItems()) {
            PriceServiceClient.PriceData priceData = priceServiceClient.getByProductCode(item.getProductId());
            if (priceData == null || priceData.getPrice() == null) {
                throw new NoSuchElementException("Price not found for product: " + item.getProductId());
            }

            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            if (quantity <= 0) {
                throw new IllegalStateException("Invalid quantity for product: " + item.getProductId());
            }

            double unitPrice = priceData.getPrice();
            item.setUnitPrice(unitPrice);
            item.setPrice(unitPrice);
            item.setTotalPrice(unitPrice * quantity);
        }
        cart.setTotal(calculateCartTotal(cart.getItems()));
    }

    private void validateStockForCheckout(List<CartItem> items) {
        for (CartItem item : items) {
            StockServiceClient.StockData stockData = stockServiceClient.getByProductId(item.getProductId());
            int availableQuantity = stockData == null || stockData.getAvailableQuantity() == null
                    ? 0
                    : stockData.getAvailableQuantity();
            int requestedQuantity = item.getQuantity() == null ? 0 : item.getQuantity();
            if (availableQuantity < requestedQuantity) {
                throw new IllegalStateException("Insufficient stock for product: " + item.getProductId());
            }
        }
    }

    private void validateCartOwnershipRequest(String customerId, String cartId) {
        validateCustomerId(customerId);
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId is required");
        }
    }

    private void validateQuantityUpdateRequest(String customerId,
                                               String cartId,
                                               Integer entryNumber,
                                               EntryQuantityRequest request) {
        validateEntryRequest(customerId, cartId, entryNumber);
        if (request == null || request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }
    }

    private void validateEntryRequest(String customerId, String cartId, Integer entryNumber) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId is required");
        }
        if (cartId == null || cartId.isBlank()) {
            throw new IllegalArgumentException("cartId is required");
        }
        if (entryNumber == null || entryNumber < 0) {
            throw new IllegalArgumentException("entryNumber must be zero or greater");
        }
    }

    private Cart getOwnedCart(String customerId, String cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NoSuchElementException("Cart not found for id: " + cartId));
        if (!customerId.equals(cart.getUserId())) {
            throw new IllegalArgumentException("Cart does not belong to customer: " + customerId);
        }
        return cart;
    }

    private CartItem findItemByEntryNumber(List<CartItem> items, Integer entryNumber) {
        return items.stream()
                .filter(item -> Objects.equals(item.getEntryNumber(), entryNumber))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cart item not found for entryNumber: " + entryNumber));
    }

    private CartItem findOrCreateItem(Cart cart, String productId) {
        for (CartItem item : cart.getItems()) {
            if (productId.equals(item.getProductId())) {
                return item;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setEntryNumber(nextEntryNumber(cart.getItems()));
        newItem.setProductId(productId);
        cart.getItems().add(newItem);
        return newItem;
    }

    private int nextEntryNumber(List<CartItem> items) {
        return items.stream()
                .map(CartItem::getEntryNumber)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .map(max -> max + 1)
                .orElse(0);
    }

    private void assignMissingEntryNumbers(List<CartItem> items) {
        int next = nextEntryNumber(items);
        for (CartItem item : items) {
            if (item.getEntryNumber() == null) {
                item.setEntryNumber(next++);
            }
        }
    }

    private double calculateCartTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            if (item.getTotalPrice() != null) {
                total += item.getTotalPrice();
            }
        }
        return total;
    }

    private CartResponse toResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getCartId());
        response.setUserId(cart.getUserId());
        response.setStatus(cart.getStatus());
        response.setAddressId(cart.getAddressId());
        response.setTotal(cart.getTotal());
        response.setCreationTime(cart.getCreationTime());

        List<CartItem> sortedItems = new ArrayList<>(cart.getItems());
        sortedItems.sort(Comparator.comparing(CartItem::getEntryNumber, Comparator.nullsLast(Integer::compareTo)));

        List<CartItemResponse> itemResponses = new ArrayList<>();
        for (CartItem item : sortedItems) {
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setEntryNumber(item.getEntryNumber());
            itemResponse.setProductId(item.getProductId());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setTotalPrice(item.getTotalPrice());

            StockServiceClient.StockData stockData = stockServiceClient.getByProductId(item.getProductId());
            itemResponse.setAvailableStock(stockData == null ? 0 : stockData.getAvailableQuantity());

            ProductDetailsResponse product = productServiceClient.getByCode(item.getProductId());
            itemResponse.setProduct(product);

            itemResponses.add(itemResponse);
        }
        response.setItems(itemResponses);
        return response;
    }
}

