package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import com.example.model.GlossaryPagination;

@Controller
@RequestMapping("/glossary")
public class GlossaryController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping("/get")
	private String getAll(Model model, @RequestParam(name="pageNum", defaultValue = "1", required = false) int pageNum,
			@RequestParam(name="sortField", defaultValue = "vietnamese", required = false)String sortField,
			@RequestParam(name="sortDir", defaultValue = "asc", required = false)String sortDir,
			@RequestParam(name="key", defaultValue = "", required = false)String key) {
		GlossaryPagination response=rest.getForObject(url+"/glossary/get?pageNum="+pageNum+"&sortField="+sortField+"&sortDir="+sortDir+"&key="+key, GlossaryPagination.class);
		if(response.getContent().size()==0)
		{
			response.setTotalPages(1);
			model.addAttribute("empty", "empty");
		}
		model.addAttribute("gloResponse", response);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("key", key);
		return "glossary.html";
	}
}
