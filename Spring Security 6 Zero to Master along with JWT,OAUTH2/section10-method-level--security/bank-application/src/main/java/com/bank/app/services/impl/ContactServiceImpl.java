package com.bank.app.services.impl;

import com.bank.app.io.entity.ContactEntity;
import com.bank.app.io.repository.ContactRepository;
import com.bank.app.services.ContactService;
import com.bank.app.shared.dto.ContactDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public ContactDto saveContactInquiryDetails(ContactDto contactDto) {
        ContactEntity contactEntity = new ModelMapper().map(contactDto, ContactEntity.class);
        contactEntity.setContactId(getServiceReqNumber());
        contactEntity.setCreateDt(new Date(System.currentTimeMillis()));
        ContactEntity storedContactEntity = contactRepository.save(contactEntity);
        return new ModelMapper().map(storedContactEntity, ContactDto.class);
    }

    private String getServiceReqNumber() {
        Random random = new Random();
        int ranNum = random.nextInt(999999999 - 9999) + 9999;
        return "SR"+ranNum;
    }
}
