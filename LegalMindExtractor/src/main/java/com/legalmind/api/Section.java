/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.api;

import java.util.Objects;

/**
 *
 * @author jnj
 */
public class Section {

    private final int pageNo;
    private final String sectionTitle;
    private final String sectionNo;
    private final String sectionContent;

    Section(int pageNo, String sectionTitle, String sectionNo,String sectionContent) {
        this.pageNo = pageNo;
        this.sectionTitle = sectionTitle;
        this.sectionNo = sectionNo;
        this.sectionContent = sectionContent;
    }
    
    @Override
    public boolean equals(Object o){
        Section s  = (Section)o;
        
    return s.getSectionNo().equalsIgnoreCase(this.sectionNo);
}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.sectionNo);
        return hash;
    }
    public String getSectionNo(){
        return sectionNo;
    }
    public String getSectionTitle() {
        return sectionTitle;
    }
    
    public String getSectionContent(){
        return sectionContent;
    }

    public int getPageNo() {
        return pageNo;
    }
}
