package com.project.zhi.tigerapp.complexmenu;

import com.project.zhi.tigerapp.Enums.AttributeType;

import lombok.Data;

@Data
public class MenuModel {
    private String attributeKey;
    private String attributeDisplayText;
    private AttributeType attributeType;
    private Double minValue;
    private Double maxValue;
    private String value;

    public String getAttributeDisplayText(){
        if(this.attributeDisplayText == null || this.attributeDisplayText.isEmpty()){
            return this.attributeKey;
        }
        return attributeDisplayText;
    }
}
