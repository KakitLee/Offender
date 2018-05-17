package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Enums.AttributeType;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.MenuTuple;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import lombok.experimental.var;

@EBean
public class MenuService {
    public ArrayList<MenuModel> getMainMenus (){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        MenuModel name= new MenuModel();
        name.setAttributeKey("names");
        name.setAttributeDisplayText("Names");

        MenuModel mainDemo= new MenuModel();
        mainDemo.setAttributeKey("mainDemo");
        mainDemo.setAttributeDisplayText("Main Demographic Info");

        MenuModel otherDemo= new MenuModel();
        otherDemo.setAttributeKey("otherDemo");
        otherDemo.setAttributeDisplayText("Other Demographic Info");
        newListModel.add(name);
        newListModel.add(mainDemo);
        newListModel.add(otherDemo);
        return newListModel;
    }

//    public ArrayList<ArrayList<MenuModel>> getAllMenus(ArrayList<String> keys){
//        ArrayList<ArrayList<MenuModel>> allMenuModels = new ArrayList<ArrayList<MenuModel>>();
//        var nameMenuTuple = getNamesMenusWithRemove(keys);
//        allMenuModels.add(nameMenuTuple.getMenuModels());
//        var mainMenuTuple = getMainDemographicWithRemove(nameMenuTuple.getLeftKeys());
//        allMenuModels.add(mainMenuTuple.getMenuModels());
//        var otherMenu = getOtherDemographic(mainMenuTuple.getLeftKeys());
//
//        allMenuModels.add(otherMenu);
//        return allMenuModels;
//    }

    public ArrayList<ArrayList<MenuModel>> getAllMenus(ArrayList<Attributes> keys){
        ArrayList<ArrayList<MenuModel>> allMenuModels = new ArrayList<ArrayList<MenuModel>>();
        var nameMenuTuple = getNamesMenusWithRemove(keys);
        allMenuModels.add(nameMenuTuple.getMenuModels());
        var mainMenuTuple = getMainDemographicWithRemove(nameMenuTuple.getLeftAttributes());
        allMenuModels.add(mainMenuTuple.getMenuModels());
        var otherMenu = getOtherDemographic(mainMenuTuple.getLeftAttributes());

        allMenuModels.add(otherMenu);
        return allMenuModels;
    }

    public ArrayList<MenuModel> getNamesMenus (ArrayList<String> keys){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (String key: keys
             ) {
            switch (key){
                case "firstName":
                case "middleName":
                case "lastName":
                case "nationalidnumber":
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newListModel.add(newModel);
            }
        }
        return newListModel;
    }

    public MenuTuple getNamesMenusWithRemove (ArrayList<Attributes> keys){

        ArrayList<MenuModel> newListModel = new ArrayList<>();
        ArrayList<String> keyStr = new ArrayList<String>();

        for (Iterator<Attributes> iterator = keys.iterator(); iterator.hasNext(); ) {
            Attributes attribute =iterator.next();
            String key = attribute.getAttributeKey();
            keyStr.add(key);
            if(key.toLowerCase().contains("name") || key.toLowerCase().contains("nationalid")){
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newModel.setAttributeType(AttributeType.valueOf(attribute.getType().toUpperCase(Locale.ENGLISH)));
                    newListModel.add(newModel);
                    iterator.remove();
            }
        }
        MenuTuple menuTuple = new MenuTuple();
        menuTuple.setMenuModels(newListModel);
        menuTuple.setLeftAttributes(keys);
        return menuTuple;
    }

    public MenuTuple getMainDemographicWithRemove (ArrayList<Attributes> keys){

        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (Iterator<Attributes> iterator = keys.iterator(); iterator.hasNext(); ) {
            Attributes attribute =iterator.next();
            String key = attribute.getAttributeKey();
            if(key.toLowerCase().contains("gender") || key.toLowerCase().contains("height") || key.toLowerCase().contains("address") || key.toLowerCase().contains("phone") || key.toLowerCase().contains("age")){
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newModel.setAttributeType(AttributeType.valueOf(attribute.getType().toUpperCase(Locale.ENGLISH)));
                    newListModel.add(newModel);
                    iterator.remove();
            }
        }
        Collections.sort(newListModel, Utils.getComparatorMenuModelAlphabetically());
        MenuTuple menuTuple = new MenuTuple();
        menuTuple.setMenuModels(newListModel);
        menuTuple.setLeftAttributes(keys);
        return menuTuple;
    }


    public ArrayList<MenuModel> getOtherDemographic (ArrayList<Attributes> keys){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (var otherDemo: keys
             ) {
            MenuModel newModel= new MenuModel();
            newModel.setAttributeKey(otherDemo.getAttributeKey());
            newModel.setAttributeDisplayText(Utils.displayKeyValue(otherDemo.getAttributeKey()));
            newModel.setAttributeType(AttributeType.valueOf(otherDemo.getType().toUpperCase(Locale.ENGLISH)));
            newListModel.add(newModel);
        }
        Collections.sort(newListModel, Utils.getComparatorMenuModelAlphabetically());
        return newListModel;
    }

    public ArrayList<MenuModel> clearMenuValue (ArrayList<MenuModel> menus){
        for (var menu: menus
                ) {
            if(menu.getValue() != null && !menu.getValue().isEmpty()){
                menu.setValue(null);
            }
        }
        return menus;
    }
}
