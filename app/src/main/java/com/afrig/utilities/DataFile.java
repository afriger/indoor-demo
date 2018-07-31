package com.afrig.utilities;
import android.os.Environment;
import android.util.Log;

import com.afrig.plotter.AnchorPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class DataFile
{
    private static final String tag = "DataFile";
    private static final String prefix = "i";
    private static final File path_download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static final File path_dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    public static final File file = new File(path_dcim, "indoor.txt");
    public static final File file1 = new File(path_dcim, "idoor.txt");

    public static String getProperty(String key) throws IOException
    {
        FileInputStream fis = new FileInputStream(file1);
        if (fis != null)
        {
            Properties properties = new Properties();
            properties.load(fis);
            return properties.getProperty(key);
        }
        return "";
    }

    public static Properties getProperty()
    {
        try
        {
            FileInputStream fis = new FileInputStream(file1);
            if (fis != null)
            {
                Properties properties = new Properties();
                try
                {
                    properties.load(fis);
                    return properties;
                } catch (IOException e1)
                {
                    Log.e(tag, e1.getMessage());
                }
            }
        } catch (FileNotFoundException e)
        {
            Log.e(tag, e.getMessage());
        }
        return null;
    }

    public static JSONObject getJSONObject(final String name, final String value)
    {
        Properties prop = getProperty();
        if (prop == null)
        {
            return null;
        }
        int size = Integer.parseInt(prop.getProperty("size"));
        try
        {
            for (int k = 0; k < size; ++k)
            {
                String sarr = prop.getProperty(prefix + k);
                JSONObject obj = new JSONObject(sarr);
                if (value.compareToIgnoreCase(obj.getString(name)) == 0)
                {
                    return obj;
                }
            }
        } catch (JSONException i)
        {
            Log.e(tag, i.getMessage());
        }
        return null;
    }

    public static AnchorPoint getAnchorPoint(final String name, final String value)
    {
        Properties prop = getProperty();
        if (prop == null)
        {
            return null;
        }
        int size = Integer.parseInt(prop.getProperty("size"));
        try
        {
            for (int k = 0; k < size; ++k)
            {
                String sarr = prop.getProperty(prefix + k);
                JSONObject obj = new JSONObject(sarr);
                if (value.compareToIgnoreCase(obj.getString(name)) == 0)
                {
                    AnchorPoint ap = new AnchorPoint(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z"));
                    return ap;
                }
            }
        } catch (JSONException i)
        {
            Log.e(tag, i.getMessage());
        }
        return null;
    }
}//class
