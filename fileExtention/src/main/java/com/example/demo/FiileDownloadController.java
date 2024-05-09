package com.example.demo;

import java.io.ByteArrayOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/fileDownload")
public class FiileDownloadController {
	
	
	@PostMapping("/pdf")
	@ResponseBody
	public ResponseEntity<byte[]> pdfChange(@RequestParam(value = "userfile")MultipartFile uploadFile) throws Exception{
		 //Loading an existing document
		System.out.println("/pdf/change 타기");
		try (PDDocument document = PDDocument.load(uploadFile.getInputStream())) {
	        PDPage page = document.getPage(0);
	        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	            contentStream.beginText();
	            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
	            contentStream.newLineAtOffset(25, 500);
	            contentStream.showText("This is the sample document and we are adding content to it.");
	            contentStream.endText();
	        }

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        document.save(outputStream);
	        byte[] pdfBytes = outputStream.toByteArray();

	        return ResponseEntity.ok()
	                .contentType(MediaType.APPLICATION_PDF)
	                .body(pdfBytes);
	    }
	   }
	}


