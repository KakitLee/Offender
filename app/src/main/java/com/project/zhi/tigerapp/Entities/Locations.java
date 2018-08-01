package com.project.zhi.tigerapp.Entities;

import lombok.Data;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Data
@Root(strict = false)
public class Locations {
    @Attribute
    protected String id;
    @Attribute
    protected String recordKey;
}
