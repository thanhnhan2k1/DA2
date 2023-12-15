package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.example.model.Contact;

@Controller
@RequestMapping("/contact")
public class ContactController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	
	@GetMapping
	private String getContact(Model model) {
		Contact contact=new Contact();
		model.addAttribute("contact", contact);
		return "contact.html";
	}
	@PostMapping("/save")
	private String saveContact(Contact contact, Model model) {
		contact.setStatus(false);
		Contact contact1=rest.postForObject(url+"/contact/save", contact, Contact.class);
		model.addAttribute("contact", contact);
		if(contact1==null)
			model.addAttribute("result", "fail");
		else
			model.addAttribute("result", "success");
		return "contact.html";
	}
}
