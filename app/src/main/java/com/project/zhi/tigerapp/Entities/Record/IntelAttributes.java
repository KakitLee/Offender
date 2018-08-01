package com.project.zhi.tigerapp.Entities.Record;

import lombok.Data;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Data
@Root(strict = false)
public class IntelAttributes {
    @Attribute
    protected String type;
    @Element(name="record_attribute")
    protected RecordAttribute recordAttribute;
    @Element(name="number_value_1", required=false)
    protected Double numberValue1;
    @Element(name="number_value_2", required=false)
    protected Double numberValue2;
}
