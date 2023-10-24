package com.bank.app.controller;

import com.bank.app.models.request.ContactRequest;
import com.bank.app.models.response.ContactResponse;
import com.bank.app.services.ContactService;
import com.bank.app.shared.dto.ContactDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    public ContactResponse saveContactInquiryDetails(@RequestBody ContactRequest contact) {
        ModelMapper modelMapper = new ModelMapper();
        ContactDto contactDto = modelMapper.map(contact, ContactDto.class);
        ContactDto responseDto  = contactService.saveContactInquiryDetails(contactDto);
        return modelMapper.map(responseDto, ContactResponse.class);
    }

}