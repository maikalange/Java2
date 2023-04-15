/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.api;

/**
 *
 * @author jnj
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legalmind.napita.TocExtractor;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.text.WordUtils;

public class JSONGenerator {

    static final String sectionsRegex = "(\\d{1,}\\.)";

    public static File[] getFiles(String dir) {
        FileFilter actTxtFileFilter = (var file) -> {
            //if the file extension is .log return true, else false

            return file.getName().endsWith(".pdf.txt");
        } //Override accept method
                ;
        File directory = new File(dir);
        return directory.listFiles(actTxtFileFilter);
    }

    public static void main(String[] args) throws IOException {
        String s1 = " Hello world. This is a test ";
        System.out.println(s1.trim().indexOf("Hello world. This is a test"));

        File[] files = getFiles("C:\\Users\\jnj\\Documents\\Laws Zambia\\TOC");

        for (File f : files) {
            String contents = Files.readString(Paths.get(f.getAbsolutePath()));
            Path path = Paths.get("C:\\Users\\jnj\\Documents\\Laws Zambia\\TOC\\" + f.getName());
            var plainTextContent = Files.readString(path);
            processJsonForAct(contents, f.getName(), plainTextContent);
        }
    }

    private static void processJsonForAct(String actOfParliamentDoc, String fileName, String plainTextContent) {
        var string1 = actOfParliamentDoc.replace("The Laws of Zambia", "").replace("Copyright Ministry of Legal Affairs, Government of the Republic of Zambia", "");
        final Pattern pattern = Pattern.compile(sectionsRegex);
        final Matcher matcher = pattern.matcher(string1);
        List<String> sectionNos = new ArrayList<>();
        while (matcher.find()) {
            sectionNos.add(matcher.group(0));
        }
        System.out.println(sectionNos);

        //short title from file name
        //noPages from text extract
        //copyright information standard
        //number of sections from sectionNos
        //sections from List<String>
        //LongTitle from text extract regex
        //Act act = new Act(shortTitle, noPages, copyRight, noSections, sections, longTitle);
        //Regex for locating subsection
        //final String pageNoRegex = "PageNo\\d{1,}";
        var longTitle = actOfParliamentDoc.replace("PageNo1", "").replace("The Laws of Zambia", "").replace("Copyright Ministry of Legal Affairs, Government of the Republic of Zambia", "").toUpperCase().split("ARRANGEMENT OF SECTIONS");

        List<String> longTitle2 = new ArrayList<>();
        var j = longTitle[0].trim().replace("\n", ",");
        var p = j.split(",");
        for (String p1 : p) {
            longTitle2.add(p1.trim());
        }

        //final Pattern pattern1 = Pattern.compile(pageNoRegex, Pattern.MULTILINE);
        //final Matcher matcher1 = pattern1.matcher(string1);
        //while (matcher1.find()) {
        //   System.out.println(matcher1.group(0).replace("PageNo", ""));
        //}
        //Get sections in a page
        var pages = new String[]{string1};
        HashMap<Integer, String[]> pageSectionMap = new HashMap<>();
        for (int z = 0; z < pages.length; z++) {
            //find the sections in a page
            var sectionsInPage = getSectionsInPage(pages[z]);
            pageSectionMap.put(z, sectionsInPage);
        }
        var myPages = new Page[pages.length];
        for (int i = 0; i < pages.length; i++) {
            myPages[i] = new Page(i, pages[i]);
        }
        List<Section> sections = new ArrayList<>();
        var nonHtmlContent = plainTextContent.replace("<br/>", "\n").replace("<p>", "").replace("</p>", "\n").replace("<div>", "").replace("</div>", "");

        var toc = TocExtractor.generateDocumentToc(nonHtmlContent);//get plain text 
        var actContentSb = new StringBuilder(myPages[0].getContent());
        var sc = new Scanner(toc);
        while (sc.hasNext()) {
            var c = sc.nextLine().trim();
            if (c.length() > 1 && Character.isDigit(c.charAt(0))) {
                String[] s = c.split("\\.");
                if (s.length == 2) {
                    try {
                        var sectionContent = getSectionContentFromPage(myPages[0].getContent(), s[0]);
                        //prevent the addition of duplicate section numbers
                        if (!sections.contains(new Section(0, "", s[0], ""))) {
                            sections.add(new Section(0, s[1].trim(), s[0], sectionContent));
                            if (s[1].trim().length() - 1 > 0) {
                                int sectionTocPos = actContentSb.toString().indexOf(s[1].trim());
                                var wrappedMargin = WordUtils.wrap(s[1].trim(), 25, "<br/>", true);

                                int sectionInMarginPos = actContentSb.toString().toLowerCase().lastIndexOf(wrappedMargin.toLowerCase().trim());
                                if (sectionTocPos != sectionInMarginPos) {
                                    if (sectionInMarginPos > -1) {
                                        actContentSb.replace(sectionInMarginPos, sectionInMarginPos + wrappedMargin.length(), "<a name=\"" + s[0] + "\"><b class=\"margin2\">&#8593;" + wrappedMargin + "</b></a>");
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("Error processing section " + s[0] + "  " + e.getMessage() + " " + fileName);
                    }
                }
            }
        }
        myPages[0].setContent(actContentSb.toString());
        Act act = new Act(myPages, sections, longTitle2);
//        var k = getSectionContentFromPage(myPages[10].getContent(), 31);
//        var s = getSectionThatSpansMultiplePages(myPages, 31, actOfParliamentDoc);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(new File("c:\\sandbox\\legalminds\\json\\" + fileName.replace(".pdf.txt", "") + ".json"), act);
        } catch (IOException ex) {
            Logger.getLogger(JSONGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        var page = myPages[0];
        List<String> sectionIds = new ArrayList<>();
        sections.forEach(e -> {
            sectionIds.add(e.getSectionNo());
        });

        var json = generateJson(page, sectionIds, actOfParliamentDoc);

    }

    private static String generateJson(Page page, List<String> sectionIds, String actOfParliament) {
        var sb = new StringBuilder();
        for (var i : sectionIds) {
            var s = getSectionThatSpansMultiplePages(page, i, actOfParliament);
            var c = String.format("{\"id\":%s,\"content\":\"%s\"},", i, s);
            sb.append(c);
        }
        return sb.toString();
    }

    private static String getSectionContentFromPage(String pageContent, String sectionNo) {
        //Get the content for a numbered section on a page
        HashMap<String, String> sectionNumberAndContent = new HashMap<>();
        //^29\.\s*([\w\W]){1,}
        String sb = "^" + sectionNo + "\\.\\s*([\\w\\W]){1,}";
        var sectionsOnThePage = pageContent.split("\\d{1,3}\\w{0,}\\.[^\\n]");

        final Pattern pattern = Pattern.compile("\\d{1,3}\\w{0,}\\.[^\\n]", Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(pageContent);
        HashMap idSectionMap = new HashMap<String, String>();
        var count = 1;
        while (matcher.find()) {
            var id = matcher.group(0);
            idSectionMap.put(id, sectionsOnThePage[count]);
            sectionNumberAndContent.put(id, sectionsOnThePage[count]);
            count++;
        }
        return sectionNumberAndContent.get(sectionNo + ". ");
    }

    private static String[] getSectionsInPage(String page) {
        final Pattern pattern = Pattern.compile(sectionsRegex);
        final Matcher matcher = pattern.matcher(page);
        List<String> sectionsInPage = new ArrayList<>();
        while (matcher.find()) {
            if (page != "") {
                sectionsInPage.add(matcher.group(0));
            }
        }
        return sectionsInPage.toArray(new String[0]);
    }

    private static String getSectionThatSpansMultiplePages(Page page, String sectionNo, String actOfParliament) {
        StringBuilder sb = new StringBuilder();

        sb.append(page.getContent());

        var c = sb.toString().replace(TocExtractor.generateDocumentToc(actOfParliament), "");
        return getSectionContentFromPage(c, sectionNo);
    }
}
