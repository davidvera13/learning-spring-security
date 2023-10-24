package com.bank.app.services;

import com.bank.app.shared.dto.ContactDto;

public interface ContactService {
    ContactDto saveContactInquiryDetails(ContactDto contactDto);
}
