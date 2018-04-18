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
    private ArrayList<Attributes> sortMainDemoGraphic(ArrayList<Attributes> attributes) {
        Integer gender = null;
        Integer height1 = null;
        Integer currentaddress = null;
        Integer otheroccupantsataddress = null;
        Integer phonenumber1 = null;
        Integer age1 = null;
        for (var i = 0; i < attributes.size(); i++
                ) {
            var attribute = attributes.get(i);
            switch (attribute.getAttributeKey()){
                case "gender":
                    gender = i;
                    break;
                case "height1":
                    height1 = i;
                    break;
                case "currentaddress":
                    currentaddress = i;
                    break;
                case "otheroccupantsataddress":
                    otheroccupantsataddress = i;
                case "phonenumber1":
                    phonenumber1 = i;
                case "age1":
                    age1 = i;
            }
        }
        attributes = removeAndAddItem(attributes, gender);
        attributes = removeAndAddItem(attributes, height1);
        attributes = removeAndAddItem(attributes, currentaddress);
        attributes = removeAndAddItem(attributes, otheroccupantsataddress);
        attributes = removeAndAddItem(attributes, phonenumber1);
        attributes = removeAndAddItem(attributes, age1);
        return attributes;
    }

    private ArrayList<Attributes> sortAttributesNameFirst(ArrayList<Attributes> attributes) {
        Integer firstName = null;
        Integer middleName = null;
        Integer lastName = null;
        Integer nationalidnumber = null;
        for (var i = 0; i < attributes.size(); i++
                ) {
            var attribute = attributes.get(i);
            switch (attribute.getAttributeKey()){
                case "firstname":
                    firstName = i;
                    break;
                case "middlename":
                    middleName = i;
                    break;
                case "familyname":
                    lastName = i;
                    break;
                case "nationalidnumber":
                    nationalidnumber = i;
            }
        }
        attributes = removeAndAddItem(attributes, nationalidnumber);
        attributes = removeAndAddItem(attributes, lastName);
        attributes = removeAndAddItem(attributes, middleName);
        attributes = removeAndAddItem(attributes, firstName);
        return attributes;
    }
    private ArrayList<Attributes> removeAndAddItem(ArrayList<Attributes> attributes, Integer i){
        if(i == null) return attributes;
        Collections.swap(attributes, i ,0);
//        if(i == null) return attributes;
//        Attributes attribute = attributes.get(i);
//        attributes.remove(i);
//        attributes.add(0, attribute);
        return attributes;
    }

}
