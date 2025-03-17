package com.example.project.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.dto.BlogboardDTO;
import com.example.project.entity.Blogboard;
import com.example.project.service.BlogboardService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BlogboardController {
	@Autowired
	BlogboardService service;
	
	@Value("${project.upload.path}")
	private String uploadpath;
	
	// http://localhost:8080/blogboard/blogboardWriteForm
	@GetMapping("/blogboardWriteForm")
	public String imageboardWriteForm() {
		return "/blogboard/blogboardWriteForm";
	}
	
	@PostMapping("/blogboard/uploadImage")
	@ResponseBody
	public Map<String, String> uploadImage(@RequestParam("img") MultipartFile uploadFile) {
	    Map<String, String> response = new HashMap<>();

	    if (!uploadFile.isEmpty()) {
	        try {
	            // 파일명 생성 (UUID 적용)
	            String originalFileName = uploadFile.getOriginalFilename();
	            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
	            String newFileName = UUID.randomUUID().toString() + fileExtension;

	            // 저장할 경로
	            File file = new File(uploadpath, newFileName);
	            uploadFile.transferTo(file);

	            // 응답 데이터 설정
	            response.put("url", "/storage/" + newFileName);  // 웹에서 접근할 URL
	            response.put("fileName", newFileName);  // 대표사진 저장용 파일명
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return response;
	}

	
	@PostMapping("/blogboardWrite")
	public String blogboardWrite(BlogboardDTO dto, Model model) {
	    System.out.println(dto.toString());

	    // DB 저장
	    Blogboard blogboard = service.blogboardWrite(dto);

	    // 대표사진이 없는 경우, 본문에서 첫 번째 이미지를 찾아 자동 설정 가능
	    if (dto.getBlogimage1() == null || dto.getBlogimage1().isEmpty()) {
	        String firstImage = extractFirstImage(dto.getBlogcontent());
	        dto.setBlogimage1(firstImage);
	    }

	    // 데이터 공유
	    model.addAttribute("blogboard", blogboard);
	    return "/blogboard/blogboardWrite";
	}

	// 본문에서 첫 번째 이미지 URL 추출하는 메서드
	private String extractFirstImage(String content) {
	    Pattern pattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']");
	    Matcher matcher = pattern.matcher(content);
	    return matcher.find() ? matcher.group(1) : null;
	}

	// http://localhost:8080/blogboard/blogboardList
	@GetMapping("/blogboardList")
	public String blogboardList(Model model, HttpServletRequest request) {
		// 1. 데이터 처리
		// 1) pg 저장
		int pg = 1;
		if(request.getParameter("pg") != null) {
			pg = Integer.parseInt(request.getParameter("pg"));
		}

		// 2. 데이터 공유
		// 세션 데이터 공유
		HttpSession session = request.getSession();
		model.addAttribute("personId", session.getAttribute("personId"));
		model.addAttribute("pg", pg);
		
		// 3. view 파일명 리턴
		return "/blogboard/blogboardList";
	}
}
