package com.afrig.utilities;
import android.os.Environment;
import android.util.Log;

import com.afrig.beacon_demo.BeaconScene;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DataFile
{
    private static final String tag = "DataFile";
    private static final File path_download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private static final File path_dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    public static final File file = new File(path_dcim, "indoor.txt");

    public static String getProperty(String key) throws IOException
    {
        FileInputStream fis = new FileInputStream(file);
        if (fis != null)
        {
            Properties properties = new Properties();
            properties.load(fis);
            return properties.getProperty(key);
        }
        return "";
    }

    public static void test()
    {
        BeaconScene bs = BeaconScene.getBeaconScene();
        AnchorPoint p = bs.getAnchorPointbyName("PETER-BEA4");
        return;
    }
}//class
