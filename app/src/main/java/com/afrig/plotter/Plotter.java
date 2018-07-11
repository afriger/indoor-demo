package com.afrig.plotter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.afrig.utilities.AnchorPoint;

import java.util.ArrayList;
import java.util.List;

//import android.support.annotation.Nullable;
public class Plotter extends PlotterBasic
{
    public Plotter(Context context)
    {
        super(context);
        savePoints(true);
    }

    public Plotter(Context context,/* @Nullable*/ AttributeSet attrs)
    {
        super(context, attrs);
        savePoints(true);
    }

    public Plotter(Context context, /*@Nullable*/ AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        savePoints(true);
    }

    public void addAnchorPoint(final AnchorPoint p, int color)
    {
        if (p == null)
        {
            return;
        }
        PlotterPoint bp = new PlotterPoint(p.getPoint(), color);
        addPoint(bp);
        if (p.r > 0)
        {
            AnchorPoint c = new AnchorPoint(p.getPoint(), p.r);
            addCirle(c);
        }
    }
}//class Plotter
