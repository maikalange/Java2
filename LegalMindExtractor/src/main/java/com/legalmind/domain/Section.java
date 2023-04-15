/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.domain;

/**
 *
 * @author jnj
 */
public class Section {

    private final String sectionId;
    private final String title;

    public Section(String sectionId, String title) {
        this.sectionId = sectionId;
        this.title = title;
    }
    
    public String getSectionId(){
        return sectionId;
    }
    
    public String getTitle(){
        return title;
    }
}
