/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.search;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 * @author jnj
 */
public class SolrDocumentLoader {

    public static void main(String[] args) throws IOException, SolrServerException, NoSuchAlgorithmException {
//        System.setProperty("javax.net.ssl.trustStore", "C:\\solr-8.11.1\\server\\etc\\solr-ssl.keystore.jks");
//        System.setProperty("javax.net.ssl.trustStorePassword", "secret");

        SolrClient client = getUpdateClient();

        
        //Get all parsed pdfs
        final String pdfDir = "C:\\sandbox\\judiciarydownloads\\causelist";
        File file = new File(pdfDir);

        System.out.println("Getting all .html files in " + file.getCanonicalPath());
        List<File> files = (List<File>) FileUtils.listFiles(file, new String[]{"html"}, false);
        for (File f : files) {
            try {
                String content = Files.readString(Path.of(f.getPath()), Charset.forName("UTF-8"));
                client.add(buildDocument(content, f.getName(), "CauseList"),20000);
                client.commit();
            } catch (Exception e) {
                System.out.println("An error occurred whilst processing file " + f.getName() + "\n" + e.getMessage());
            }

        }
        //client.commit();
    }

    private static SolrClient getUpdateClient() {        
        var solrService  = "http://83.229.71.153:8983/solr/legalmind/";
        //"https://legalmindsearch:53RchXLgl@legalmind.co.zm:8983/solr/legalmind"
        SolrClient solrClient = new ConcurrentUpdateSolrClient.Builder(solrService).withThreadCount(10).withQueueSize(100)
                .build();
        return solrClient;
    }

    private static String getDocumentId(String input) throws NoSuchAlgorithmException {
        var b = String.valueOf(input).getBytes();
        var hashBytes = MessageDigest.getInstance("MD5").digest(b);
        return UUID.nameUUIDFromBytes(hashBytes).toString();
    }

    private static SolrInputDocument buildDocument(String content, String fileName, String docType) throws NoSuchAlgorithmException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField("id", getDocumentId(fileName));
        doc.setField("p", content);
        doc.setField("docType", docType);
        doc.setField("title", fileName.replace(".html", "").replace("txt", ""));

        return doc;
    }
}
