/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.legalmind.domain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jnj
 */
public class Page {

    private List<Section> sections;
    private int pageNo;

    public Page(int pageNo) {
        this.pageNo = pageNo;
        sections = new ArrayList<>();
    }

    public void setPageSection(Section section) {
        sections.add(section);
    }

    public int getPageNo() {
        return pageNo;
    }

    public List<Section> getSections() {
        return sections;
    }
}
