package com.project.zhi.tigerapp;

import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.MenuService;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.MenuTuple;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import lombok.experimental.var;
import lombok.val;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class localTest {

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertEquals(2, 1+1);

    }

    @Test
    public void parseXML (){
        Serializer serializer = new Persister();
        String input = this.getClass().getClassLoader().getResource("entities.xml").getPath();
        File source = new File(input);
       assertEquals(true, source.exists());

        try {
            Data data = serializer.read(Data.class, source);
            List<Entities> list = data.getEntitiesList();
            assertEquals(6, list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Data getData(){
        Serializer serializer = new Persister();
        String input = this.getClass().getClassLoader().getResource("entities.xml").getPath();
        File source = new File(input);
        Data data = null;
        try {
            data = serializer.read(Data.class, source);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            return data;
        }
    }

    @Test
    public void DataSourceServiceTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        assertEquals(6,getData().getEntitiesList().size());
    }
    @Test
    public void DataFilterPersonNameTest(){
        DataFilteringService dataFilteringService = new DataFilteringService();
        DataSourceServices dataSourceServices = new DataSourceServices();
        List<Entities> entities = getData().getEntitiesList();
        assertEquals("Nawwaara MUZNA", dataFilteringService.getPersonName(entities.get(1)));
    }
    @Test
    public void DataSourceServiceUniqueKeyTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        Data data = getData();
        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
        assertEquals(20,keys.size());
    }
    @Test
    public void DataSourceServiceUniqueKeyRemoveTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        MenuService menuService= new MenuService();
        Data data = getData();
        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
        assertEquals(20,keys.size());
        ArrayList<ArrayList<MenuModel>> allMenus = menuService.getAllMenus(keys);
        ArrayList<MenuModel> nameMenus = allMenus.get(0);
        Integer ds = nameMenus.size();
        assertEquals(4,nameMenus.size());
        assertEquals(6,allMenus.get(1).size());
        assertEquals(10,allMenus.get(2).size());

    }

    @Test
    public void KeyCamelTest(){
        assertEquals("Gender", Utils.displayKeyValue("gender"));
        assertEquals("Gender Gender", Utils.displayKeyValue("gender gender"));
    }

    @Test
    public void FilterServiceUniqueKeyRemoveTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        MenuService menuService= new MenuService();
        DataFilteringService dataFilteringService = new DataFilteringService();

        Data data = getData();
        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
        assertEquals(20,keys.size());
        ArrayList<ArrayList<MenuModel>> allMenus = menuService.getAllMenus(keys);
        ArrayList<MenuModel> nameMenus = allMenus.get(0);
        Integer ds = nameMenus.size();
        assertEquals(4,nameMenus.size());
        assertEquals(6,allMenus.get(1).size());
        assertEquals(10,allMenus.get(2).size());
        nameMenus.get(0).setValue("Ayu");
        ArrayList<Entities>  list = dataFilteringService.update(data.getEntitiesList(),nameMenus, allMenus.get(1), allMenus.get(2));
        assertEquals(1,list.size());

    }

}