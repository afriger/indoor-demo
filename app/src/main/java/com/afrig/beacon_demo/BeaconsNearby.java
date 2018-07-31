package com.afrig.beacon_demo;
import android.graphics.Color;
import android.util.Log;

import com.afrig.plotter.AnchorPoint;
import com.afrig.utilities.DataFile;
import com.afrig.plotter.PointEx;
import com.afrig.utilities.Trilateration;
import com.afrig.plotter.Plotter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class BeaconsNearby extends TreeSet<BeaconDeviceAdaptation>
{
    private final String tag = "BeaconsScheme";
    private final String ttt = "TRA-CE";

    public BeaconsNearby()
    {
    }

    public static class Scene
    {
        public AnchorPoint pos;
        public List<AnchorPoint> beacons = new ArrayList<>();

        public String Position()
        {
            if (pos == null)
            {
                return ("(null; null)");
            }
            return ("(" + String.format("%.3f", pos.x) + ";" + String.format("%.3f", pos.y) + ")");
        }
    }

 /*   public AnchorPoint getAnchorPointbyName(final String name)
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
    }*/

    public Scene getBeaconScene()
    {
        int size = this.size();
        Log.e(ttt, "Beacons count is " + size);
        Scene ret = new Scene();
        for (BeaconDeviceAdaptation ba : this)
        {
            AnchorPoint t = DataFile.getAnchorPoint("mac",ba.getAddress()) ; //getAnchorPointbyName(ba.getName());
            if (t == null)
            {
                continue;
            }
            t.r = ba.getDistance();
            ret.beacons.add(t);
            Log.e(ttt, "distance: " + t.r + " : " + ba.getName());
        }
        PointEx pos = null;
        if (ret.beacons.size() > 2)
        {
            Trilateration tri = new Trilateration();
            pos = tri.getPosition(ret.beacons.get(0), ret.beacons.get(1), ret.beacons.get(2));
        }
        ret.pos = (pos != null) ? new AnchorPoint(pos, 0) : null;
        return ret;
    }

    public AnchorPoint addToPlotter1(final Plotter plot, int color)
    {
/*
SASA-BEA1 : 00:15:83:00:4B:C6
SASA-BEA2 : 00:15:83:00:42:68
PETER-BEA1 : 44:EA:D8:27:39:55
PETER-BEA2 : 44:EA:D8:27:3D:6D
PETER-BEA4 : 44:EA:D8:27:2C:00
*/




/*
        public static final AnchorPoint a = new AnchorPoint(new PointF(2, 2), 5.831);
        public static final AnchorPoint b = new AnchorPoint(new PointF(8, 2), 5.831);
        public static final AnchorPoint c = new AnchorPoint(new PointF(8, 12), 5.831);
*/
        Map<String, AnchorPoint> map = new HashMap();
        BeaconsNearby bs = new BeaconsNearby();
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
