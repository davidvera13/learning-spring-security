package com.bank.app.services.impl;

import com.bank.app.io.repository.NoticeRepository;
import com.bank.app.services.NoticeService;
import com.bank.app.shared.dto.NoticeDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeRepository noticeRepository;

    @Autowired
    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public List<NoticeDto> findAllActiveNotices() {
        return new ModelMapper().map(noticeRepository.findAllActiveNotices(), new TypeToken<List<NoticeDto>>(){}.getType());
    }
}
