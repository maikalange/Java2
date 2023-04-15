/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.napita;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jnj
 */
public class TocExtractor {

    public static String generateDocumentToc(String legalDoc) {
        //https://regex101.com/r/ARDsIZ/1
        final String regex = "\\s{0,}\\d{1,3}\\w{0,}[.]\\s{1,}[:\\\".,\\\\(\\\\)\\d{0,}\\]\\['’\\sa-zA-Z\\\\-]*\\n";

        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(legalDoc);

        if (matcher.find(0)) {
            return matcher.group(0);
        }

        return new String();
    }
}
