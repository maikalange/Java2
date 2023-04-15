/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.cms;
//import com.spire.pdf.*;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

/**
 *
 * @author jnj
 */
public class PDFToWordConverter {

    private static void convertPDFToWordDocument(String sourcePdfFile, String outputWordDocFile) throws IOException {
        XWPFDocument doc = new XWPFDocument();
        
        PdfReader reader = new PdfReader(sourcePdfFile);
        var parser = new PdfReaderContentParser(reader);

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {            
            TextExtractionStrategy strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
            
            String text = strategy.getResultantText();
            System.out.println(text);
            Scanner sc  = new Scanner(text);
            while(sc.hasNext()){
                
            }
            var formattedText  = text.replace("\n", "<w:br/>");
            XWPFParagraph p = doc.createParagraph();

            XWPFRun run = p.createRun();
            
            run.setText(formattedText);
            run.addBreak(BreakType.PAGE);
        }
        FileOutputStream out = new FileOutputStream(outputWordDocFile);
        doc.write(out);
        doc.close();
    }

    public static void main(String[] args) throws IOException {
        //create a PdfDocument object
        
//        PdfDocument doc = new PdfDocument();
//        var baseDir = "C:\\Users\\jnj\\Documents\\Laws Zambia\\toc\\";
//        var files = TextExtractor.getFilesByTypeInDir(baseDir, "pdf");
//        //load a sample PDF file
//        for (var file : files) {
//            //doc.loadFromFile(baseDir + file);
//            convertPDFToWordDocument(baseDir + file,"WordDocumentsActs\\" + file + ".docx");
//            //doc.saveToFile("WordDocumentsActs\\" + file + ".docx", FileFormat.DOCX);
//            //save as. docx file
//            //doc.close();
//        }
        
        //save as .doc file
    }
}
