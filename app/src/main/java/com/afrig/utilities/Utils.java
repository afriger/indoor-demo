package com.afrig.utilities;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Utils
{
    private static final String ttt = "TRA-CE";

    /**
     * bytesToHex method
     */
    public static String BytesToHex(byte[] in)
    {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : in)
        {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private static final File path_download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

    public static void write(final String fileName, final String str)
    {
        if (fileName == null)
        {
            Log.e(ttt, "fileName is null");
            return;
        }
        String content;
        if (str == null)
        {
            content = "null";
        }
        else
        {
            content = str;
        }
        content += '\n';
        File file = new File(path_download, fileName);
        try
        {
            FileWriter out = new FileWriter(file, true);
            out.write(content);
            out.close();
        } catch (IOException e)
        {
            Log.e(ttt, e.getMessage());
        }
    }
}// class Utils
