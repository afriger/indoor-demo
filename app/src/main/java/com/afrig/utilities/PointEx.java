package com.afrig.utilities;
import android.graphics.PointF;

public class PointEx extends PointF
{
    public static final double ACCURACY = 0.1;
    private static Double mAccuracy = null;

    public static Double getAccuracy()
    {
        return mAccuracy;
    }

    public static void setAccuracy(Double Accuracy)
    {
        mAccuracy = Accuracy;
    }

    public PointEx(float x, float y)
    {
        super(x, y);
    }
    public PointEx(final PointEx p)
    {
        super(p.x, p.y);
    }
    public PointEx(final PointF p)
    {
        super(p.x, p.y);
    }
    public PointEx(double x, double y)
    {
        super((float) x, (float) y);
    }

    @Override
    public boolean equals(Object o)
    {
        PointEx p = (PointEx) o;
        double acc = (mAccuracy == null) ? ACCURACY : mAccuracy;
        return (
                (p.x - acc) < this.x && this.x < (p.x + acc)
                        &&
                        (p.y - acc) < this.y && this.y < (p.y + acc));
    }

    public boolean eq(PointEx other, double accuracy)
    {
        return (
                (other.x - accuracy) < this.x && this.x < (other.x + accuracy)
                        &&
                        (other.y - accuracy) < this.y && this.y < (other.y + accuracy)
        );
    }

    public boolean eq(PointF other, double accuracy)
    {
        return (
                (other.x - accuracy) < this.x && this.x < (other.x + accuracy)
                        &&
                        (other.y - accuracy) < this.y && this.y < (other.y + accuracy)
        );
    }
}//PointEx
