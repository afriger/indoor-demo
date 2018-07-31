package com.afrig.plotter;
import android.content.Context;
import android.util.AttributeSet;

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
        p.setPointColor(color);
        addPoint(p);
        if (p.r > 0)
        {
            AnchorPoint c = new AnchorPoint(p.getPoint(), p.r);
            addCirle(c);
        }
    }
}//class Plotter
