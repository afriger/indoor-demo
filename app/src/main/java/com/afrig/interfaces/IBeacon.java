package com.afrig.interfaces;
public interface IBeacon extends Comparable<IBeacon>
{
    String getName();

    int getMajor();

    int getMinor();

    int getProximity();

    double getRssi();

    int getTxPower();

    double getDistance();

    String getProximityUuid();

    String getAddress();

    boolean isBeacon();

    String toString();
}//interface IBeacon
