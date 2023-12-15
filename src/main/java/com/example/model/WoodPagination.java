package com.example.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WoodPagination {
	private int totalPages;
	private long totalElements;
	private List<Wood>content; 
}
