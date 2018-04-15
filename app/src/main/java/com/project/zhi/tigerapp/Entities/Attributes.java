package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;

import lombok.Data;

@Data
public class Attributes{
    @Attribute
    protected String attributeKey;
    @Attribute
    protected String type;
    @Attribute(required=false)
    protected String stringValue;
    @Attribute(required=false)
    protected Double doubleValue;
}
