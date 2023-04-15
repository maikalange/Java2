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
public class TableOfContent {

    private List<TableOfContentItem> tocItems;
    private String title;

    public TableOfContent(String title) {
        tocItems = new ArrayList<>();
        this.title = title;
    }

    private String getTitle() {
        return title;
    }

    public void setTableOfContentIem(TableOfContentItem item) {
        this.tocItems.add(item);
    }
    private String name;

    public String getName() {
        return name;
    }

    public void setActName(String name) {
        this.name = name;
    }
}
