package com.example.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	
	
	@GetMapping("/main/index")
	public String index(HttpSession session, Model model) {
	    String memId = (String) session.getAttribute("memId");
	    model.addAttribute("memId", memId);
	    return "main/index";
	}
}
