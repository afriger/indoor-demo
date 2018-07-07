package com.afrig.plotter;
import android.graphics.PointF;

public class PlotterPoint
{
    private PointF point;
    private Integer pointRadius;
    private Integer pointColor;

    public PlotterPoint(PointF point)
    {
        this.point = point;
    }

    public PlotterPoint(PointF point, Integer radius, Integer color)
    {
        this.point = point;
        this.pointRadius = radius;
        this.pointColor = color;
    }

    public PlotterPoint(PointF point, Integer color)
    {
        this.point = point;
        this.pointColor = color;
    }

    public PointF getPoint()
    {
        return point;
    }

    public void setPoint(PointF point)
    {
        this.point = point;
    }

    public Integer getPointRadius()
    {
        return pointRadius;
    }

    public void setPointRadius(Integer pointRadius)
    {
        this.pointRadius = pointRadius;
    }

    public Integer getPointColor()
    {
        return pointColor;
    }

    public void setPointColor(Integer pointColor)
    {
        this.pointColor = pointColor;
    }
}

