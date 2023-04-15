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
public class Section {
    
        public String sectionTitle;
        
        public enum LocationOfSection { TOC,BODY}
        
        public String sectionNo;
        
        public LocationOfSection sectionLocation ;
        
        public String sectionContent ;
        
        public Section(){}
        
        public Section(String sectionNo, String content, LocationOfSection secLocation)
        {
            this.sectionNo = sectionNo;
            sectionContent = content;
            sectionLocation = secLocation;
        }

}
