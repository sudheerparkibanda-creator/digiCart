package com.digiCart.address_service.service;

import com.digiCart.address_service.model.Address;
import com.digiCart.address_service.repository.AddressRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    private static final Logger log = LoggerFactory.getLogger(AddressService.class);

    public List<Address> getAllAddresses() {
        log.info("Entering getAllAddresses");
        return addressRepository.findAll();
    }

    public Optional<Address> getAddressById(String addressId) {
        log.info("Entering getAddressById with addressId: {}", addressId);
        if (addressId == null || addressId.isBlank()) {
            return Optional.empty();
        }
        return addressRepository.findById(addressId);
    }

    public Address addAddress(Address address) {
        log.info("Entering addAddress with address: {}", address);
        address.setAddressId(null);
        if (address.getCountry() == null || address.getCountry().isBlank()) {
            address.setCountry("India");
        }
        return addressRepository.save(address);
    }

    public Optional<Address> updateAddress(String addressId, Address updated) {
        log.info("Entering updateAddress with addressId: {}, updated: {}", addressId, updated);
        return addressRepository.findById(addressId).map(existing -> {
            existing.setFullName(updated.getFullName());
            existing.setMobileNumber(updated.getMobileNumber());
            existing.setAddressLine1(updated.getAddressLine1());
            existing.setAddressLine2(updated.getAddressLine2());
            existing.setLandmark(updated.getLandmark());
            existing.setCity(updated.getCity());
            existing.setDistrict(updated.getDistrict());
            existing.setState(updated.getState());
            existing.setPinCode(updated.getPinCode());
            existing.setCountry(updated.getCountry() == null || updated.getCountry().isBlank()
                    ? "India" : updated.getCountry());
            existing.setAddressType(updated.getAddressType());
            existing.setAlternatePhone(updated.getAlternatePhone());
            return addressRepository.save(existing);
        });
    }

    public Optional<Address> removeAddress(String addressId) {
        log.info("Entering removeAddress with addressId: {}", addressId);
        Optional<Address> existing = addressRepository.findById(addressId);
        existing.ifPresent(addressRepository::delete);
        return existing;
    }
}

