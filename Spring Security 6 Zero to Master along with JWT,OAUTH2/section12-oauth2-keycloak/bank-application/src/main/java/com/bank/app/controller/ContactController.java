package com.bank.app.controller;

import com.bank.app.models.request.ContactRequest;
import com.bank.app.models.response.ContactResponse;
import com.bank.app.services.ContactService;
import com.bank.app.shared.dto.ContactDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // we must pass input as list
    // if contactName equals test, list size = 0
    @PostMapping("/contact")
    @PreFilter("filterObject.contactName != 'test'")
    //@PostFilter("filterObject.contactName != 'test'")
    public List<ContactResponse> saveContactInquiryDetails(@RequestBody List<ContactRequest> contacts) {
        ContactRequest contact = contacts.get(0);
        ModelMapper modelMapper = new ModelMapper();


        ContactDto contactDto = modelMapper.map(contact, ContactDto.class);
        ContactDto responseDto  = contactService.saveContactInquiryDetails(contactDto);
        return List.of(modelMapper.map(responseDto, ContactResponse.class));
    }

}