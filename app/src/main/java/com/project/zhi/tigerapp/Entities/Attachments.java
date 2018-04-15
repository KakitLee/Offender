package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;

import lombok.Data;

@Data
public class Attachments {
    @Attribute
    protected String filename;
    @Attribute
    protected Boolean isPrimary;
}
