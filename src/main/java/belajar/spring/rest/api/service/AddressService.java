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

import java.util.List;
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

    @Transactional(readOnly = true)
    public AddressResponse get(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        return toAddressResponse(address);
    }

    @Transactional
    public void remove(User user, String contactId, String addressId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        Address address = addressRepository.findFirstByContactAndId(contact, addressId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> list(User user, String contactId){
        Contact contact = contactRepository.findFirstByUserAndId(user, contactId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        List<Address> addresses = addressRepository.findAllByContact(contact);
        return addresses.stream().map(this::toAddressResponse).toList();
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
