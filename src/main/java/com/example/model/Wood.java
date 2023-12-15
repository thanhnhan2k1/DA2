package com.example.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Wood {
	private int id;
	private String vietnameName;
	private String scientificName;
	private String commercialName;
	private Preservation preservation;
	private PlantFamily family;
	private CategoryWood categoryWood;
	private List<Image>listImage=new ArrayList<>();
	private int specificGravity;
	private String characteristic;
	private String note;
	private AppendixCITES appendixCites;
	private List<GeographicalArea>listAreas=new ArrayList<GeographicalArea>();
	private String color;
}
