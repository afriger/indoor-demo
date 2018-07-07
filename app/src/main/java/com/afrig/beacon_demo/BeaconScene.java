package com.afrig.beacon_demo;
import android.graphics.Color;
import android.util.Log;

import com.afrig.utilities.AnchorPoint;
import com.afrig.utilities.DataFile;
import com.afrig.utilities.PointEx;
import com.afrig.utilities.Trilateration;
import com.afrig.plotter.Plotter;
import com.afrig.plotter.PlotterPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class BeaconScene extends TreeSet<BeaconDeviceAdaptation>
{
    private final String ttt = "TRACE-1";
    private static Map<String, AnchorPoint> map = new HashMap();

    public BeaconScene()
    {
    }

    public AnchorPoint getAnchorPointbyName(final String name)
    {
        try
        {
            String json = DataFile.getProperty(name);
            JSONObject obj = null;
            try
            {
                obj = new JSONObject(json);
                AnchorPoint ap = new AnchorPoint(obj.getDouble("x"), obj.getDouble("y"), obj.getDouble("z"));
                return ap;
            } catch (JSONException e)
            {
                Log.e(ttt, e.getMessage());
            }
        } catch (IOException e)
        {
            Log.e(ttt, e.getMessage());
        }
        return AnchorPoint.NaN;
    }

    private AnchorPoint getAnchorPointbyName1(final String name)
    {
        return map.get(name);
    }
    public static BeaconScene getBeaconScene()
    {
        BeaconScene bs = new BeaconScene();
        return bs;
    }
        public static BeaconScene getBeaconScene1()
    {
        BeaconScene bs = new BeaconScene();
        map.put("PETER-BEA1", new AnchorPoint(2, 2, 0));
        map.put("PETER-BEA2", new AnchorPoint(8, 2, 0));
        map.put("PETER-BEA4", new AnchorPoint(8, 12, 0));
        //map.put("SASA-BEA1", new AnchorPoint(2, 21, 0));
        //map.put("SASA-BEA2", new AnchorPoint(2, 2, 0));
        /*

        public static final AnchorPoint a = new AnchorPoint(new PointF(2, 2), 5.831);
        public static final AnchorPoint b = new AnchorPoint(new PointF(8, 2), 5.831);
        public static final AnchorPoint c = new AnchorPoint(new PointF(8, 12), 5.831);
         */
        return bs;
    }

    public void AnchorPointToPlotter1(final AnchorPoint p, final Plotter plot, int color)
    {
        PlotterPoint bp = new PlotterPoint(p.getPoint(), color);
        plot.addPoint(bp);
        if (p.r > 0)
        {
            AnchorPoint c = new AnchorPoint(p.getPoint(), p.r);
            plot.addCirle(c);
        }
    }

    public void addToPlotter1(final Plotter plot, int color)
    {
        AnchorPoint a = map.get("PETER-BEA1");
        AnchorPoint b = map.get("PETER-BEA2");
        AnchorPoint c = map.get("PETER-BEA4");
        a.r = 5.831;
        b.r = 5.831;
        c.r = 5.831;
        AnchorPointToPlotter1(a, plot, color);
        AnchorPointToPlotter1(b, plot, color);
        AnchorPointToPlotter1(c, plot, color);
        Trilateration tri = new Trilateration();
        List<PointEx> res = tri.getPositions(a, b, c);
        for (PointEx x : res)
        {
            AnchorPoint p = new AnchorPoint(x, 0);
            AnchorPointToPlotter1(p, plot, Color.RED);
        }
    }

    public void addToPlotter(final Plotter plot, int color)
    {
        int k = 0;
        for (BeaconDeviceAdaptation beacon : this)
        {
            AnchorPoint p = getAnchorPointbyName1(beacon.getName());
            p.r = 5.831;//beacon.getDistance();
            PlotterPoint bp = new PlotterPoint(p.getPoint(), color);
            plot.addPoint(bp);
            if (p.r > 0)
            {
                AnchorPoint c = new AnchorPoint(p.getPoint(), p.r);
                plot.addCirle(c);
            }
            k++;
        }
    }


/*
    public AnchorPoint getLocation()
    {
        if (this.size() < 3)
        {
            return AnchorPoint.NaN;
        }
        AnchorPoint p[] = new AnchorPoint[3];
        int k = 0;
        for (BeaconDeviceAdaptation b : this)
        {
            if (k > 2)
            {
                break;
            }
            p[k] = getAnchorPointbyName(b.getName());
            p[k].r = b.getDistance();
            k++;
        }
        return Calculate(p[0],p[1],p[2]);
    }
*/
}//class EMFieldprint
