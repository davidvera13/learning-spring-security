package com.bank.app.services;

import com.bank.app.shared.dto.NoticeDto;

import java.util.List;

public interface NoticeService {
    List<NoticeDto> findAllActiveNotices();
}
