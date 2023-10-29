package com.bank.app.models.response;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class NoticeResponse {
	private int noticeId;
	private String noticeSummary;
	private String noticeDetails;
	private Date noticBegDt;
	private Date noticEndDt;
	private Date createDt;
	private Date updateDt;
}
