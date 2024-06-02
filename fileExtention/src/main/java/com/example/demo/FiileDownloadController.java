package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import kr.dogfoot.hwplib.object.bodytext.control.ControlSectionDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.CtrlHeaderFooter;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.CtrlHeaderPageNumberPosition;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.CtrlHeaderSectionDefine;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.pagenumberposition.PageNumberPositionHeaderProperty;
import kr.dogfoot.hwplib.object.bodytext.control.ctrlheader.pageoddevenadjust.PageOddEvenAdjustHeaderProperty;
import kr.dogfoot.hwplib.object.bodytext.control.sectiondefine.BatangPageInfo;
import kr.dogfoot.hwplib.object.bodytext.paragraph.Paragraph;
import kr.dogfoot.hwplib.object.bodytext.paragraph.ParagraphList;
import kr.dogfoot.hwplib.object.bodytext.paragraph.lineseg.LineSegItem;
import kr.dogfoot.hwplib.object.bodytext.paragraph.text.ParaText;
import kr.dogfoot.hwplib.object.docinfo.Numbering;
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
		
		
        
		String fileName = uploadFile.getOriginalFilename();
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
            
            //첫 번째 문단인지 여부를 추적
            boolean isFirstParagraph = true;
            
            for (Paragraph paragraph : paragraphList) {
                
                //페이지 갯수 (줄의 세로 위치 파악, 0이면 페이지 추가)
                ArrayList<LineSegItem> segitem = paragraph.getLineSeg().getLineSegItemList();//문단 레이아웃 파악
                int addPage = segitem.get(0).getLineVerticalPosition();//문단 세로위치 파악
                System.out.println("HWP addPage: " + addPage);
                
                if((yPosition <= 50 || addPage == 0) && !isFirstParagraph) {
                	// 이전 contentStream 닫기
                    contentStream.close();
                    
                    // 새 페이지 추가
                    page = new PDPage();
                    pdfDocument.addPage(page);
                    contentStream = new PDPageContentStream(pdfDocument, page);
                    yPosition = 700; // y 위치 초기화
                	
                }
                
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);//텍스트 행간 설정
                contentStream.newLineAtOffset(50, yPosition);
            	contentStream.showText(paragraph.getText().getNormalString(0));//한 문장 입력
            	
            	System.out.println("paragraph.getText() = "+paragraph.getText().getNormalString(0));// enter인식해서 한줄씩 출력
            	contentStream.endText();
                yPosition -= 15; // 다음 문단을 위해 y 위치 조정
               
                isFirstParagraph = false;

            }

            contentStream.close();
            
     
            //PDF 파일을 byte 배열로 저장
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            //PDF 문서를 ByteArrayOutputStream에 저장
            pdfDocument.save(baos);
	        pdfDocument.close();
	        
	        //PDF 파일의 byte 배열을 가져온다
	        byte[] pdfBytes = baos.toByteArray();
	        // Close the ByteArrayOutputStream
	        baos.close();
	        
	        //다운로드임을 명시
	        HttpHeaders header = new HttpHeaders();
	        header.add("Content-Disposition", "attachment; filename="+fileName);
	        
	        //임시 file 삭제
	        tempFile.delete();
	      
	        
	     
	        ResponseEntity<byte[]> entity = new ResponseEntity<>(pdfBytes,header,HttpStatus.OK);//데이터, 헤더, 상태값
	        return entity;
        } catch (IOException e) {
            e.printStackTrace();
        }
		return null;
	   }

	
	}


