package com.example.project.dto;

import java.util.Date;

import com.example.project.entity.Blogboard;

import lombok.Data;

@Data
public class BlogboardDTO {
    private int seq;                  // 글번호
    private String blogid;         	  // 블로그ID
    private String blogname;          // 이름
    private String blogsubject;       // 블로그제목
    private String blogcontent;   	  // 블로그 내용
    private String blogimage1;         // 파일이름
    private int hit;             	  // 조회수
    private Date logtime;   
    
    public Blogboard toEntity() {
    	return new Blogboard(seq, blogid, blogname, blogsubject, blogcontent, blogimage1, hit, logtime);
    }
}
