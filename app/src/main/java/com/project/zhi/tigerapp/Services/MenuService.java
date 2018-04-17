package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.MenuTuple;

import org.androidannotations.annotations.EBean;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import lombok.experimental.var;
import lombok.val;

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

    public ArrayList<ArrayList<MenuModel>> getAllMenus(ArrayList<String> keys){
        ArrayList<ArrayList<MenuModel>> allMenuModels = new ArrayList<ArrayList<MenuModel>>();
        var nameMenuTuple = getNamesMenusWithRemove(keys);
        allMenuModels.add(nameMenuTuple.getMenuModels());
        var mainMenuTuple = getMainDemographicWithRemove(nameMenuTuple.getLeftKeys());
        allMenuModels.add(mainMenuTuple.getMenuModels());
        var otherMenu = getOtherDemographic(mainMenuTuple.getLeftKeys());
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

    public MenuTuple getNamesMenusWithRemove (ArrayList<String> keys){

        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            switch (key){
                case "firstname":
                case "middlename":
                case "familyname":
                case "nationalidnumber":
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newListModel.add(newModel);
                    iterator.remove();
            }
        }
        MenuTuple menuTuple = new MenuTuple();
        menuTuple.setMenuModels(newListModel);
        menuTuple.setLeftKeys(keys);
        return menuTuple;
    }

    public ArrayList<MenuModel> getMainDemographic (ArrayList<String> keys){
        ArrayList<MenuModel> newListModel = new ArrayList<>();

        for (String key: keys
                ) {
            switch (key){
                case "gender":
                case "height1":
                case "currentaddress":
                case "otheroccupantsataddress":
                case "phonenumber1":
                case "age1":
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newListModel.add(newModel);
                    break;
            }
        }

        return newListModel;
    }
    public MenuTuple getMainDemographicWithRemove (ArrayList<String> keys){

        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (Iterator<String> iterator = keys.iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            switch (key){
                case "gender":
                case "height1":
                case "currentaddress":
                case "otheroccupantsataddress":
                case "phonenumber1":
                case "age1":
                    MenuModel newModel= new MenuModel();
                    newModel.setAttributeKey(key);
                    newModel.setAttributeDisplayText(Utils.displayKeyValue(key));
                    newListModel.add(newModel);
                    iterator.remove();
            }
        }
        MenuTuple menuTuple = new MenuTuple();
        menuTuple.setMenuModels(newListModel);
        menuTuple.setLeftKeys(keys);
        return menuTuple;
    }


    public ArrayList<MenuModel> getOtherDemographic (ArrayList<String> keys){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        for (var otherDemo: keys
             ) {
            MenuModel newModel= new MenuModel();
            newModel.setAttributeKey(otherDemo);
            newModel.setAttributeDisplayText(Utils.displayKeyValue(otherDemo));
            newListModel.add(newModel);
        }

        return newListModel;
    }
}
