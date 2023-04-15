/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 *
 * @author jnj
 */
public class JudgementsScraper {

    public static void main(String[] args) throws MalformedURLException, IOException, URISyntaxException {
        var baseUrl = "https://judiciaryzambia.com/wp-content/uploads/";
        String folder = null;
        for (int i = 2018; i <= 2022; i++) {
            for (int j = 1; j < 12; j++) {
                if (j < 10) {
                    folder = (i + "/0" + j + "/");
                } else {
                    folder = (i + "/" + j + "/");
                }
                try {
                    downloadJudgementsFile(baseUrl, folder);
                } catch (Exception e) {
                    System.out.println("Error processing file @" + baseUrl + folder + ". The error was " + e.getMessage());
                }
            }
        }

    }
    private static void downloadJudgementsFile(String baseUrl, String folder) throws IOException, MalformedURLException {
        URL url = new URL(baseUrl + folder);
        var js = Jsoup.parse(url, 20000);
        Elements elements = js.getElementsByTag("a");
        for (var e : elements) {
            var file = e.attr("href");
            if (file.contains(".pdf")) {
                String fileToFetch = url.toString() + file;
                URL myPDF = new URL(fileToFetch);
                var b = myPDF.openStream();
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(b.readAllBytes());
                fo.close();
            }
        }
    }
}
