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
public class TableOfContentItem {

    String identifier;
    String description;
    String sectionTitle;

    public TableOfContentItem(String identifier, String description) {

        this.identifier = identifier;
        this.description = description;
    }
    
    public TableOfContentItem() {

    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String title) {
        sectionTitle = title;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getDescription() {
        return description;
    }
}
