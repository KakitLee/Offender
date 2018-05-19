package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import java.io.Serializable;

import lombok.Data;

@Data
@Root(strict = false)
public class Attachments implements Serializable{
    @Attribute
    protected String filename;
    @Attribute
    protected Boolean isPrimary;
}
