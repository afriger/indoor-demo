package com.afrig.interfaces;
import com.afrig.utilities.AnchorPoint;
import com.afrig.utilities.PointEx;

import java.util.List;

public interface ITrilateration
{
    PointEx getPosition(AnchorPoint a, AnchorPoint b, AnchorPoint c);
    List<PointEx> getPositions(AnchorPoint a, AnchorPoint b, AnchorPoint c);
}//interface ITrilateration
