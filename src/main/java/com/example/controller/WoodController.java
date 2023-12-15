package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.AppendixCITES;
import com.example.model.GeographicalArea;
import com.example.model.Glossary;
import com.example.model.IdentifyWoodHistory;
import com.example.model.PlantFamily;
import com.example.model.Preservation;
import com.example.model.RequestImage;
import com.example.model.User;
import com.example.model.Wood;
import com.example.model.WoodPagination;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@Controller
@RequestMapping("/wood")
public class WoodController {
	private RestTemplate rest = new RestTemplate();
	private String url1="http://localhost:8082";
	//private String url1="https://da-server2-production.up.railway.app";
	@GetMapping("/get")
	private String get(
			@RequestParam(name="category", defaultValue = "1", required = false)int category,
			@RequestParam(name = "key", defaultValue = "", required = false) String key,
			@RequestParam(name = "pageNum", defaultValue = "1", required = false) int pageNum,
			@RequestParam(name = "family", required = false) List<String> familys,
			@RequestParam(name = "area", required = false) List<String> areas,
			@RequestParam(name = "color", required = false) List<String> colors,
			@RequestParam(name = "cites", required = false) List<String> cites,
			@RequestParam(name = "sort", required = false) String sort,
//			@RequestParam(name="from", required = false, defaultValue = "")String from,
//			@RequestParam(name="to", required = false, defaultValue = "")String to,
			@RequestParam(name = "preservation", required = false) List<String> preservations, HttpSession session,
			Model model, HttpServletRequest request) {
		User user=(User) session.getAttribute("user");
		if(user==null)
			return "redirect:/user/login";
		// lay thon tin duong dan hien tai
		Object categoryPre=session.getAttribute("categoryPre");
		if(categoryPre!=null) {
			int catePre=(int)categoryPre;
			if(catePre!=category) {
				session.setAttribute("mapFamily", null);
				session.setAttribute("mapArea", null);
				session.setAttribute("mapColor", null);
			}
		}
		session.setAttribute("categoryPre", category);
		String urlCur=request.getRequestURL().toString()+"?";
		String query=request.getQueryString();
		session.setAttribute("urlCur", urlCur+query);// dung de luu trang thai thich
		if(query!=null) {
			if(query.contains("pageNum=")==false)
			{
				urlCur=urlCur+query+"&";
				session.setAttribute("url", urlCur);
			}
		}else
			session.setAttribute("url", urlCur);
		// lay thong tin key
		String url = url1+"/wood/get?pageNum=" + pageNum+"&category="+category;
		if (!key.isEmpty()) {
			url = url + "&key=" + key;
		}
		session.setAttribute("key", key);
		// lay thon tin sap xep
		Map<String, Boolean> mapSort = (Map<String, Boolean>) session.getAttribute("mapSort");
		List<String> listSort;
		if (mapSort == null) {
			listSort = new ArrayList<String>();
			listSort.add("Tên khoa học");
			listSort.add("Tên thương mại");
			mapSort = new LinkedHashMap<String, Boolean>();
		} else {
			listSort = new ArrayList<>(mapSort.keySet());
		}
		if (sort == null) {
			mapSort.put(listSort.get(0), true);
			mapSort.put(listSort.get(1), true);
			for (int i = 1; i < listSort.size(); i++)
				mapSort.put(listSort.get(i), false);
		} else {
			for (String i : listSort) {
				if (i.equalsIgnoreCase(sort)) {
					mapSort.put(i, true);
					url=url+"&sort="+i;
				} else
					mapSort.put(i, false);
			}
		}
		session.setAttribute("mapSort", mapSort);

		// lay thong tin family
		List<PlantFamily> listFamily;
		Map<PlantFamily, Boolean> mapFamily = (Map<PlantFamily, Boolean>) session.getAttribute("mapFamily");
		if (mapFamily == null) {
			listFamily = Arrays.asList(rest.getForObject(url1+"/plantfamily/getByCategory?category="+category, PlantFamily[].class));
			mapFamily = new LinkedHashMap<PlantFamily, Boolean>();
		} else
			listFamily = (List<PlantFamily>) mapFamily.keySet();

		if (familys == null)
			for (PlantFamily i : listFamily)
				mapFamily.put(i, false);
		else
			for (PlantFamily i : listFamily) {
				if (familys.contains(i.getEnglish())) {
					mapFamily.put(i, true);
					url = url + "&family=" + i.getEnglish();
				} else
					mapFamily.put(i, false);
			}

		session.setAttribute("mapfamily", mapFamily);

		// lay thon tin vi tri dia ly
		Map<GeographicalArea, Boolean> mapArea = (Map<GeographicalArea, Boolean>) session.getAttribute("mapArea");
		List<GeographicalArea> listAreas = new ArrayList<>();
		if (mapArea == null) {
			listAreas = Arrays.asList(rest.getForObject(url1+"/area/getByCategoryWood?category="+category, GeographicalArea[].class));
			mapArea = new LinkedHashMap<GeographicalArea, Boolean>();
		} else
			listAreas = new ArrayList<>(mapArea.keySet());

		if (areas == null)
			for (GeographicalArea i : listAreas)
				mapArea.put(i, false);
		else
			for (GeographicalArea i : listAreas) {
				if (areas.contains(i.getEnglish())) {
					mapArea.put(i, true);
					url = url + "&area=" + i.getEnglish();
				} else
					mapArea.put(i, false);
			}
		session.setAttribute("mapArea", mapArea);
		// lay danh sach thong tin cites
		Map<AppendixCITES, Boolean> mapCites = (Map<AppendixCITES, Boolean>) session.getAttribute("mapCites");
		List<AppendixCITES> listCites = new ArrayList<>();
		if (mapCites == null) {
			listCites = Arrays.asList(rest.getForObject(url1+"/cites/get", AppendixCITES[].class));
			mapCites = new LinkedHashMap<AppendixCITES, Boolean>();
		} else
			listCites = new ArrayList<>(mapCites.keySet());

		if (cites == null)
			for (AppendixCITES i : listCites)
				mapCites.put(i, false);
		else
			for (AppendixCITES i : listCites) {
				if (cites.contains(i.getName())) {
					mapCites.put(i, true);
					url = url + "&cites=" + i.getName();
				} else
					mapCites.put(i, false);
			}
		session.setAttribute("mapCites", mapCites);
		// lay thon tin ve sự bảo tồn(preservation)
		Map<Preservation, Boolean> mapPre = (Map<Preservation, Boolean>) session.getAttribute("mapPre");
		List<Preservation> listPre;
		if (mapPre == null) {
			listPre = Arrays.asList(rest.getForObject(url1+"/preservation/get", Preservation[].class));
			mapPre = new LinkedHashMap<Preservation, Boolean>();
		} else
			listPre = new ArrayList<>(mapPre.keySet());

		if (preservations == null)
			for (Preservation i : listPre)
				mapPre.put(i, false);
		else
			for (Preservation i : listPre) {
				if (preservations.contains(i.getAcronym())) {
					mapPre.put(i, true);
					url = url + "&preservation=" + i.getAcronym();
				} else
					mapPre.put(i, false);
			}
		session.setAttribute("mapPre", mapPre);
		// lấy thông tin dựa vào color
		
		Map<String, Boolean> mapColor = (Map<String, Boolean>) session.getAttribute("mapColor");
		List<String> listColor;
		if (mapColor == null) {
			listColor = new ArrayList<String>();
			if(category==1) {
				listColor.add("Đen");
				listColor.add("Xanh");
				listColor.add("Đỏ");
				listColor.add("Hồng");
				listColor.add("Vàng");
			}else {
				listColor.add("brown");
				listColor.add("red");
				listColor.add("yellow");
				listColor.add("white or grey");
				listColor.add("black");
				listColor.add("purple");
				listColor.add("green");
			}
			mapColor = new LinkedHashMap<String, Boolean>();
		} else
			listColor = new ArrayList<>(mapColor.keySet());

		if (colors == null)
			for (String i : listColor)
				mapColor.put(i, false);
		else
			for (String i : listColor) {
				if (colors.contains(i)) {
					mapColor.put(i, true);
					url = url + "&color=" + i;
				} else
					mapColor.put(i, false);
			}
		session.setAttribute("mapColor", mapColor);
		System.out.println(url);
		WoodPagination woodpag = rest.getForObject(url, WoodPagination.class);
		System.out.println(woodpag);
		session.setAttribute("woodpag", woodpag);
		
		List<Wood>listFav=user.getListFavouriteWood();
		Map<Wood, Boolean>mapWood=new LinkedHashMap<>();
		for(Wood wood:woodpag.getContent())
			if(listFav.contains(wood))
				mapWood.put(wood,true);
			else
				mapWood.put(wood,false);
		session.setAttribute("mapWood",mapWood);
		model.addAttribute("currentPage", pageNum);
		//model.addAttribute("totalPages", woodpag.getTotalPages());
		return "wood400Species.html";
	}

	@GetMapping("/detail-400-species")
	private String getDetailWood400Species(@RequestParam("index") int index, HttpSession session, Model model) {
		WoodPagination woodpag = (WoodPagination) session.getAttribute("woodpag");
		Wood wood = woodpag.getContent().get(index);
		String distribute = "";
		for (GeographicalArea i : wood.getListAreas())
			distribute += i.getEnglish() + ", ";
		distribute = distribute.substring(0, distribute.length() - 2);
		model.addAttribute("distribute", distribute);
		session.setAttribute("wood", wood);
		return "woodDetail400Species.html";
	}
	@GetMapping("/detail")
	private String getDetail(HttpSession session, @RequestParam(name="id")int id) {
		User user=(User) session.getAttribute("user");
		List<IdentifyWoodHistory>list=user.getListIdentify();
		for(IdentifyWoodHistory i:list) {
			if(i.getId()==id)
			{
				session.setAttribute("wood",i.getWood());
				break;
			}
		}
		return "woodDetail400Species.html";
	}
	@GetMapping("/detailWoodFromSearchQRCode")
	private String detailWoodFromSearchQRCode() {
		return "woodDetail400Species.html";
	}
	@PostMapping("/identification")
	private String identification(@RequestParam (name="img")MultipartFile[] files, Model model, HttpSession session) throws IOException {
		//System.out.println(Base64.encodeBase64String(file.getBytes()));
		User user=(User) session.getAttribute("user");
		MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);// Main request's headers
		for(MultipartFile file: files) {
		HttpHeaders requestHeadersAttachment = new HttpHeaders();
		HttpEntity<ByteArrayResource> attachmentPart;
		ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
			@Override
			public String getFilename() {
				return file.getOriginalFilename();
			}
		};
		attachmentPart = new HttpEntity<>(fileAsResource, requestHeadersAttachment);

		multipartRequest.add("file", attachmentPart);
		}

		HttpHeaders requestHeadersJSON = new HttpHeaders();
		requestHeadersJSON.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<User> requestEntityJSON = new HttpEntity<>(user, requestHeadersJSON);
		multipartRequest.add("user", requestEntityJSON);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(multipartRequest, requestHeaders);// final
																														// request
		ResponseEntity<User> response = rest.postForEntity(url1 +"/user/addIdentify", requestEntity,
				User.class);
		List<IdentifyWoodHistory>list=response.getBody().getListIdentify();
		List<IdentifyWoodHistory>listOut=new ArrayList<>();
		for(int i=list.size()-files.length;i<list.size();i++)
			listOut.add(list.get(i));
		session.setAttribute("listResult", listOut);
		session.setAttribute("user", response.getBody());
		model.addAttribute("size", list.size()-1);
//		if (response.getBody() == null)
//			model.addAttribute("result", "fail");
//		else
//			model.addAttribute("result", "success");
		return "identificationWood.html";
	}
	@GetMapping("/getsearchByQRCode")
	private String getSearchByQRCode() {
		return "searchWoodByQRCode.html";
	}
	@GetMapping("/searchByQRCode")
	private String searchByQRCode(@RequestParam(name="simg")String simg, Model model, HttpSession session) {
		RequestImage requestImage=new RequestImage(simg);
		Wood wood=rest.postForObject(url1+"/wood/readQRCode", requestImage, Wood.class);
		session.setAttribute("src", simg);
		if(wood==null)
		{	session.setAttribute("result", "Không tìm được gỗ dựa trên mã QR"); 
			return "searchWoodByQRCode.html";
		}
		else
		{
			System.out.println(wood);
			if(wood.getVietnameName()!=null)
				session.setAttribute("result1", wood.getVietnameName());
			else
				session.setAttribute("result1", wood.getScientificName());
			session.setAttribute("wood1", wood);
			session.setAttribute("wood", wood);
			return "searchWoodByQRCode.html";
		}
	}
	@GetMapping("/getWoodByScanCamera")
	private String getWoodByScanCamera(@RequestParam("id")int id, Model model, HttpSession session) {
		Wood wood=rest.getForObject(url1+"/wood/getById?id="+id, Wood.class);
		if(wood!=null) {
			session.setAttribute("wood2", wood);
			session.setAttribute("wood", wood);
			if(wood.getVietnameName()!=null)
				session.setAttribute("result2", wood.getVietnameName());
			else
				session.setAttribute("result2", wood.getScientificName());
			return "searchWoodByQRCode.html";
		}else {
			session.setAttribute("result2", "Không tìm được gỗ dựa trên mã QR"); 
			return "searchWoodByQRCode.html";
		}
	}
}

