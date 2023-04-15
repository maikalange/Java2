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
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author jnj
 */
public class AlphabeticalIndexGenerator {

    private static String[] getFilesByTypeInDir(String dirPath, String fileType) {
        File directoryPath = new File(dirPath);
        FilenameFilter fileFilter = (File dir, String name) -> {
            return name.toLowerCase().endsWith("." + fileType);
        };
        return directoryPath.list(fileFilter);
    }

    public static void createAlphabeticalIndexOfActs(String dir) throws IOException {
        //String dir = "C:\\Users\\jnj\\Documents\\Laws Zambia\\";
        //Get all html files in the drive
        //Build a list
        String indexOfActsTemplate = """
                                <!DOCTYPE html>     
                                <html lang="en">
                                 <head>
                                   <meta charset="UTF-8" />
                                   <meta http-equiv="X-UA-Compatible" content="IE=edge" />
                                   <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                                   <title>List of Acts</title>
                                   <link rel="stylesheet" href="jquery/css/listnav.css" /> 
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
                                    </head>
                                    <nav class="navbar navbar-expand-lg navbar-light bg-light">
                                  <a class="navbar-brand" href="#">Legal Mind</a>
                                  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                                    <span class="navbar-toggler-icon"></span>
                                  </button>                               
                                  <div class="collapse navbar-collapse" id="navbarSupportedContent">
                                    <ul class="navbar-nav mr-auto">
                                     <p id="sectionheader"></p>                                                              
                                      <li class="nav-item active">
                                        <a class="nav-link" href="index.html">Home <span class="sr-only">(current)</span></a>
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
                                      <p id="pragma" class="lead"></p>
                                    </div>                               
                                    <div class="container">                               
                                      <div class="row">
                                        <div class="col">
                                         <p class="h6">Acts of Parliament</p>                               
                                        </div>
                                        <div class="col-7">
                                          <p class="h6" id="sectionTitle"></p>
                                           <ul id="listOfActs"></ul>
                                        </div>
                                        <div class="col"></div>
                                      </div>
                                    </div>    
                                    <script src="//code.jquery.com/jquery-1.12.4.min.js"></script>
                                    <script src="jquery/js/jquery-listnav.js"></script>
                                    <script>
                                        $("#listOfActs").listnav({showCounts: true,prefixes:['The']});
                                    </script>
                                  </body>
                                </html>                               
                               """;
        Document indexDoc = Jsoup.parse(indexOfActsTemplate);
        Element ul = indexDoc.getElementById("listOfActs");
        String[] files = getFilesByTypeInDir(dir, "html");
        for (String file : files) {
            Element li = new Element("li");
            Element a = new Element("a");
            String actName  = file.replace(".html", "");
            a.appendText(actName.replace("_", " ")).attr("href", MessageFormat.format("./{0}/index.html", actName));
            ul.appendChild(li.appendChild(a));
        }
        File f = new File(dir + "index.html");
        try ( Writer w = new FileWriter(f, Charset.forName("utf-8"))) {
            w.write(indexDoc.html());
        }
    }
}
