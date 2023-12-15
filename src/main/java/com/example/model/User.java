package com.example.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	private int id;
	private String name;
	private String email;
	private String phone;
	private String address;
	private String provider;
	private Date createAt;
	private Date createUpdate;
	private String password;
	private String role;
	private boolean enabled;
	private List<Wood>listFavouriteWood=new ArrayList<>();
	private List<IdentifyWoodHistory>listIdentify=new ArrayList<>();
//	private	String token;
}
