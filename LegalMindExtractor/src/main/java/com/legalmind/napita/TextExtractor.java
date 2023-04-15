/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.napita;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Scanner;
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
            Writer output = new PrintWriter(sb.toString() + "html", "utf-8");
            new PDFDomTree().writeText(pdf, output);
            output.close();
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
            //Write TOC to file
            var htmlTemplate = """
                               <!DOCTYPE html>
                               <html lang="en">
                                 <head>
                                   <title></title>
                                   <link
                                     rel="stylesheet"
                                     href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css"
                                     integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm"
                                     crossorigin="anonymous"
                                   />
                                   <script
                                     src="https://code.jquery.com/jquery-3.2.1.slim.min.js"
                                     integrity="sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN"
                                     crossorigin="anonymous"
                                   ></script>
                                   <script
                                     src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js"
                                     integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q"
                                     crossorigin="anonymous"
                                   ></script>
                                   <script
                                     src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"
                                     integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl"
                                     crossorigin="anonymous"
                                   ></script>
                                   <style>
                               ol{
                                         max-height: 18em;
                                         overflow-y: auto;
                                         position: relative;
                                     }
                                     /* width */
                                     *::-webkit-scrollbar {
                                       width: 10px;
                                     }
                                     
                                     /* Track */
                                     *::-webkit-scrollbar-track {
                                       background: #f1f1f1;
                                     }
                                     
                                     /* Handle */
                                     *::-webkit-scrollbar-thumb {
                                       background: #888;
                                     }
                                     
                                     /* Handle on hover */
                                     *::-webkit-scrollbar-thumb:hover {
                                       background: #555;
                                     }                                          
                                   </style>
                                   <script>
                                     var request;
                               
                                     function loadPage(url, anchor) {
                                       if (window.XMLHttpRequest) {
                                         request = new XMLHttpRequest();
                                       } else if (window.ActiveXObject) {
                                         request = new ActiveXObject("Microsoft.XMLHTTP");
                                       }
                                       try {
                                         request.onreadystatechange = function () {
                                           if (request.readyState == 4) {
                                             var val = request.responseText;
                                             document.getElementById("div1").innerHTML = val;
                                             document.getElementById("sectionTitle").innerHTML = anchor.innerText;
                                           }
                                         };
                                         request.open("GET", url, true);
                                         request.send();
                                       } catch (e) {
                                         alert("Unable to connect to server");
                                       }
                                     }
                                   </script>
                                 </head>
                                 <body>
                               <nav class="navbar navbar-expand-lg navbar-light bg-light">
                                 <a class="navbar-brand" href="#">Legal Mind</a>
                                 <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                                   <span class="navbar-toggler-icon"></span>
                                 </button>                               
                                 <div class="collapse navbar-collapse" id="navbarSupportedContent">
                                   <ul class="navbar-nav mr-auto">
                                    <p id="sectionheader"></p>                                                              
                                     <li class="nav-item active">
                                       <a class="nav-link" href="../index.html">All Acts of Parliament <span class="sr-only">(current)</span></a>
                                     </li>
                                     <li class="nav-item">
                                       <a class="nav-link" href="#">Link</a>
                                     </li>
                                     <li class="nav-item dropdown">
                                       <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                         Dropdown
                                       </a>
                                       <div class="dropdown-menu" aria-labelledby="navbarDropdown">
                                         <a class="dropdown-item" href="#">Action</a>
                                         <a class="dropdown-item" href="#">Another action</a>
                                         <div class="dropdown-divider"></div>
                                         <a class="dropdown-item" href="#">Something else here</a>
                                       </div>
                                     </li>
                                     <li class="nav-item">
                                       <a class="nav-link disabled" href="#">Disabled</a>
                                     </li>
                                   </ul>
                                   <form class="form-inline my-2 my-lg-0">
                                     <input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
                                     <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
                                   </form>
                                 </div>
                               </nav>                               
                                   <div class="display-4">
                                     <p id="pragma" class="h2"></p>
                                   </div>                               
                                   <div class="container">                               
                                     <div class="row">
                                       <div class="col"><p class="h6">Arrangement of Sections</p>
                                        <ol></ol>                               
                                       </div>
                                       <div class="col-6">
                                         <p class="h6" id="sectionTitle"></p>
                                         <a href="#">Download</a>
                                         <div id="div1"></div>
                                       </div>
                                       <div class="col"></div>
                                     </div>
                                   </div>
                                 </body>
                               </html>""";
            //Get Pages with section or part information      
            Document navDoc = Jsoup.parse(htmlTemplate);
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

    public static void main(String args[]) throws IOException, ParserConfigurationException {
        //String dir = "C:\\Users\\jnj\\Documents\\Laws Zambia\\";
        String dir2 = "C:\\sandbox\\judiciarydownloads\\causelist\\";
        String[] x = getFilesByTypeInDir(dir2, "pdf");
        for (String f : x) {
            //extractPlainTextFromPDF(dir + f, false,true);
            extractPlainTextFromPDF(dir2 + f, false, true);
            //generateHTMLFromPDF(dir2 + f);
            //getTOCFromDocument(dir2 + f + ".txt");
        }
        //Generate Document Sections
//        String[] htmlFiles = getFilesByTypeInDir(dir2, "html");
//        for (String file : htmlFiles) {
//            HtmlPageSplitter.splitHtmlDocument(file, dir2);
//        }
//        AlphabeticalIndexGenerator.createAlphabeticalIndexOfActs(dir2);
    }

    public static String[] getFilesByTypeInDir(String dirPath, String fileType) {
        File directoryPath = new File(dirPath);
        FilenameFilter fileFilter = (File dir, String name) -> {
            return name.toLowerCase().endsWith("." + fileType);
        };
        return directoryPath.list(fileFilter);
    }

    private static String removeHeadersFooters(String content) {
        var footer1 = "The Laws of Zambia";
        var footer2 = "Copyright Ministry of Legal Affairs, Government of the Republic of Zambia";
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
            createLegislationTextFile(sb.toString().replace("\r\n"," ").replace("\n"," "), pdfFileName, includeHtml);
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

    private static void createLegislationTextFile(String legislationContent, String pdfFileName, boolean includeHtml) throws IOException {
        saveTextToFile(pdfFileName.replace(".pdf", ""), removeHeadersFooters(legislationContent));
        if (legislationContent.contains("SUBSIDIARY LEGISLATION")) {
            var legs  = legislationContent.split("SUBSIDIARY LEGISLATION");
            //create new file for each subsidiary legislation
            for (int i = 1; i < legs.length; i++) {
                saveTextToFile(pdfFileName.replace(".pdf", String.format("_SUBS_%d",i)), removeHeadersFooters(legs[i]));
            }
        }
    }

    private static void saveTextToFile(String pdfFile, String sb) throws IOException {
        File f = new File(pdfFile + ".html");
        try (Writer w = new FileWriter(f, Charset.forName("utf-8"))) {
            w.write(sb);
        }
    }
}
