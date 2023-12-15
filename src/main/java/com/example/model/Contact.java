package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
	private int id;
	private String name;
	private String email;
	private String subject;
	private String content;
	private boolean status=false;
}
