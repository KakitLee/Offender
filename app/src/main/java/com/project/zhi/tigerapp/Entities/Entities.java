package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Entities {
    @Element
    protected String id;
    @Attribute
    protected String entityTypeKey;
    @Element(required=false)
    protected String scratchpad;
    @ElementList(inline=true)
    private ArrayList<Attributes> list;
    @Element(required=false)
    protected  Attachments attachments;
}

