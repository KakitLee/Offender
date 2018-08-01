package com.project.zhi.tigerapp.Entities.Record;

import lombok.Data;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Data
@Root(strict = false)
public class IntelLocations {
    @Element(name="id")
    protected String id;
    @ElementList(inline=true, required=false, entry="entities")
    protected ArrayList<IntelEntities> intelEntities;
}
