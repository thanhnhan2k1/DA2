package com.example.controller;


import java.io.IOException;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.VoiceStatus;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.model.IdentifyWoodHistory;
import com.example.model.User;
import com.example.model.UserDTO;
import com.example.model.Wood;
import com.example.model.WoodPagination;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@Controller
@RequestMapping("/user")
public class UserController {
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	private RestTemplate rest=new RestTemplate();
	@GetMapping("/register")
	private String register(Model model){
		UserDTO userDTO=new UserDTO();
		model.addAttribute("user", userDTO);
		return "register.html";
	}
	@PostMapping("/register")
	private String checkRegister(UserDTO userDto, Model model) {
		ResponseEntity<String> response=rest.postForEntity(url+"/user/register", userDto,String.class);
		String urlPage="";
		if(response.getStatusCode().equals(HttpStatus.IM_USED))
		{
			model.addAttribute("error", "fail");
			urlPage="register.html";
		}
		else if(response.getStatusCode().equals(HttpStatus.OK))
		{
			urlPage="login.html";
		}
		model.addAttribute("user", userDto);
		return urlPage;
	}
	@GetMapping("/login")
	private String getlogin(Model model) {
		UserDTO userDto=new UserDTO();
		model.addAttribute("user", userDto);
		return "login.html";
	}
	@PostMapping("/login")
	private String checkLogin(UserDTO user,@RequestParam(required = false,name="rememberMe")String remember, Model model, HttpSession session,
			HttpServletResponse response) throws IOException {
		try {
			String token=rest.postForObject(url+"/user/login", user, String.class);
//			if(remember!=null)
//			{
				// luu thong tin vao cookies
				Cookie ctoken=new Cookie("token", token);
				ctoken.setMaxAge(24*365*2*60*60);
				ctoken.setPath("/");
				response.addCookie(ctoken);
//			}
			//lay thong tin ng dung
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer "+token);
			ResponseEntity<User> u=rest.exchange(url+"/user/getUserInfo",HttpMethod.GET, new HttpEntity<Void>(headers), User.class);
			session.setAttribute("user", u.getBody());
			session.setAttribute("token", token);
			return "redirect:/";
		}catch(HttpStatusCodeException e) {
			model.addAttribute("error", "fail");
			model.addAttribute("user", user);
			return "login.html";
		}
	}
	@GetMapping("/getGoogleInfor")
	private String getGoogleInfor(@RequestParam("token")String token, HttpServletResponse response, HttpSession session) {
		// luu thong tin vao cookies
		Cookie ctoken=new Cookie("token", token);
		ctoken.setMaxAge(24*365*2*60*60);
		ctoken.setPath("/");
		response.addCookie(ctoken);
		//lay thong tin ng dung
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer "+token);
		ResponseEntity<User> u=rest.exchange(url+"/user/getUserInfo",HttpMethod.GET, new HttpEntity<Void>(headers), User.class);
		session.setAttribute("user", u.getBody());
		System.out.println(u.getBody().getEmail());
		return "redirect:/";
	}
	@GetMapping("/logout")
	private String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
		
		Cookie[] cookies=request.getCookies();
		for(Cookie cookie:cookies)
			if(cookie.getName().equals("token"))
			{
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
		session.setAttribute("user", null);
		return "index.html";
	}
	
	@GetMapping("/resetPassword")
	private String getPassword(HttpSession session, Model model) {
//		String token=(String)session.getAttribute("token");
		User user=(User)session.getAttribute("user");
//		System.out.println(user.getEmail());
//		HttpHeaders headers = new HttpHeaders();
//		headers.set("Authorization", "Bearer "+token);
//		headers.set("Content-Type", "application/json");
//		HttpEntity<User> entity=new HttpEntity<User>(user, headers);
//		ResponseEntity<String> u=rest.exchange(url+"/user/resetPassword",HttpMethod.POST, entity, String.class);
		String u=rest.postForObject(url+"/user/resetPassword",user , String.class);
		String result=u;
		model.addAttribute("result", result);
		return "resetPassword.html";
	}
	@PostMapping("/resetPassword")
	private String savePassword(@RequestParam("pass")String token, @RequestParam("passwordNew")String passwordNew, 
			@RequestParam("passwordResetNew")String passwordResetNew, Model model) {
		if(passwordNew.equals(passwordResetNew)==false)
			model.addAttribute("result1", "xác nhận password mới sai");
		else {
			String result=rest.getForObject(url+"/user/resetPassword?token="+token+"&password="+passwordNew, String.class);
			model.addAttribute("result1", result);
		}
		return "resetPassword.html";
	}
	@GetMapping("/updateInfor")
	private String getUpdateInfor() {
		return "updateInfor.html";
	}
//	@PostMapping("/updateInfor")
//	private String saveUpdateInfor(HttpSession session, @RequestParam("name")String name, @RequestParam("phone")String phone,
//			@RequestParam("address")String address, Model model, @RequestParam("email")String email) {
//		HttpHeaders headers = new HttpHeaders();
//		String token=(String)session.getAttribute("token");
//		User user=(User)session.getAttribute("user");
//		
//		if(token!=null && user!=null)
//		{
//			user.setAddress(address);
//			user.setName(name);
//			user.setPhone(phone);
//			headers.set("Authorization", "Bearer "+token);
//			HttpEntity<User> entity=new HttpEntity<User>(user, headers);
//			ResponseEntity<User> u=rest.exchange(url+"/user/updateInfor",HttpMethod.POST, entity, User.class);
//			if(u.getBody()==null)
//				model.addAttribute("result","fail");
//			else
//			{
//				model.addAttribute("result", "success");
//				session.setAttribute("user", u.getBody());
//			}
//		}
//		return "updateInfor.html";
//	}
	
	@PostMapping("/updateInfor")
	private String saveUpdateInfor(HttpSession session, @RequestParam("name")String name, @RequestParam("phone")String phone,
			@RequestParam("address")String address, Model model, @RequestParam("email")String email) {
		User user=(User)session.getAttribute("user");
		user.setAddress(address);
		user.setName(name);
		user.setPhone(phone);
		int u=rest.postForObject(url+"/user/updateInfor", user, Integer.class);
		if(u==0) {
			model.addAttribute("result","fail");
		}else {
			model.addAttribute("result", "success");
			session.setAttribute("user", user);
		}
		return "updateInfor.html";
	}
	@GetMapping("/listFavourite")
	private String showListFavouriteWood(HttpServletRequest request, HttpSession session) {
		String urlCur=request.getRequestURL().toString();
		session.setAttribute("urlCur", urlCur);// luu de sau khi luu trang thai thich quay laij trang hien tai
		return "listFavourite.html";
	}
	@GetMapping("/updateListFavourist")
	private String updateListFavourist(@RequestParam("id")int id, @RequestParam("action")String action,HttpSession session) {
		User user=(User) session.getAttribute("user");
		if(action.equalsIgnoreCase("add"))
		{
			WoodPagination woodPagination=(WoodPagination)session.getAttribute("woodpag");
			for(Wood wood:woodPagination.getContent())
				if(wood.getId()==id)
				{
					user.getListFavouriteWood().add(wood);
					break;
				}
		}
		else {
			for(Wood wood:user.getListFavouriteWood()) {
				if(wood.getId()==id) {
					user.getListFavouriteWood().remove(wood);
					break;
				}
			}
		}
		user=rest.postForObject(url+"/user/updateListFavourite", user, User.class);
		session.setAttribute("user", user);
		String urlCur=(String) session.getAttribute("urlCur");
		urlCur=urlCur.substring(url.length());
		System.out.println(urlCur);
		if(user!=null)
			session.setAttribute("result","success");
		return "redirect:"+urlCur;
	}
	@GetMapping("/gallery")
	private String getIdentifyIdentify(HttpSession session) {
		return "listIdentifyWood.html";
	}
	@GetMapping("/deleteIdentifyHistory")
	private String deleteIdentifyHistory(HttpSession session, @RequestParam("index")int index) {
		User user=(User) session.getAttribute("user");
		IdentifyWoodHistory identifyWoodHistory=user.getListIdentify().get(index);
		rest.postForObject(url+"/user/deleteIdentifyHistory",identifyWoodHistory, String.class);
		user.getListIdentify().remove(index);
		session.setAttribute("user", user);
		return "listIdentifyWood.html";
	}
}
