package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
@Root(strict = false)
public class Entities{
    @Element
    protected String id;
    @Attribute
    protected String entityTypeKey;
    @Element(required=false)
    protected String scratchpad;
    @ElementList(inline=true)
    private ArrayList<Attributes> list;
    @ElementList(inline=true, required=false)
    protected ArrayList<Attachments> attachments;
}

