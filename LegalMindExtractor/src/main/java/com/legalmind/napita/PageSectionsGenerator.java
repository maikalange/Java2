/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.napita;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jnj
 */
public class PageSectionsGenerator {

    public static int getPageNoForSection(String pageContent, String sectionTitle) {
        final String regex = "(?:PageNo\\d{1,})";
        String safeContent = makeContentRegexSafe(pageContent);

        String[] pages = (new StringBuilder(safeContent.replace("\n", " "))).toString().split(regex);
        var pageMatches = new ArrayList<Integer>();

        for (int p = 0; p < pages.length; p++) {
            Pattern searchPattern = Pattern.compile(MessageFormat.format("(.*){0}(.*)", makeContentRegexSafe(sectionTitle)), Pattern.MULTILINE);
            Matcher sectionMatcher = searchPattern.matcher(pages[p]);

            while (sectionMatcher.find()) {
                if (p - 1 < 0) {
                    pageMatches.add(0);
                } else {
                    pageMatches.add(p - 1);
                }
            }
        }
        return pageMatches.size() > 0 ? Collections.max(pageMatches) : 0;
    }

    private static String makeContentRegexSafe(String content) {
        var sb = content;
        var modifiedContent = sb.replace("(", "").replace(")", "").
                replace("[", "").replace("]", "")
                .replace("\n", " ").replace("\r", "");
        return modifiedContent;
    }
}
