package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;
import kr.dogfoot.hwplib.tool.textextractor.TextExtractMethod;
import kr.dogfoot.hwplib.tool.textextractor.TextExtractor;

@Controller
@RequestMapping("/fileUpload")
public class FileUploadController {
	
	@PostMapping("/extention")
	@ResponseBody
	public ResponseEntity<byte[]> fileExtention(@RequestParam(value = "userfile")MultipartFile uploadFile) throws Exception {
		System.out.println("extention 타기");
		
		//파일 종류 체크
		String extension = StringUtils.getFilenameExtension(uploadFile.getOriginalFilename());
		System.out.println("파일 타입 =" + extension);
		String fileName = uploadFile.getOriginalFilename();
		System.out.println(fileName + "파일 이름");
	        
        //MultipartFile을 임시File로 변환
        File tempFile = File.createTempFile("temp-file-name", null);
        uploadFile.transferTo(tempFile);

        // 임시File을 HWPFile로 변환
        HWPFile hwpFile = HWPReader.fromFile(tempFile);
        
        byte[] pdfBytes = null;
       
        // HWPfile text 가져오기
        String hwpText = TextExtractor.extract(hwpFile, TextExtractMethod.InsertControlTextBetweenParagraphText);
        System.out.println("hwpText =" + hwpText);

        //PDF 문서 생성
        PDDocument doc = new PDDocument();
        //1. data
		ResponseEntity<byte[]> entity = null;
		//2. header
		HttpHeaders header = new HttpHeaders();
		
        
		//페이지에 내용 추가
		PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true);
		
        //PDF 파일을 byte 배열로 저장
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        	try {
    			
    	        
            	//Content Stream 시작
                contentStream.beginText();

                //HWP 파일의 폰트를 PDF 문서에 적용
                PDType0Font font = PDType0Font.load(doc, tempFile);
                //contentStream.setFont(PDType1Font.HELVETICA, 16);

                //텍스트를 추가할 위치를 설정
                contentStream.newLineAtOffset(25, 500);
                String text = hwpText;

                //HWP 파일의 텍스트를 PDF 문서에 추가
                contentStream.showText(text);
                
                //Content Stream을 종료
    	        contentStream.endText();
    	        System.out.println("Content added");
    	        System.out.println("hwpText =" + hwpText);

    	        
    	        

    	      //Content Stream 종료
    	        contentStream.close();
    	        System.out.println("Content added");
    	        System.out.println("hwpText =" + hwpText);
    			
    	        //PDF 문서를 ByteArrayOutputStream에 저장
    	        doc.save(baos);
    	        doc.close();
    	        //PDF 파일의 byte 배열을 가져온다
    	        pdfBytes = baos.toByteArray();
    	        
    	        // Close the ByteArrayOutputStream
    	        baos.close();
    	        
    	        // 임시 파일 삭제
    	        tempFile.delete();
    	        
    	        
    	        //다운로드임을 명시
    			header.add("Content-Disposition", "attachment; filename="+fileName);
    			
    			//3. 응답본문
    			entity = new ResponseEntity<>(pdfBytes,header,HttpStatus.OK);//데이터, 헤더, 상태값

    			return entity;
    		} catch (EOFException e) { //더이상 입력할 stream이 없는경우
    			
    			e.printStackTrace();
    	        //Content Stream 종료
    	        contentStream.close();
    	        System.out.println("Content added");
    	        System.out.println("hwpText =" + hwpText);
    			
    	        //PDF 문서를 ByteArrayOutputStream에 저장
    	        doc.save(baos);
    	        
    	        //PDF 파일의 byte 배열을 가져온다
    	        pdfBytes = baos.toByteArray();
    	        
    	        // Close the ByteArrayOutputStream
    	        baos.close();
    	        
    	        // 임시 파일 삭제
    	        tempFile.delete();
    	        
    	        
    	        //다운로드임을 명시
    			header.add("Content-Disposition", "attachment; filename="+fileName);
    			
    			//3. 응답본문
    			 entity = new ResponseEntity<>(pdfBytes,header,HttpStatus.OK);//데이터, 헤더, 상태값

    			return entity;
    		}
		
        
        
		
	}
	
	@GetMapping("/test/get")
	public String testGet() {
		return "test_form";
	}
}
