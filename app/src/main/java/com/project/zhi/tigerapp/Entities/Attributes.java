package com.project.zhi.tigerapp.Entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import lombok.Data;

@Data
@Root(strict = false)
public class Attributes{
    @Attribute
    protected String attributeKey;
    @Attribute
    protected String type;
    @Attribute(required=false)
    protected String stringValue;
    @Attribute(required=false)
    protected Double doubleValue;
    @Attribute(required=false)
    protected String listKey;
}
