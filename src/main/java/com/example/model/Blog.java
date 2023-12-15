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
public class Blog {
	private int id;
	private String title;
	private String content;
	private String author;
	private Date dateUpdate;
	private String image;
	private String precontent;
	private CategoryBlog categoryBlog;
	private String status;
	private List<Comment> listComment=new ArrayList<>();
}
