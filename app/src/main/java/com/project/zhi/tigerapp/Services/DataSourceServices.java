package com.project.zhi.tigerapp.Services;

import android.content.Context;

import com.project.zhi.tigerapp.Entities.Data;
import com.project.zhi.tigerapp.Entities.Entities;
import com.project.zhi.tigerapp.R;

import org.androidannotations.annotations.EBean;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

interface  IDataSourceServices{
    Data getPeopleSource(Context context);
}
@EBean
public class DataSourceServices implements IDataSourceServices {

    @Override
    public Data getPeopleSource(Context context) {
        Serializer serializer = new Persister();
        InputStream input = context.getResources().openRawResource(R.raw.entities);
        Data data = null;
        try {
            data = serializer.read(Data.class, input);
            ArrayList<Entities> list = data.getEntitiesList();
        } catch (Exception e) {
            //Likely to the issue with the data parser.
        }
        finally {
            return data;
        }
    }
}

