package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.object.bodytext.BodyText;
import kr.dogfoot.hwplib.object.bodytext.Section;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.ParagraphList;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.reader.bodytext.ForParagraphList;

@Controller
@RequestMapping("/fileDownload")
public class FiileDownloadController {
	
	
	@PostMapping("/pdf")
	@ResponseBody
	public ResponseEntity<byte[]> pdfChange(@RequestParam(value = "userfile")MultipartFile uploadFile) throws Exception{
		 //Loading an existing document
		System.out.println("/pdf/change 타기");
		try {
            // Load the HWP file
			//MultipartFile을 임시File로 변환
	        File tempFile = File.createTempFile("temp-file-name", null);
	        uploadFile.transferTo(tempFile);

	        // File을 HWPFile로 변환
	        HWPFile hwpFile = HWPReader.fromFile(tempFile);

            // Create a new PDDocument
            PDDocument pdfDocument = new PDDocument();

            // ParagraphList 객체 생성
            Section paragraphList = hwpFile.getBodyText().getLastSection();
            
            // Add each paragraph to the PDDocument
            PDPage page = new PDPage();
            pdfDocument.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page);
            float yPosition = 700; // 초기 y 위치 설정
            for (Paragraph paragraph : paragraphList) {
            	
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(paragraph.getText().getNormalString(0));
                System.out.println("paragraph.getText() = "+paragraph.getText().getNormalString(0));
                contentStream.endText();
                yPosition -= 15; // 다음 문단을 위해 y 위치 조정
            }

            //paragraphList.addNewParagraph(); // 문단 추가
            contentStream.close();
            // Save the PDDocument
            pdfDocument.save("C:\\Users\\hwang\\OneDrive\\바탕 화면\\fileExtention_test files\\text.pdf");
            pdfDocument.close();
            // 임시 파일 삭제
	        tempFile.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	   }

	
	}


