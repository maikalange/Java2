/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.napita;

import com.legalmind.utils.ConfigHelper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.fit.pdfdom.PDFDomTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author jnj
 */
public class TextExtractor {

    private static void generateHTMLFromPDF(String fileName) throws IOException, ParserConfigurationException {
        var sb = new StringBuilder(fileName);
        sb.replace(fileName.indexOf("pdf"), fileName.length(), "");
        try (PDDocument pdf = PDDocument.load(new File(fileName))) {
            try (Writer output = new PrintWriter(sb.toString() + "html", "utf-8")) {
                new PDFDomTree().writeText(pdf, output);
            }
            pdf.close();
        }
    }

    private static void getTOCFromDocument(String textFileName) throws IOException {
        //Get Text Document
        String contents = Files.readString(Paths.get(textFileName));
        if (contents.length() > 0 && contents.indexOf("1.") > 0) {
            var fileNameParts = textFileName.replace("\\", ",").split(",");
            var pragma = fileNameParts[fileNameParts.length - 1].replace("_", " ").replace(".pdf.txt", "");
            var toc = TocExtractor.generateDocumentToc(contents);
            
            //Get Pages with section or part information      
            Document navDoc = Jsoup.parse(getTocHtmlTemplate());
            Element ol = navDoc.getElementsByTag("ol").first();
            Element pre = navDoc.getElementById("pragma");
            Element title = navDoc.getElementsByTag("title").first();

            pre.appendText(pragma.replace("PageNo1", ""));
            title.appendText(textFileName.substring(textFileName.lastIndexOf("\\")).replace(".pdf.txt", "").replace("\\", ""));
            Scanner sc = new Scanner(new StringBuffer(pragma).append(toc).toString());
            String destinationFolder = textFileName.replace(".pdf", "").replace(".txt", "");

            File f = new File(destinationFolder + "\\index.html");
            try (Writer w = new FileWriter(f, Charset.forName("utf-8"))) {
                while (sc.hasNext()) {
                    var x = sc.nextLine().stripLeading();

                    if (x.length() > 1 && Character.isDigit(x.charAt(0))) {
                        String[] s = x.split("\\.");
                        if (s.length == 2) {
                            var pageNo = PageSectionsGenerator.getPageNoForSection(contents, s[1].stripLeading());
                            Element li = new Element("li");
                            Element a = new Element("a");
                            a.appendText(s[1].stripLeading()).attr("href", "#").attr("onclick", MessageFormat.format("loadPage(\"page_{0}.html\",this)", pageNo));

                            li.appendChild(a);
                            ol.appendChild(li);
                        }
                    } else {
                        Element t = new Element("span");
                        t.addClass("h6");
                        var c = x.replace("\n", "").replace("\r", "");
                        t.appendText(c);
                        //ol.appendChild(t);
                    }
                }
                w.write(navDoc.html());
            }
            sc.close();
        }
    }

    private static String getTocHtmlTemplate() throws IOException {
        //Write TOC to file
        var stream = ClassLoader.getSystemResourceAsStream("TocTemplate.html");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder contentBuilder = new StringBuilder();
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            contentBuilder.append(currentLine).append(System.lineSeparator());
        }
        return contentBuilder.toString();
    }
    private static final ConfigHelper propertiesReader = new ConfigHelper("napita.properties");

    public static void main(String args[]) throws IOException, ParserConfigurationException {

        String actspath = propertiesReader.getProperty("actsdir.path");
        String causelistpath = propertiesReader.getProperty("causelistdir.path");

        List.of(getFilesByTypeInDir(actspath, "pdf")).forEach((fileName) -> {
            try {
                String fullPath = actspath + fileName;
                extractPlainTextFromPDF(fullPath, false, false);
                generateHTMLFromPDF(fullPath);
                getTOCFromDocument(fullPath.replace(".pdf", "") + ".txt");
            } catch (IOException | ParserConfigurationException ex) {
                Logger.getLogger(TextExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        List.of(getFilesByTypeInDir(causelistpath, "pdf")).forEach(fileName -> {
            try {
                extractPlainTextFromPDF(causelistpath + fileName, false, true);
            } catch (IOException ex) {
                Logger.getLogger(TextExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        List.of(getFilesByTypeInDir(actspath, "html")).forEach(filePath -> {
            try {
                HtmlPageSplitter.splitHtmlDocument(filePath, actspath);
            } catch (IOException ex) {
                Logger.getLogger(TextExtractor.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        AlphabeticalIndexGenerator.createAlphabeticalIndexOfActs(actspath);
    }

    public static String[] getFilesByTypeInDir(String dirPath, String fileType) {
        File directoryPath = new File(dirPath);
        FilenameFilter fileFilter = (File dir, String name) -> {
            return name.toLowerCase().endsWith("." + fileType);
        };
        return directoryPath.list(fileFilter);
    }

    private static String removeHeadersFooters(String content) {
        var footer1 = propertiesReader.getProperty("footer1");
        var footer2 = propertiesReader.getProperty("footer2");;
        return content.replace(footer1, "").replace(footer2, "");
    }

    private static void extractPlainTextFromPDF(String pdfFileName, boolean includePageNumber, boolean includeHtml) throws IOException {
        //Loading an existing PDF document
        File file = new File(pdfFileName);
        try (org.apache.pdfbox.pdmodel.PDDocument document = PDDocument.load(file)) {
            int k = document.getNumberOfPages();
            //Instantiate PDFTextStripper class
            StringBuilder sb = new StringBuilder();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            //pdfStripper.setSortByPosition(true);
            if (includeHtml) {
                //pdfStripper.setLineSeparator("<br/>");
                //pdfStripper.setWordSeparator(" ");
                pdfStripper.setParagraphStart("<p>");
                pdfStripper.setParagraphEnd("</p>");
                pdfStripper.setPageStart("<div>");
                pdfStripper.setPageEnd("</div>");
            }

            for (int i = 1; i <= k; i++) {
                pdfStripper.setStartPage(i);
                pdfStripper.setEndPage(i);

                String text = pdfStripper.getText(document);
                if (includePageNumber) {
                    sb.append("PageNo").append(i).append("\n\r").append(text).append("<hr/>").append("<br/>");
                } else {
                    //check if the text contains subsidiary legislation
                    sb.append(text);
                }
            }
            document.close();
            createLegislationTextFile(sb.toString().replace("\r\n", " ").replace("\n", " "), pdfFileName);
        }
    }

    private static int getNumberOfSchedulesInAct(String content, boolean includeHtml) {
        final String regexForSchedule = includeHtml ? "<p>\\w{0,}\\W{0,1}SCHEDULE\\w{0,}\\W{0,1}<\\/p>" : "\\w{0,}\\W{0,1}SCHEDULE\\w{0,}\\W{0,1}";

        final Pattern pattern = Pattern.compile(regexForSchedule, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(content);

        var matchCount = 0;
        while (matcher.find()) {
            matchCount++;
        }

        return matchCount;
    }

    private static void createLegislationTextFile(String legislationContent, String pdfFileName) throws IOException {
        saveTextToFile(pdfFileName.replace(".pdf", ""), removeHeadersFooters(legislationContent));
        if (legislationContent.contains("SUBSIDIARY LEGISLATION")) {
            var legs = legislationContent.split("SUBSIDIARY LEGISLATION");
            //create new file for each subsidiary legislation
            for (int i = 1; i < legs.length; i++) {
                saveTextToFile(pdfFileName.replace(".pdf", String.format("_SUBS_%d", i)), removeHeadersFooters(legs[i]));
            }
        }
    }

    private static void saveTextToFile(String pdfFile, String sb) throws IOException {
        File f = new File(pdfFile + ".txt");
        try (Writer w = new FileWriter(f, Charset.forName("utf-8"))) {
            w.write(sb);
        }
    }
}
