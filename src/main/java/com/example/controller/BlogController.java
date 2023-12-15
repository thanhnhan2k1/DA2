package com.example.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.example.model.Blog;
import com.example.model.BlogPagination;
import com.example.model.CategoryBlog;
import com.example.model.Comment;
import com.example.model.User;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/blog")
public class BlogController {
	private RestTemplate rest=new RestTemplate();
	private String url="http://localhost:8082";
	//private String url="https://da-server2-production.up.railway.app";
	@GetMapping
	private String getString(HttpSession session,
			@RequestParam(name="pageNum", defaultValue = "1", required = false) int pageNum,
			@RequestParam(name="key", defaultValue = "", required = false)String key,
			@RequestParam(name="idCate", defaultValue = "-1", required = false)int idCate, Model model) {
		List<Blog>listBlog=new ArrayList<Blog>();
		BlogPagination response=rest.getForObject(url+"/blog/get?pageNum="+pageNum+"&key="+key+"&idCate="+idCate, BlogPagination.class);
		session.setAttribute("listBlog", response.getContent());
		List<CategoryBlog>listCate=new ArrayList<>();
		listCate=Arrays.asList(rest.getForObject(url+"/category-blog/getAll", CategoryBlog[].class));
		session.setAttribute("listCate", listCate);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("key", key);
		List<Integer>listNum=new ArrayList<Integer>();
		for(int i=1;i<=response.getTotalPages();i++)
			listNum.add(i);
		model.addAttribute("listNum", listNum);
		String url1="/blog?key="+key;
		if(idCate!=-1)
			url1=url1+"&idCate="+idCate;
		url1=url1+"&pageNum=";
		model.addAttribute("url", url1);
		model.addAttribute("idCate", idCate);
		
		List<Blog>listTop5=new ArrayList<>();
		listTop5=Arrays.asList(rest.getForObject(url+"/blog/getTop5", Blog[].class));
		session.setAttribute("listTop5", listTop5);
		return "blog/blog.html";
	}
	@GetMapping("/detail")
	private String getBlogDetail(HttpSession session,Model model, @RequestParam(name="idBlog")int index) {
		List<Blog>listBlog=(List<Blog>) session.getAttribute("listBlog");
		Blog blog=listBlog.get(index);
		model.addAttribute("blog", blog);
		model.addAttribute("comments",blog.getListComment());
		int sizeComment=0;
		for(Comment c:blog.getListComment())
			sizeComment=sizeComment+1+c.getListChildren().size();
		model.addAttribute("sizeComment", sizeComment);
		return "blog/detailblog.html";
	}
	@PostMapping("/addComment")
	private String addComment(Model model,HttpSession session,@RequestParam(name="idComment", required = false, defaultValue = "0")int idcomment, @RequestParam(name="idBlog")int idblog, @RequestParam(name="content")String content) {
		User user=(User)session.getAttribute("user");
		if(user==null)
			return "redirect:/user/login";
		Comment comment=new Comment();
		comment.setContent(content);
		comment.setUser(user);
		Calendar calendar=Calendar.getInstance();
		Date date=calendar.getTime();
		comment.setCreateAt(date);
		int response=rest.postForObject(url+"/blog/addComment?idComment="+idcomment+"&idBlog="+idblog, comment,Integer.class);
		System.out.println(response);
		
			Blog blog=rest.getForObject(url+"/blog/detailBlog?id="+idblog,Blog.class );
			model.addAttribute("blog",blog );
			model.addAttribute("comments",blog.getListComment());
			int sizeComment=0;
			for(Comment c:blog.getListComment())
				sizeComment=sizeComment+1+c.getListChildren().size();
			model.addAttribute("sizeComment", sizeComment);
		
		return "blog/detailblog.html";
	}
}
