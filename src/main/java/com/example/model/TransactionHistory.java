package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionHistory {
	private int id;
	private int amount;
	private String bankCode;
	private String bankTranNo;
	private String orderInfor;
	private String cardType;
	private Date payDate;
	private String responseCode;
	private String tmnCode;
	private String transactionStatus;
	private String transactionNo;
}
