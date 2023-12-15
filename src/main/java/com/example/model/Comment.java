package com.example.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
	private int id;
	private String content;
	private List<Comment> listChildren=new ArrayList<Comment>();
	private Date createAt;
	private User user;
}
