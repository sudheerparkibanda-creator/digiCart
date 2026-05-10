package com.digiCart.address_service.controller;

import com.digiCart.address_service.model.Address;
import com.digiCart.address_service.service.AddressService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/addresses")
public class AddressController {

    private static final Logger log = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    public List<Address> getAllAddresses() {
        log.info("Entering getAllAddresses");
        return addressService.getAllAddresses();
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<Address> getAddressById(@PathVariable String addressId) {
        log.info("Entering getAddressById with addressId: {}", addressId);
        return addressService.getAddressById(addressId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody Address request) {
        log.info("Entering addAddress with request: {}", request);
        Address saved = addressService.addAddress(request);
        return ResponseEntity.created(URI.create("/addresses/" + saved.getAddressId()))
                .body(saved);
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable String addressId,
                                                  @RequestBody Address request) {
        log.info("Entering updateAddress with addressId: {} and request: {}", addressId, request);
        return addressService.updateAddress(addressId, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable String addressId) {
        log.info("Entering removeAddress with addressId: {}", addressId);
        return addressService.removeAddress(addressId)
                .map(a -> ResponseEntity.noContent().<Void>build())
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

