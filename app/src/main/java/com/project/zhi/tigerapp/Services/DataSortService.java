package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import lombok.experimental.var;

@EBean
public class DataSortService {
    public ArrayList<Attributes> sortAttributesGeneral(ArrayList<Attributes> attributes){
        attributes = sortAttributesAlph(attributes);
        attributes = sortMainDemoGraphic(attributes);
        attributes = sortAttributesNameFirst(attributes);
        return attributes;
    }
    public ArrayList<Attributes> sortAttributesAlph(ArrayList<Attributes> attributes){
        Collections.sort(attributes, (object1, object2) -> object1.getAttributeKey().compareTo(object2.getAttributeKey()));
        return attributes;
    }
    private ArrayList<Attributes> sortIndivisualItem(String key, ArrayList<Attributes> attributes) {
        Integer indices = null;
        for (var i = 0; i < attributes.size(); i++
                ) {
            if(attributes.get(i).getAttributeKey().equalsIgnoreCase(key)){
                indices = i;
                break;
            }
        }
        if(indices != null){
            attributes = removeAndAddItem(attributes, indices);
        }
        return attributes;

    }
    private ArrayList<Attributes> sortMainDemoGraphic(ArrayList<Attributes> attributes) {
        attributes = sortIndivisualItem("gender",attributes);
        attributes = sortIndivisualItem("height1",attributes);
        attributes = sortIndivisualItem("currentaddress",attributes);
        attributes = sortIndivisualItem("otheroccupantsataddress",attributes);
        attributes = sortIndivisualItem("phonenumber1",attributes);
        attributes = sortIndivisualItem("age1",attributes);

        return attributes;
    }

    private ArrayList<Attributes> sortAttributesNameFirst(ArrayList<Attributes> attributes) {

        attributes = sortIndivisualItem("nationalidnumber",attributes);
        attributes = sortIndivisualItem("familyname",attributes);
        attributes = sortIndivisualItem("middleName",attributes);
        attributes = sortIndivisualItem("firstname",attributes);

        return attributes;
    }
    private ArrayList<Attributes> removeAndAddItem(ArrayList<Attributes> attributes, Integer i){
        if(i == null) return attributes;
        Attributes tempAttribute = attributes.get(i);
        attributes.remove(tempAttribute);
        attributes.add(0,tempAttribute);
//        Collections.swap(attributes, i ,0);
//        if(i == null) return attributes;
//        Attributes attribute = attributes.get(i);
//        attributes.remove(i);
//        attributes.add(0, attribute);
        return attributes;
    }

}
