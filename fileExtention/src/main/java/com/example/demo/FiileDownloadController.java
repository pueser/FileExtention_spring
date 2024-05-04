package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/fileDownload")
public class FiileDownloadController {
	
	@GetMapping("/pdf/change")
	public String pdfChange(MultipartFile uploadFile) {
		System.out.println("pdf change 타기");
		System.out.println(uploadFile.getOriginalFilename());
		System.out.println(uploadFile.getContentType());
		
		return "test_form";
	}

}
