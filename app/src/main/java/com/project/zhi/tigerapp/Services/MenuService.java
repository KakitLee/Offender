package com.project.zhi.tigerapp.Services;

import com.project.zhi.tigerapp.complexmenu.MenuModel;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;

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

    public ArrayList<MenuModel> getNamesMenus (){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        MenuModel firstName= new MenuModel();
        firstName.setAttributeKey("firstname");
        firstName.setAttributeDisplayText("Firstname");

        MenuModel middleName= new MenuModel();
        middleName.setAttributeKey("middleName");
        middleName.setAttributeDisplayText("MiddleName");

        MenuModel lastName= new MenuModel();
        lastName.setAttributeKey("lastName");
        lastName.setAttributeDisplayText("LastName");
        newListModel.add(firstName);
        newListModel.add(middleName);
        newListModel.add(lastName);
        return newListModel;
    }
    public ArrayList<MenuModel> getMainDemographic (){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        MenuModel age= new MenuModel();
        age.setAttributeKey("age1");
        age.setAttributeDisplayText("Age");

        MenuModel height= new MenuModel();
        height.setAttributeKey("height1");
        height.setAttributeDisplayText("Height");

        newListModel.add(age);
        newListModel.add(height);

        return newListModel;
    }
    public ArrayList<MenuModel> getOtherDemographic (){
        ArrayList<MenuModel> newListModel = new ArrayList<>();
        MenuModel haircolour= new MenuModel();
        haircolour.setAttributeKey("haircolour");
        haircolour.setAttributeDisplayText("Haircolour");

        MenuModel habitualdress= new MenuModel();
        habitualdress.setAttributeKey("habitualdress");
        habitualdress.setAttributeDisplayText("Habitualdress");

        newListModel.add(haircolour);
        newListModel.add(habitualdress);

        return newListModel;
    }
}
