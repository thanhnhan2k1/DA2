package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdentifyWoodHistory {
	private int id;
	private Wood wood;
	private String path;
	private String result;
	private float prob;
}
