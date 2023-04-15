/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.napita;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author jnj
 */
public class HtmlPageSplitter {

    private static Path createHtmlSplitDir(String actName, String pdfSourceDir) {
        //Creating a directory
        Path path = Paths.get((pdfSourceDir + actName + "/"));
        try {
            if (!Files.isDirectory(path)) {
                path = Files.createDirectories(path);
            }
        } catch (IOException e) {

            System.err.println("Failed to create directory!" + e.getMessage());
        }
        return path;
    }

    public static void splitHtmlDocument(String htmlFile, String pdfSourceDir) throws IOException {
        //Get the document 
        var sb = new StringBuffer(htmlFile);
        sb.replace(sb.indexOf(".html"), sb.length(), "");
        var outputFolder = createHtmlSplitDir(sb.toString(), pdfSourceDir);
        File in = new File(pdfSourceDir + htmlFile);

        Document doc = Jsoup.parse(in, "utf-8");
        Elements pages = doc.getElementsByClass("page");
        //Create new html pages by splitting across pages
        Element style = doc.getElementsByTag("style").first();

        String navigationPage = "<html><head><title></title></head><body><ul></ul></body></html>";
        Document navDoc = Jsoup.parse(navigationPage);
        Element ul = navDoc.getElementsByTag("ul").first();

        pages.forEach(page -> {
            ul.append(MessageFormat.format("<li><a href=\"{0}.{1}\">{0}</a></li>", page.id(), "html"));
            String content = "<html><head><meta name=\"viewport\" content=\"width=device, initial-scale=1.0\"><meta charset=\"utf-8\">"
                    + "</head><body></body></html>";
            Document articlePage = Jsoup.parse(content);
            articlePage.getElementsByTag("head").append(style.outerHtml());
            articlePage.getElementsByTag("body").append(page.outerHtml());
            File f = new File(outputFolder + "\\" + page.id() + ".html");
            try {
                Writer w = new FileWriter(f);
                w.write(articlePage.html());
                w.close();
            } catch (IOException ex) {
                Logger.getLogger(HtmlPageSplitter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

    }
}
