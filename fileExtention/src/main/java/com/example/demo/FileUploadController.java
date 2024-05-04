package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/fileUpload")
public class FileUploadController {
	
	@PostMapping("/extention")
	@ResponseBody
	public ResponseEntity<byte[]> fileExtention(@RequestParam(value = "userfile")MultipartFile uploadFile) throws IOException {
		System.out.println("extention 타기");
		
		//파일 종류 체크
		String extension = StringUtils.getFilenameExtension(uploadFile.getOriginalFilename());
		//파일 경로
		
		//if(extension.equals("hwp")) {
			System.out.println("파일 타입 =" + extension);
			String fileName = uploadFile.getOriginalFilename();
			System.out.println(fileName + "파일 이름");
			String newFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".txt";
			System.out.println(newFileName + "변경된 파일 이름");
			
			//File file = new File("C:\\Users\\hwang\\OneDrive\\바탕 화면\\fileExtention_test files\\테스트_한글파일.hwp");
			//String path = "C:\\Users\\hwang\\OneDrive\\바탕 화면\\fileExtention_test files\\fdsafdsa.hwp";
			String path = "C:\\Users\\hwang\\OneDrive\\바탕 화면\\fileExtention_test files\\text.txt";
			
			String encoding = "UTF-8"; // 또는 "CP949"
			//파일이 저장된 경로
			File file = new File(path);
			
            
           
			byte[] result=null;
			
			
			//1. data
			ResponseEntity<byte[]> entity=null;
			
			result = FileCopyUtils.copyToByteArray(file);

			
			//2. header
			HttpHeaders header = new HttpHeaders();
			//다운로드임을 명시
			header.add("Content-Disposition", "attachment; filename="+fileName);
			
			//3. 응답본문
			entity = new ResponseEntity<>(result,header,HttpStatus.OK);//데이터, 헤더, 상태값

			return entity;
			
		//}else {
			//throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "파일 업로드가 실패하였습니다. 관리자에게 문의 바랍니다.");
		//}
	}
	
	@GetMapping("/test/get")
	public String testGet() {
		return "test_form";
	}
}
