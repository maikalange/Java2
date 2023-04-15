/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.tagsoup.processor;

/**
 *
 * @author jnj
 */
public class Definition {

    public String Term;
    public String Meaning;

    public Definition(){
        
    }
    public Definition(String term, String meaning) {
        Term = term;
        Meaning = meaning;
    }
}
