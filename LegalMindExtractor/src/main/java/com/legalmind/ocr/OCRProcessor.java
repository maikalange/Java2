/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.ocr;

import com.legalmind.napita.TextExtractor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 *
 * @author jnj
 */
public class OCRProcessor {

    public static void main(String[] args) throws IOException, Exception {
        var dirPath = "C:\\Users\\jnj\\Documents\\NetBeansProjects\\LegalMindScraper\\Judgements\\";
        //Get all files in the judgements directory
        String[] files = TextExtractor.getFilesByTypeInDir(dirPath, "pdf");
        for (var file : files) {
            run(dirPath, file);
        }

    }

    private static void run(String dir, String pdfFile) throws Exception {
        try ( PDDocument document = PDDocument.load(new File(dir + pdfFile))) {
            System.out.println("Processing file " + pdfFile);
            String text = extractTextFromScannedDocument(document);
            try ( FileWriter sw = new FileWriter("OCR_output\\" + pdfFile.replace("pdf", "txt"))) {
                sw.write(text);
            } catch (Exception e) {
                System.out.println("Error processing " + pdfFile);
            }
        } catch (Exception e) {
            System.out.println("Error processing " + pdfFile);
        }
    }

    private static String extractTextFromScannedDocument(PDDocument document) throws IOException, TesseractException {
        // Extract images from file
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        StringBuilder out = new StringBuilder();

        var tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Users\\jnj\\Documents\\Laws Zambia\\OCR\\tessdata");
        tesseract.setLanguage("eng");

        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

            // Create a temp image file
            File tempFile = File.createTempFile("tempfile_" + page, ".png");
            ImageIO.write(bufferedImage, "png", tempFile);

            String result = tesseract.doOCR(tempFile);
            out.append(result);

            // Delete temp file
            tempFile.delete();
        }
        return out.toString();
    }
}
