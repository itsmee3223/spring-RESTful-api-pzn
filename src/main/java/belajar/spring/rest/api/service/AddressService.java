package belajar.spring.rest.api.service;

import belajar.spring.rest.api.entity.Address;
import belajar.spring.rest.api.entity.Contact;
import belajar.spring.rest.api.entity.User;
import belajar.spring.rest.api.model.AddressResponse;
import belajar.spring.rest.api.model.CreateAddressRequest;
import belajar.spring.rest.api.model.UpdateAddressRequest;
import belajar.spring.rest.api.model.UpdateContactRequest;
import belajar.spring.rest.api.repository.AddressRepository;
import belajar.spring.rest.api.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressService {
    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public AddressResponse create(User user, CreateAddressRequest request){
        validationService.validate(request);
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setContact(contact);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());

        addressRepository.save(address);

        return toAddressResponse(address);
    }

    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request){
        validationService.validate(request);
        Contact contact = contactRepository.findFirstByUserAndId(user, request.getContactId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, request.getAddressId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return toAddressResponse(address);
    }

    private AddressResponse toAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .postalCode(address.getPostalCode())
                .build();
    }
}
