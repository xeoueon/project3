package com.example.project.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.project.dto.BlogboardDTO;
import com.example.project.entity.Blogboard;
import com.example.project.service.BlogboardService;

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
	
	@PostMapping("/blogboardWrite")
	public String blogboardWrite(BlogboardDTO dto, Model model,
							@RequestParam("img") MultipartFile uploadFile) {
		System.out.println(dto.toString());
		// 1. 데이터 처리
		// 파일명 저장
		String fileName = uploadFile.getOriginalFilename();
		dto.setBlogimage1(fileName);
		
		// 파일 저장 폴더 만들기
		File folder = new File(uploadpath);
		if(folder.exists()) {		// 폴더가 없으면
			folder.mkdirs();		// 폴더 만들기
		}
		
		// 전달된 파일이 있으면 저장하기
		if(!fileName.equals("")) {
			File file = new File(uploadpath, fileName);
			try {
				uploadFile.transferTo(file);	// 파일 저장하기
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// db
		Blogboard blogboard = service.blogboardWrite(dto);
		
		// 2. 데이터 공유
		model.addAttribute("blogboard", blogboard);
		// 3. view 파일명 리턴
		return "/blogboard/blogboardWrite";
	}
	
	
}
