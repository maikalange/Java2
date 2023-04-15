/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.legamind.content.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 *
 * @author JoeNyirenda
 */
public class GradeAContentTest {

    private final String BASE_DIR = "C:\\sandbox\\LegalMinds\\html-parsing-output\\Grade_A_Acts\\";

    public GradeAContentTest() {
    }

    private String[] getFilesByTypeInDir(String fileType) {
        File directoryPath = new File(BASE_DIR);
        FilenameFilter fileFilter = (File dir, String name) -> {
            return name.toLowerCase().endsWith("." + fileType);
        };
        return directoryPath.list(fileFilter);
    }

    @Test
    public void testThatActShouldContainContent() {
        try {
            var htmlFiles = getFilesByTypeInDir("html");
            var expectedContentFile = getFilesByTypeInDir("tsv");
            Scanner myScanner = new Scanner(new FileReader(BASE_DIR + expectedContentFile[0]));
            while (myScanner.hasNext()) {
                var content = myScanner.nextLine().split("\t");
                var f = BASE_DIR + content[0] + ".html";
                var i = Arrays.asList(htmlFiles).indexOf(content[0] + ".html");
                if (i > -1) {
                    var actualContentForAct = Files.readString(Paths.get(f));
                    var c = content[1].replace(" ", "");
                    var testStatus = (actualContentForAct.replace(" ", "").contains(c)) ? "Passed" : "Failed";
                    var testResultMsg = String.format("Test case for %1$s \t %2$s", content[0] + ".html", testStatus);
                    System.out.println(testResultMsg);
                } else {
                    System.out.println(String.format("%s \t File not found",content[0] + ".html"));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GradeAContentTest.class.getName()).log(Level.WARNING, null, ex);
        }
    }
}
