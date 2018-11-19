package com.project.zhi.tigerapp.Services;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Entities.Name;
import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.Entities.Record.IntelRecord;
import com.project.zhi.tigerapp.Enums.AttributeType;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.CartesianDistCalc;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.impl.PointImpl;

import java.util.ArrayList;
import java.util.Collections;

import lombok.experimental.var;
import lombok.val;

@EBean
public class DataFilteringService {
    @Pref
    UserPrefs_ userPrefs;
    @Bean
    DataSourceServices dataSourceServices;
    public Name getPersonName(Entities entities){
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
        Name newName = new Name();
        newName.setFirstName(firstName);
        newName.setMiddleName(middleName);
        newName.setLastName(lastName);
        return newName;
    }

    public ArrayList<Entities> search(ArrayList<Entities> entities, String query){
        if(query == null || query.isEmpty()){
            return entities;
        }
        String [] queries = query.split(",");
        ArrayList<Entities> filteredEntities = new ArrayList<>();
        for (String oneQuery: queries
             ) {
            for (Entities entity: entities){
                if(isSatisySingleQuery(oneQuery.trim(),entity.getList())){
                    filteredEntities.add(entity);
                }
            }
            entities.retainAll(filteredEntities);
            filteredEntities.clear();
        }
        return entities;
    }

    public ArrayList<Entities> searchLocation(ArrayList<Entities> entities, Double longitude, Double latitude, Double radius){
        if(entities == null ||entities.size() == 0) {
            return entities;
        }
        SpatialContext ctx = SpatialContext.GEO;
        Double degree = DistanceUtils.dist2Degrees(radius,DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM);
        PointImpl point = new PointImpl(longitude,latitude,ctx);
        CartesianDistCalc cdc = new CartesianDistCalc();
        ArrayList<Entities> searchedEntites = new ArrayList<Entities>();
        for (Entities entity: entities
             ) {
            if(entity.getRecordLocations() != null && entity.getRecordLocations().size() > 0) {
                for (IntelRecord record : entity.getRecordLocations()) {
                    boolean isWithin = isWithin(degree, point, cdc, record.getLocationAttribute().getNumberValue1(),record.getLocationAttribute().getNumberValue2() );
                    if (isWithin) searchedEntites.add(entity);
                    break;
                }
            }
            for(Attributes attribute: entity.getList()){
               if(attribute.getType().equalsIgnoreCase(AttributeType.POSITION.name())){

               }
            }
        }


        return searchedEntites;
    }

    public boolean isWithin(Double degree, PointImpl point, CartesianDistCalc cdc, Double recordLongitude, Double recordLatitude) {
        return cdc.within(point, recordLongitude, recordLatitude, degree);
    }

    public ArrayList<Entities> update(ArrayList<Entities> entities, ArrayList<MenuModel> nameMenu, ArrayList<MenuModel> mainDemoMenu, ArrayList<MenuModel> otherDemoMenu) {
        ArrayList<Entities> filteredEntities = new ArrayList<>();
        val noneEmptyNameMenu = nonEmptyCriteria(nameMenu);
        ArrayList<MenuModel> noneEmptyMainDemoMenu = nonEmptyCriteria(mainDemoMenu);
        ArrayList<MenuModel> noneEmptyOtherDemoMenu = nonEmptyCriteria(otherDemoMenu);
        for (Entities entity : entities
                ) {
            if(isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyNameMenu, entity.getList(), true) && isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyMainDemoMenu, entity.getList(),true) && isSatisfyAllCriteriaFromAListOfCriteria(noneEmptyOtherDemoMenu, entity.getList(),true)){
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

    public boolean isSatisySingleQuery(String query, ArrayList<Attributes> attributes) {
        for (var i = 0; i < attributes.size(); i++) {
            var attribute = attributes.get(i);
            var key = attribute.getAttributeKey();
            var isFuzzy = true;
            var value = "";
            if (attribute.getType().equalsIgnoreCase("TEXT")) {
                value = attribute.getStringValue();
            } else if (attribute.getType().equalsIgnoreCase(AttributeType.LIST.name())) {
                value = attribute.getListKey();
                isFuzzy = false;
            } else if (attribute.getDoubleValue() != null) {
                value = attribute.getDoubleValue().toString();
            }
            if (isFuzzy) {
                if ((wildCardMatch(value,query))){
                    return true;
                } else if(value.toLowerCase().contains(query.toLowerCase())) {
                    return true;
                }
            } else {
                if (wildCardMatch(value,query)){
                    return true;
                }
                else if (value.toLowerCase().equalsIgnoreCase(query.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean wildCardMatch(String value, String query){
        if (query.contains("*")){
            String tempQuery = query.replaceAll("\\*", "\\\\w*");
            if(value.matches(tempQuery)){
                return true;
            }
        }
        return false;
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
            if(attribute.getType().equalsIgnoreCase(AttributeType.NUMERIC.name())){
                if(criteria.getAttributeKey().equalsIgnoreCase(key) && attribute.getDoubleValue() >= criteria.getMinValue() && attribute.getDoubleValue() <= criteria.getMaxValue() ){
                    return true;
                }
            }
            else if(attribute.getType().equalsIgnoreCase(AttributeType.BOOLEAN.name())){
                if(criteria.getAttributeKey().equalsIgnoreCase(key) && Utils.getAttributeValues(attribute).equalsIgnoreCase(criteria.getValue())){
                    return true;
                }
            }
            else if(attribute.getType().equalsIgnoreCase(AttributeType.LIST.name())){
                value = attribute.getListKey();
                if(value != null &&criteria.getAttributeKey().equalsIgnoreCase(key) && value.toLowerCase().equalsIgnoreCase(criteria.getValue().toLowerCase())){
                    return true;
                }
            }
            else if (attribute.getType().equalsIgnoreCase(AttributeType.POSITION.name())){
                Double longitude = attribute.getDoubleValue();
                Double latitude = attribute.getDoubleValue2();

                if(criteria.getAttributeKey().equalsIgnoreCase(key) && longitude >= criteria.getMinValue() && longitude <= criteria.getMaxValue() ){
                    if(criteria.getAttributeKey().equalsIgnoreCase(key) && latitude >= criteria.getMinValue2() && latitude <= criteria.getMaxValue2() )
                        return true;
                }

            }
            else{
                value = attribute.getStringValue();
                if(value != null && criteria.getAttributeKey().equalsIgnoreCase(key) && value.toLowerCase().contains(criteria.getValue().toLowerCase())){
                    return true;
                }
            }

        }
        return false;
    }

    public ArrayList<Person> mergeAll(Context context, boolean isClear){
        ArrayList<Person> filteredPersonList = new ArrayList<Person>();
        ArrayList<Entities> entities = dataSourceServices.getPeopleSource(context).getEntitiesList();
        String gsonAllMenu = userPrefs.allMenu().get();
        ArrayList<ArrayList<MenuModel>> allMenus = Utils.gson.fromJson(gsonAllMenu, new TypeToken< ArrayList<ArrayList<MenuModel>>>(){}.getType());
        ArrayList<MenuModel> nameMenu = new ArrayList<MenuModel>();
        ArrayList<MenuModel> mainDempMenu = new ArrayList<MenuModel>();
        ArrayList<MenuModel> otherDemoMenu = new ArrayList<MenuModel>();

        if(allMenus != null && allMenus.size() > 3) {
            nameMenu = allMenus.get(1);
            mainDempMenu = allMenus.get(2);
            otherDemoMenu = allMenus.get(3);
        }

        ArrayList<Entities> updatedEntities = update(entities,nameMenu,mainDempMenu,otherDemoMenu);
        ArrayList<Person> people = dataSourceServices.getPeopleFromEntities(updatedEntities);
        //ArrayList<Entities> filterList= new ArrayList<Entities>();
        //Union
       // String jsonFilteredEntites = userPrefs.filteredEntites().get();////json filterd
        String jsonVoice = userPrefs.voiceEntities().get();/// json voice
        String jsonFace = userPrefs.facialEntities().get(); //// json face

        if((jsonVoice.isEmpty()||jsonVoice==null)&&(jsonFace.isEmpty()||jsonFace==null)){
            return people;
            //filteredPersonList=people;
        }
        else if(jsonVoice.isEmpty()||jsonVoice==null){
            filteredPersonList=Utils.gson.fromJson(userPrefs.facialEntities().get(),new TypeToken<ArrayList<Person>>(){}.getType());
        }
        else if(jsonFace.isEmpty()||jsonFace==null){
            filteredPersonList=Utils.gson.fromJson(userPrefs.voiceEntities().get(),new TypeToken<ArrayList<Person>>(){}.getType());
        }
        else{
            filteredPersonList= Utils.gson.fromJson(userPrefs.facialEntities().get(),new TypeToken<ArrayList<Person>>(){}.getType());
            ArrayList<Person> voiceList= new ArrayList<Person>();
            voiceList= Utils.gson.fromJson(userPrefs.voiceEntities().get(),new TypeToken<ArrayList<Person>>(){}.getType());
            for(int i=0; i<voiceList.size();i++){
                Person voiceItem=voiceList.get(i);
                boolean flag= false;
                for(int j=0; j<filteredPersonList.size();j++)
                {
                    Person faceItem=filteredPersonList.get(j);
                    String faceId= faceItem.getEntity().getId();
                    String voiceId= voiceItem.getEntity().getId();
                    if(faceId.equals(voiceId)){
                        faceItem.setVoiceSimilarity(voiceItem.getVoiceSimilarity());
                        faceItem.setOverallSimilarity(0.5*faceItem.getFacialSimilarity()+0.5*voiceItem.getVoiceSimilarity());
                        flag=true;
                    }
                }
                if(flag==false){
                    filteredPersonList.add(voiceItem);
                }
            }
        }
        ArrayList<Person> mergedPersonList = new ArrayList<Person>();
        for(int i=0; i<filteredPersonList.size();i++){
            Person faceAndVoiceItem=filteredPersonList.get(i);
            for(int j=0; j<people.size();j++){
                String faceAndVoiceId=faceAndVoiceItem.getEntity().getId();
                String filterId=people.get(j).getEntity().getId();
                if(faceAndVoiceId.equals(filterId)){
                    mergedPersonList.add(faceAndVoiceItem);
                }
            }
        }
        Collections.sort(mergedPersonList,Utils.getPersonComparator());
        return mergedPersonList;
    }
}
