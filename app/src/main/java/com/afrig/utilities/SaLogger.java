package com.afrig.utilities;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SaLogger
{
    private final String tag = "TRACE-BEA";
    final String extendedFile = "extendedInfo.txt";
    final File _dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    final File _extended = new File(_dir, extendedFile);
    private static final int BUFFER = 1024;
    private static final long MAX_SIZE = 1024 * 1024;
    //No info , No debug
    public final static int none = 6;
    public final static int fatal = 5;
    public final static int error = 4;
    public final static int warning = 3;
    public final static int info = 2;
    public final static int debug = 1;
    public final static int trace = 0;
    public static int mSeverity = none;
    public static boolean mToFile2 = false;

    public void printStack(String msg, Exception exc)
    {
        if (mSeverity == none)
        {
            return;
        }
        if (exc != null)
        {
            if (msg != null && msg.length() > 0)
            {
                android.util.Log.w(tag, Thread.currentThread().getName() + ": " + msg, exc);
            }
            else
            {
                android.util.Log.w(tag, Thread.currentThread().getName(), exc);
            }
        }
    }

    public String getStackTraceString(Throwable tr)
    {
        return android.util.Log.getStackTraceString(tr);
    }

    private String args2str(Object[] args)
    {
        StringBuilder sb = new StringBuilder(Thread.currentThread().getName());
        for (final Object str : args)
        {
            sb.append(":");
            sb.append(str);
        }
        return sb.toString();
    }

    public void AppendToFile(final File file, final String data)
    {
        if (null == file)
        {
            return;
        }
        if (null == data)
        {
            return;
        }
        try
        {
            boolean noClear = (file.length() < MAX_SIZE);
            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyy HH:mm:ss.SSS", Locale.US);
            String date = formatter.format(new java.util.Date());
            FileWriter fw = new FileWriter(file, noClear);
            String msg = "\n" + date + " : " + data;
            fw.write(msg);
            fw.flush();
            fw.close();
        } catch (IOException e)
        {
            // String msg = e.getMessage();
        }
    }

    void AppendToLogcatFile(final String data)
    {
        if (mToFile2)
        {
            AppendToFile(_extended, data);
        }
    }

    void PrintAndSave(final Object... s)
    {
        String msg = args2str(s);
        AppendToLogcatFile(msg);
        android.util.Log.e(tag, msg);
    }

    public void n(final Object... s)
    {
        PrintAndSave(s);
    }

    public void f(final Object... s)
    {
        if (mSeverity > fatal)
        {
            return;
        }
        PrintAndSave(s);
    }

    public void e(final Object... s)
    {
        if (mSeverity > error)
        {
            return;
        }
        PrintAndSave(s);
    }

    public void w(final Object... s)
    {
        if (mSeverity > warning)
        {
            return;
        }
        PrintAndSave(s);
    }

    public void i(final Object... s)
    {
        if (mSeverity > info)
        {
            return;
        }
        PrintAndSave(s);
    }

    public void d(final Object... s)
    {
        if (mSeverity > debug)
        {
            return;
        }
        PrintAndSave(s);
    }

    public void t(final Object... s)
    {
        if (mSeverity > trace)
        {
            return;
        }
        PrintAndSave(s);
    }

    public static SaLogger Log = new SaLogger();

    void test()
    {
    }
}//class Logger
