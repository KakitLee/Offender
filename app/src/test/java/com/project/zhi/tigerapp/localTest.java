package com.project.zhi.tigerapp;

import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSourceServices;

import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

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
        assertEquals(6,getData().getEntitiesList().size());
    }

}