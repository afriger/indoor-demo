package com.afrig.interfaces;
import com.afrig.plotter.AnchorPoint;
import com.afrig.plotter.PointEx;

import java.util.List;

public interface ITrilateration
{
    PointEx getPosition(AnchorPoint a, AnchorPoint b, AnchorPoint c);
    List<PointEx> getPositions(AnchorPoint a, AnchorPoint b, AnchorPoint c);
}//interface ITrilateration
