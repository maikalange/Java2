/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.caseanalzyer;

/**
 *
 * @author jnj
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JudgmentAnalyzer {
    //"\d{1,4}/CCZ{0,}\/\w{0,5}"gm

    public static void main(String[] args) {
        final String regex = "\\d{1,4}/CCZ{0,}\\/\\w{0,5}";
        final String string = """
                              1. Charles Kajimanga (Honourable Judge) v Marmetus Chilemya
                              
                              (Appeal No. 50 of 2014) (unreported)
                              2. OTK Limited v Amanita Zambiana Limited (2011) 1 ZR 170
                              
                              3. Law Association of Zambia and Chapter One Foundation v The
                              Attorney-General (2019/CCZ/0013, 2019/CCZ/0014)
                              
                              4. Chief Chanje v Zulu, Appeal No. 73 of 2008 (unreported)
                              
                              5. Wilson v Secretary of State for Trade and Industry (2003) UKHL
                              
                              40
                              
                              6. Minister of Information and Broadcasting Services and Another
                              v Chembo and Others, Appeal No. 76 of 2005 (unreported)
                              
                              7. Hubert Sankombe v The People (1977) ZR 171""";
	        
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(string);
        
        while (matcher.find()) {
            System.out.println("Full match: " + matcher.group(0));
            
            for (int i = 1; i <= matcher.groupCount(); i++) {
                System.out.println("Group " + i + ": " + matcher.group(i));
            }
        }
    }
}

