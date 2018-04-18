package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.EBean;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import lombok.experimental.var;
import lombok.val;

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
    public ArrayList<Entities> update(ArrayList<Entities> entities, ArrayList<MenuModel> nameMenu, ArrayList<MenuModel> mainDemoMenu, ArrayList<MenuModel> otherDemoMenu) {
        ArrayList<Entities> filteredEntities = new ArrayList<>();
        val noneEmptyNameMenu = nonEmptyCriteria(nameMenu);
        ArrayList<MenuModel> noneEmptyMainDemoMenu = nonEmptyCriteria(mainDemoMenu);
        ArrayList<MenuModel> noneEmptyOtherDemoMenu = nonEmptyCriteria(otherDemoMenu);
        for (Entities entity : entities
                ) {
            if(isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyNameMenu, entity.getList(), true) && isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyMainDemoMenu, entity.getList(),false) && isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyOtherDemoMenu, entity.getList(),true)){
                filteredEntities.add(entity);
            }
        }
        return filteredEntities;
    }
    public ArrayList<MenuModel> nonEmptyCriteria(ArrayList<MenuModel> menuList){
        ArrayList<MenuModel> nonEmptyMenuList = new ArrayList<>(menuList);
        var nonEmptyFilter = new Predicate<MenuModel>() {
            @Override
            public boolean evaluate(MenuModel object) {
                return object.getValue() != null && !object.getValue().isEmpty();
            }
        };
        CollectionUtils.filter(nonEmptyMenuList, nonEmptyFilter);
        return nonEmptyMenuList;
    }
    public boolean isSatisfyAllCriteriaFromAListOfCriteria(ArrayList<MenuModel> criterias, ArrayList<Attributes> attributes, boolean fuzzyMatch ) {
        if(attributes == null && attributes.size() == 0){
            return true;
        }
        for (var criteria : criterias
                ) {
            if(fuzzyMatch){
                if(!isSatisyFuzzySignleCriteria(criteria, attributes))
                    return false;
            }
            else {
                if (!isSatisySignleCriteria(criteria, attributes)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isSatisySignleCriteria(MenuModel criteria, ArrayList<Attributes> attributes){
        for(var i =0; i< attributes.size(); i++){
            var attribute= attributes.get(i);
            var key = attribute.getAttributeKey();
            var value = "";
            if(attribute.getType().equalsIgnoreCase("TEXT")){
                value = attribute.getStringValue();
            }
            else{
                value = attribute.getDoubleValue().toString();
            }
            if(criteria.getAttributeKey().equalsIgnoreCase(key) && criteria.getValue().equalsIgnoreCase(value)){
                return true;
            }
        }
        return false;
    }

    public boolean isSatisyFuzzySignleCriteria(MenuModel criteria, ArrayList<Attributes> attributes){
        for(var i =0; i< attributes.size(); i++){
            var attribute= attributes.get(i);
            var key = attribute.getAttributeKey();
            var value = "";
            if(attribute.getType().equalsIgnoreCase("TEXT")){
                value = attribute.getStringValue();
            }
            else{
                value = attribute.getDoubleValue().toString();
            }
            if(criteria.getAttributeKey().equalsIgnoreCase(key) && value.toLowerCase().contains(criteria.getValue().toLowerCase())){
                return true;
            }
        }
        return false;
    }
}
