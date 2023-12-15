package com.example.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedService {
	private int id;
	private Date dateStart;
	private Date dateEnd;
	private String typePayment;
	private Service service;
	private User user;
	private String status;
	private TransactionHistory tranHistory;
	private Voucher voucher;
	private int totalMoney;
}
