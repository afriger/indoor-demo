package com.afrig.utilities;
import android.graphics.PointF;
import android.util.Log;

public class AnchorPoint
{
    public static final AnchorPoint NaN = new AnchorPoint(Double.NaN, Double.NaN, Double.NaN);
    public final double x;
    public final double y;
    public final double z;
    PointF point;
    public double r=0;

    public AnchorPoint(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        point = new PointF((float) this.x, (float) this.y);
        this.r = 0;
    }

    public AnchorPoint(PointF p, double r)
    {
        this.x = p.x;
        this.y = p.y;
        this.z = 0;
        point = new PointF((float) this.x, (float) this.y);
        this.r = r;
    }

    public PointF getPoint()
    {
        return point;
    }

    public AnchorPoint Inflation(double factor)
    {
        this.r *= factor;
        return this;
    }

    public double Distance(final AnchorPoint other)
    {
        double d = (x - other.x) * (x - other.x) + (y - other.y);
        if (z != Double.NaN && other.z != Double.NaN)
        {
            d += (z - other.z) * (z - other.z);
        }
        return Math.sqrt(d);
    }

    public boolean isPointBelongsToThisCircle(final PointF point, double accuracy)
    {
        double d = Math.sqrt((x - point.x) * (x - point.x) + (y - point.y) * (y - point.y));
        Log.e("TRACE-2", "d: " + d + "; r: " + r + "; " + (d <= (r+accuracy)));
        return d <= (r + accuracy);
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(x);
        sb.append(',').append(y);
        sb.append(',').append(z);
        sb.append(',').append(r);
        sb.append(')');
        return sb.toString();
    }
}// class AnchorPoint
