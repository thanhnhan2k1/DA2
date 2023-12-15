package com.example.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlossaryPagination{
	private int totalPages;
	private long totalElements;
	private List<Glossary>content;
}
