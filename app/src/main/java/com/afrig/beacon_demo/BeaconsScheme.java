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

public class BeaconsScheme extends TreeSet<BeaconDeviceAdaptation>
{
    private final String tag = "BeaconsScheme";
    private final String ttt = "TRACE-1";

    public BeaconsScheme()
    {
    }

    public static class Scene
    {
        public final AnchorPoint a;
        public final AnchorPoint b;
        public final AnchorPoint c;
        public final AnchorPoint pos;

        public Scene(final AnchorPoint a, final AnchorPoint b, final AnchorPoint c, final AnchorPoint pos)
        {
            this.a = a;
            this.b = b;
            this.c = c;
            this.pos = pos;
        }
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
        return null;
    }

    public Scene getBeaconScene()
    {
        int size = this.size();
        if (size < 3)
        {
            return null;
        }
        AnchorPoint p[] = new AnchorPoint[4];
        int k = 0;
        for (BeaconDeviceAdaptation ba : this)
        {
            AnchorPoint t = getAnchorPointbyName(ba.getName());
            if (t == null)
            {
                continue;
            }
            p[k] = t;
            p[k].r = ba.getDistance();
            if ((k++) > 2)
            {
                break;
            }
        }
        Trilateration tri = new Trilateration();
        PointEx pos = tri.getPosition(p[0], p[1], p[2]);
        p[3] = new AnchorPoint(pos, 0);
        Scene ret = new Scene(p[0], p[1], p[2], p[3]);
        return ret;
    }


    public AnchorPoint addToPlotter1(final Plotter plot, int color)
    {

/*
        public static final AnchorPoint a = new AnchorPoint(new PointF(2, 2), 5.831);
        public static final AnchorPoint b = new AnchorPoint(new PointF(8, 2), 5.831);
        public static final AnchorPoint c = new AnchorPoint(new PointF(8, 12), 5.831);
*/

        Map<String, AnchorPoint> map = new HashMap();
        BeaconsScheme bs = new BeaconsScheme();
        map.put("PETER-BEA1", new AnchorPoint(2, 2, 0));
        map.put("PETER-BEA2", new AnchorPoint(8, 2, 0));
        map.put("PETER-BEA4", new AnchorPoint(8, 12, 0));
        AnchorPoint a = map.get("PETER-BEA1");
        AnchorPoint b = map.get("PETER-BEA2");
        AnchorPoint c = map.get("PETER-BEA4");
        a.r = 5.831;
        b.r = 5.831;
        c.r = 5.831;
        plot.addAnchorPoint(a, color);
        plot.addAnchorPoint(b, color);
        plot.addAnchorPoint(c, color);
        Trilateration tri = new Trilateration();
        List<PointEx> res = tri.getPositions(a, b, c);
        for (PointEx x : res)
        {
            AnchorPoint p = new AnchorPoint(x, 0);
            plot.addAnchorPoint(p, Color.RED);
            return p;
        }
        return null;
    }
}//class EMFieldprint
