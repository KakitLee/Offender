package com.project.zhi.tigerapp;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.zhi.tigerapp.Entities.Attributes;
import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.Entities.Person;
import com.project.zhi.tigerapp.Entities.Record.IntelRecord;
import com.project.zhi.tigerapp.Enums.AttributeType;
import com.project.zhi.tigerapp.FaceUtils.MatchedImage;
import com.project.zhi.tigerapp.Services.DataFilteringService;
import com.project.zhi.tigerapp.Services.DataSortService;
import com.project.zhi.tigerapp.Services.DataSourceServices;
import com.project.zhi.tigerapp.Services.MenuService;
import com.project.zhi.tigerapp.Utils.Utils;
import com.project.zhi.tigerapp.complexmenu.MenuModel;
import com.project.zhi.tigerapp.complexmenu.MenuTuple;
import com.wutka.dtd.EntityExpansion;

import org.junit.Test;
import org.locationtech.spatial4j.context.SpatialContext;
import org.locationtech.spatial4j.distance.CartesianDistCalc;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.impl.CircleImpl;
import org.locationtech.spatial4j.shape.impl.PointImpl;
import org.mockito.cglib.core.Local;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)
    {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Test
    public void DataSourceUniqueTypeTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        Data data = getData();

        List<Attributes> attributes = new ArrayList<Attributes>();

        for (Entities entity: data.getEntitiesList()
                ) {
            attributes.addAll(entity.getList().stream().filter(distinctByKey(x -> x.getType())).collect(Collectors.toList()));
        }
        attributes = attributes.stream().filter(distinctByKey(x->x.getType())).collect(Collectors.toList());
        assertEquals(4,attributes.size());

    }
    @Test
    public void wildCard(){
        List<String> values = Arrays.asList("test","best","crest","zest","testy","tether","temper","teat","tempest");
        String queryStr="te*t";
        System.out.println(queryStr.contains("*"));
        queryStr= queryStr.replaceAll("\\*", "\\\\w*");
        System.out.println(queryStr);
        System.out.println(query(queryStr,values));
    }
    public Collection<String> query(String queryStr, List<String> values ) {
        List<String> list = new ArrayList<String>();
        for (String str : values) {
            if (str.matches(queryStr))
                list.add(str);
        }
        if (list.isEmpty())
            return null;
        else
            return list;

    }
        @Test
    public void DataSourceUniqueKeyTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        Data data = getData();

        List<Attributes> attributes = new ArrayList<Attributes>();

        for (Entities entity: data.getEntitiesList()
                ) {
            attributes.addAll(entity.getList().stream().filter(distinctByKey(x -> x.getAttributeKey())).collect(Collectors.toList()));
        }
        attributes = attributes.stream().filter(distinctByKey(x->x.getAttributeKey())).collect(Collectors.toList());
        assertEquals(33,attributes.size());

    }

    @Test
    public void stringToEnum(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        Data data = getData();

        List<Attributes> attributes = new ArrayList<Attributes>();

        for (Entities entity: data.getEntitiesList()
                ) {
            attributes.addAll(entity.getList().stream().filter(distinctByKey(x -> x.getAttributeKey())).collect(Collectors.toList()));
        }
        attributes = attributes.stream().filter(distinctByKey(x->x.getAttributeKey())).collect(Collectors.toList());
        String type = attributes.get(0).getType();
        AttributeType aa = AttributeType.valueOf(type.toUpperCase(Locale.ENGLISH));

        assertEquals(AttributeType.TEXT,aa);

    }

    @Test
    public void testDataValue(){
        Data data = getData();
        String value = Utils.getAttributeValues(data.getEntitiesList().get(0).getList().get(0));
        assertEquals("TIGGA",value);
        value = Utils.getAttributeValues(data.getEntitiesList().get(0).getList().get(4));
        assertEquals("thin",value);
        value = Utils.getAttributeValues(data.getEntitiesList().get(0).getList().get(2));
        assertEquals("true",value);
    }

    @Test
    public void DataSourceServiceTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();

        assertEquals(50,getData().getEntitiesList().size());
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
//        DataSourceServices dataSourceServices = new DataSourceServices();
//        MenuService menuService= new MenuService();
//        Data data = getData();
//        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
//        assertEquals(20,keys.size());
//        ArrayList<ArrayList<MenuModel>> allMenus = menuService.getAllMenus(keys);
//        ArrayList<MenuModel> nameMenus = allMenus.get(0);
//        Integer ds = nameMenus.size();
//        assertEquals(4,nameMenus.size());
//        assertEquals(6,allMenus.get(1).size());
//        assertEquals(10,allMenus.get(2).size());

    }

    @Test
    public void KeyCamelTest(){
        assertEquals("Gender", Utils.displayKeyValue("gender"));
        assertEquals("Gender Gender", Utils.displayKeyValue("gender gender"));
    }

    @Test
    public void FolderPath(){
        File file = new File("/storage/emulated/0/Download/test.xml");

        assertEquals("\\storage\\emulated\\0\\Download", file.getParent());
    }

    @Test
    public void FilterServiceUniqueKeyRemoveTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        MenuService menuService= new MenuService();
        DataFilteringService dataFilteringService = new DataFilteringService();

//        Data data = getData();
//        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
//        assertEquals(20,keys.size());
//        ArrayList<ArrayList<MenuModel>> allMenus = menuService.getAllMenus(keys);
//        ArrayList<MenuModel> nameMenus = allMenus.get(0);
//        Integer ds = nameMenus.size();
//        assertEquals(4,nameMenus.size());
//        assertEquals(6,allMenus.get(1).size());
//        assertEquals(10,allMenus.get(2).size());
//        nameMenus.get(0).setValue("Ay");
//        ArrayList<Entities>  list = dataFilteringService.update(data.getEntitiesList(),nameMenus, allMenus.get(1), allMenus.get(2));
//        assertEquals(1,list.size());

    }
    @Test
    public void SortServiceUniqueKeyRemoveTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        MenuService menuService= new MenuService();
        DataFilteringService dataFilteringService = new DataFilteringService();

        Data data = getData();
        ArrayList<String> keys =  dataSourceServices.getUniqueKey(data);
        assertEquals(20,keys.size());
        DataSortService dataSortService = new DataSortService();
        ArrayList<Attributes> attributes = dataSortService.sortAttributesGeneral(data.getEntitiesList().get(2).getList());
        assertEquals("firstname",attributes.get(0).getAttributeKey());

    }
    @Test
    public void gsonTest(){
        ArrayList<Person> people = new ArrayList<Person>();
        Entities e = new Entities();
        e.setId("1");
        Person person1 = new Person();
        person1.setEntity(e);
        Entities e2 = new Entities();
        e2.setId("2");
        Person person2 = new Person();
        person2.setEntity(e2);
        people.add(person1);
        people.add(person2);

        Gson gson = new Gson();
        String peopleObj = gson.toJson(people);

        ArrayList<Person> people2 = gson.fromJson(peopleObj,new TypeToken<ArrayList<Person>>(){}.getType());
        assertEquals(2,people2.size());

    }

    @Test
    public void ImagePathTest(){
        DataSourceServices dataSourceServices = new DataSourceServices();
        MenuService menuService= new MenuService();
        DataFilteringService dataFilteringService = new DataFilteringService();
        Data data = getData();
        assertEquals("bilat",dataSourceServices.setFileName("Bilat.jpg"));
        assertEquals("pbm",dataSourceServices.setFileName("PbM.jpg"));
        assertEquals("patrol_alpha__0087_image_1",dataSourceServices.setFileName("20170923 Patrol Alpha #0087 image 1.jpg"));
//        assertEquals("bilat",dataSourceServices.setImagePath(data).getEntitiesList().get(2).getAttachments().getFilename());
//        assertEquals("pbm",dataSourceServices.setImagePath(data).getEntitiesList().get(4).getAttachments().getFilename());
//        assertEquals("patrol_alpha__0087_image_1",dataSourceServices.setImagePath(data).getEntitiesList().get(5).getAttachments().getFilename());
    }
    @Test
    public void testIntelRecord() {
        String input = getClass().getClassLoader().getResource("record.xml").getPath();
        Serializer serializer = new Persister();
        File source = new File(input);
        assertEquals(true, source.exists());
        try {
            IntelRecord record = serializer.read(IntelRecord.class, source);
            assertEquals("MP_20170923_03_Foraging",record.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testEntities() {
        String input = getClass().getClassLoader().getResource("entities.xml").getPath();
        Serializer serializer = new Persister();
        File source = new File(input);
        assertEquals(true, source.exists());
        try {
            Data record = serializer.read(Data.class, source);
            assertEquals(null,record.getEntitiesList().get(0).getList().get(0).getLabel());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testEntitiesAndRecords() {
        String input = getClass().getClassLoader().getResource("entities.xml").getPath();
        Serializer serializer = new Persister();
        File data = new File(input);
        File record = new File(getClass().getClassLoader().getResource("record.xml").getPath());
        assertEquals(true, data.exists());
        assertEquals(true, record.exists());
        try {
            Data entities = serializer.read(Data.class, data);
            IntelRecord recordSingle = serializer.read(IntelRecord.class, record);
            ArrayList<IntelRecord> records = new ArrayList<IntelRecord>();
            records.add(recordSingle);

            DataSourceServices dataSourceService = new DataSourceServices();
            entities = dataSourceService.mergeEntitiesAndRecords(entities,records);

            assertEquals("Intercept #01",entities.getEntitiesList().get(4).getLocations().get(0).getId());
            assertEquals(103.35471041746172,entities.getEntitiesList().get(4).getRecordLocations().get(0).getLocationAttribute().getNumberValue1(),0.01);
            assertEquals(3.050261916758624,entities.getEntitiesList().get(4).getRecordLocations().get(0).getLocationAttribute().getNumberValue2(),0.01);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    @Test
//    public void testGeo() {
//        SpatialContext ctx = SpatialContext.GEO;
//        Double degree = DistanceUtils.dist2Degrees(6.76,DistanceUtils.EARTH_EQUATORIAL_RADIUS_KM);
//        PointImpl pp = new PointImpl(103.35471041746172,3.050261916758624,ctx);
//        CircleImpl cc = new CircleImpl(pp, degree,ctx);
//        CartesianDistCalc dc = new CartesianDistCalc();
//        boolean isWhitn = dc.within(pp,103.306411,3.013046,degree);
//        boolean iswhitn2 = cc.contains(103.306411,3.013046);
//        assertEquals(isWhitn,iswhitn2);
//        cc.getArea(ctx);
//
//
//
//    }




}