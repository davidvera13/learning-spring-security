package com.bank.app.controller;

import com.bank.app.models.response.NoticeResponse;
import com.bank.app.services.NoticeService;
import com.bank.app.shared.dto.NoticeDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class NoticesController {
    private final NoticeService noticeService;

    @Autowired
    public NoticesController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponse>> getNotices() {
        List<NoticeDto> noticeDtos = noticeService.findAllActiveNotices();
        // we can refresh but entrypoint is not reached if delay of 60s is not completed
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(new ModelMapper().map(noticeDtos, new TypeToken<List<NoticeResponse>>(){}.getType()));
    }

}