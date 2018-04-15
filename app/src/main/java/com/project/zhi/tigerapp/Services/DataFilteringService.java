package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;

import org.androidannotations.annotations.EBean;

@EBean
public class DataFilteringService {
    public String getPersonName(Entities entities){
        String firstName = "";
        String middleName = "";
        String lastName = "";
        for (Attributes attribute: entities.getList()) {
            if(attribute.getAttributeKey().equalsIgnoreCase("firstname")){
                firstName = attribute.getStringValue();
            }
            else if(attribute.getAttributeKey().equalsIgnoreCase("middlename")){
                middleName = attribute.getStringValue();
            }
            else if(attribute.getAttributeKey().equalsIgnoreCase("familyname")){
                lastName = attribute.getStringValue();
            }
        }
        return firstName + (middleName.isEmpty() ? "" : " " + middleName) + (lastName.isEmpty() ? "" : " " + lastName);
    }
}
