package com.project.zhi.tigerapp.complexmenu;

import com.project.zhi.tigerapp.Entities.Attributes;

import java.util.ArrayList;

import lombok.Data;

@Data
public class MenuTuple {
    ArrayList<MenuModel> menuModels;
    ArrayList<Attributes> leftAttributes;
}
