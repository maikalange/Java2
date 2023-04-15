/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.api;

import java.util.List;

/**
 *
 * @author jnj
 */
public final class Act {
    private final Page[] pages;  
    private final List<Section> sections;
    private final List<String> longTitle;

    public Act(Page[] pages,  List<Section> sections, List<String> longTitle) {
        this.longTitle = longTitle;
  
        this.sections = sections;
        this.pages  = pages;
    }

    public List<String> getLongTitle() {
        return this.longTitle;
    }

    public Page[] getPages(){
        return pages;
    }

    public List<Section> getSections() {
        return this.sections;
    }

}
