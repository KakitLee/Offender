package com.project.zhi.tigerapp.Entities.Record;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(strict = false)
@Data
public class IntelRecord {
    @Element(required=false)
    protected String title;
    @ElementList(entry="attributes",inline = true)
    protected ArrayList<IntelAttributes> intelAttributes;
    @Element(required=false, name="locations")
    protected IntelLocations locations;

    public IntelAttributes getLocationAttribute(){
        if(getLocations() == null ) return null;
        for (IntelAttributes attribute: getIntelAttributes()
                ) {
            if(attribute.getRecordAttribute().getName().toLowerCase().contains("location")){
                return attribute;
            }
        }
        return null;
    }
}
