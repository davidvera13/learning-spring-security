package com.bank.app.shared.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class NoticeDto {
    private int noticeId;
    private String noticeSummary;
    private String noticeDetails;
    private Date noticBegDt;
    private Date noticEndDt;
    private Date createDt;
    private Date updateDt;
}
