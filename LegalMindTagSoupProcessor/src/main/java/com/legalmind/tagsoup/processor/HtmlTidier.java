/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.tagsoup.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.ccil.cowan.tagsoup.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.XMLReader;

/**
 *
 * @author jnj
 */
public class HtmlTidier {

    public static void main(String[] args) {

        var sourceDir = "C:\\sandbox\\judiciarydownloads\\causelist\\";
        tidyAllActs(sourceDir);
        ObjectMapper objectMapper = new ObjectMapper();
        var files = getFilesByTypeInDir(sourceDir, "json");
        for (var filePath : files) {
            File jsonFile = new File(sourceDir + filePath);
            var dirToCreate = jsonFile.getName().replace(".json", "");
            try {
                var act = objectMapper.readValue(jsonFile, Act.class);
                var sections = act.sections;

                if (act.pages[0].content != null) {
                    getCompliantHtml(act.pages[0].content, "_ALL_CONTENT.html", dirToCreate);
                }
                sections.forEach(s -> {
                    if (s.sectionTitle != null && s.sectionLocation == Section.LocationOfSection.BODY) {
                        Document doc = Jsoup.parse(s.sectionContent);
                        Document doc2 = Jsoup.parse(s.sectionTitle);
                        getCompliantHtml(doc.html(), s.sectionNo + "_" + s.sectionLocation + "_C.html", dirToCreate);
                        getCompliantHtml(doc2.html(), s.sectionNo + "_" + s.sectionLocation + "_T.html", dirToCreate);
                    }
                    if (s.sectionLocation == Section.LocationOfSection.TOC) {
                        Document doc = Jsoup.parse(s.sectionContent);
                        getCompliantHtml(doc.html(), s.sectionNo + "_" + s.sectionLocation + "_C.html", dirToCreate);
                    }
                });
            } catch (IOException ex) {
                Logger.getLogger(HtmlTidier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String[] getFilesByTypeInDir(String dirPath, String fileType) {
        File directoryPath = new File(dirPath);
        FilenameFilter fileFilter = (File dir, String name) -> {
            return name.toLowerCase().endsWith("." + fileType);
        };
        return directoryPath.list(fileFilter);
    }

    private static void getCompliantHtml(String content, String fileName, String dir) {
        try {
            Path path = Paths.get(dir);
            Files.createDirectories(path);
            try ( OutputStream os = new FileOutputStream(path.toString() + "\\" + fileName)) {
                XMLReader p = new Parser();
                p.setProperty(Parser.schemaProperty, new HTMLSchema());
                p.setFeature(Parser.namespacesFeature, false);

                Writer w = new OutputStreamWriter(os);
                ContentHandler handler = getContentHandler(w);
                p.setContentHandler(handler);

                var source = new InputSource(new StringReader(content));
                p.parse(source);
            }
        } catch (IOException | SAXException ex) {
            Logger.getLogger(HtmlTidier.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static ContentHandler getContentHandler(Writer w) {
        XMLWriter x = new XMLWriter(w);
        x.setOutputProperty(XMLWriter.METHOD, "html");
        x.setOutputProperty(XMLWriter.OMIT_XML_DECLARATION, "yes");
        x.setOutputProperty(XMLWriter.INDENT, "yes");

        return x;
    }

    private static void tidyAllActs(String sourceDir) {
        var files = getFilesByTypeInDir(sourceDir, "html");
        for (var file : files) {
            try {
                Path path = Paths.get(sourceDir, file);
                var content = Files.readString(path);
                getCompliantHtml(content, file.replace(".html", "") + "_TIDY.html", file.replace(".html", ""));
            } catch (IOException ex) {
                Logger.getLogger(HtmlTidier.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
