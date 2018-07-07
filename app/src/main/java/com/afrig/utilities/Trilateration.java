package com.afrig.utilities;
import android.graphics.PointF;

import com.afrig.interfaces.ITrilateration;

import java.util.ArrayList;
import java.util.List;

public class Trilateration implements ITrilateration
{
    @Override
    public PointEx getPosition(AnchorPoint a, AnchorPoint b, AnchorPoint c)
    {
        List<PointEx> ps = circles_intersection(a, b, c);
        if (ps != null && !ps.isEmpty())
        {
            return ps.get(0);
        }
        return null;
    }

    @Override
    public List<PointEx> getPositions(AnchorPoint a, AnchorPoint b, AnchorPoint c)
    {
        return circles_intersection(a, b, c);
    }

    public static class CircumscribedCircle
    {
        private static double p2(PointF a)
        {
            return (a.x * a.x + a.y * a.y);
        }

        // Trilateration rough estimate
        // Circumscribed circle.
        public static PointF Center(PointF a, PointF b, PointF c)
        {
            double D = 2 * (a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x * (a.y - b.y));
            double a2 = p2(a), b2 = p2(b), c2 = p2(c);
            double x = (a2 * (b.y - c.y) + b2 * (c.y - a.y) + c2 * (a.y - b.y)) / D;
            double y = (a2 * (c.x - b.x) + b2 * (a.x - c.x) + c2 * (b.x - a.x)) / D;
            PointF center = new PointF((float) x, (float) y);
            return center;
        }
    }

    private List<PointEx> circles_intersection(AnchorPoint a, AnchorPoint b, AnchorPoint c)
    {
        List<PointEx> ab = circle_circle_intersection(a, b);
        if (ab == null || ab.isEmpty())
        {
            return null;
        }
        List<PointEx> ac = circle_circle_intersection(a, c);
        if (ac == null || ac.isEmpty())
        {
            return null;
        }
        List<PointEx> bc = circle_circle_intersection(b, c);
        if (bc == null || bc.isEmpty())
        {
            return null;
        }
        PointEx.setAccuracy(0.16);
        List<PointEx> all = new ArrayList<>();
        all.addAll(ab);
        all.addAll(bc);
        all.addAll(ac);
        List<PointEx> res = new ArrayList<>();
        for (PointEx p : all)
        {
            boolean ia = a.isPointBelongsToThisCircle(p, PointEx.getAccuracy());
            boolean ib = b.isPointBelongsToThisCircle(p, PointEx.getAccuracy());
            boolean ic = c.isPointBelongsToThisCircle(p, PointEx.getAccuracy());
            if (ia && ib && ic)
            {
                if (res.contains(p))
                {
                    continue;
                }
                res.add(p);
            }
        }
        List<PointEx> ret = new ArrayList<>();
        if (res.size() == 1)
        {
            PointEx t = res.get(0);
            ret.add(t);
            return ret;
        }
        if (res.size() == 3)
        {
            PointEx t = new PointEx(CircumscribedCircle.Center(res.get(0), res.get(1), res.get(2)));
            ret.add(t);
            return ret;
        }
        return null;
    }

    private List<PointEx> circle_circle_intersection(AnchorPoint b, AnchorPoint c)
    {
        List<PointEx> ret = new ArrayList<>();
        double x0 = b.x, y0 = b.y, r0 = b.r;
        double x1 = c.x, y1 = c.y, r1 = c.r;
        double a, dx, dy, d, h, rx, ry;
        double x2, y2;

        /* dx and dy are the vertical and horizontal distances between
         * the circle centers.
         */
        dx = x1 - x0;
        dy = y1 - y0;

        /* Determine the straight-line distance between the centers. */
        //d = sqrt((dy*dy) + (dx*dx));
        d = Math.hypot(dx, dy); // Suggested by Keith Briggs

        /* Check for solvability. */
        if (d > (r0 + r1))
        {
            /* no solution. circles do not intersect. */
            return ret;
        }
        if (d < Math.abs(r0 - r1))
        {
            /* no solution. one circle is contained in the other */
            return ret;
        }

        /* 'point 2' is the point where the line through the circle
         * intersection points crosses the line between the circle
         * centers.
         */

        /* Determine the distance from point 0 to point 2. */
        a = ((r0 * r0) - (r1 * r1) + (d * d)) / (2.0 * d);

        /* Determine the coordinates of point 2. */
        x2 = x0 + (dx * a / d);
        y2 = y0 + (dy * a / d);

        /* Determine the distance from point 2 to either of the
         * intersection points.
         */
        h = Math.sqrt((r0 * r0) - (a * a));

        /* Now determine the offsets of the intersection points from
         * point 2.
         */
        rx = -dy * (h / d);
        ry = dx * (h / d);

        /* Determine the absolute intersection points. */
        PointEx p1 = new PointEx((x2 + rx), (y2 + ry));
        PointEx p2 = new PointEx((x2 - rx), (y2 - ry));
        ret.add(p1);
        ret.add(p2);
        return ret;
    }
}// class Trilateration
