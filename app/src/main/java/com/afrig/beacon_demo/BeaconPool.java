package com.afrig.beacon_demo;
import android.bluetooth.le.ScanResult;

import java.util.HashMap;

public class BeaconPool extends HashMap<String, BeaconDeviceAdaptation>
{
    public BeaconDeviceAdaptation get(final String key, ScanResult result)
    {
        BeaconDeviceAdaptation ret = null;
        ret = this.get(key);
        if (null == ret)
        {
            ret = new BeaconDeviceAdaptation();
        }
        if (BeaconDeviceAdaptation.Result.NEWOB == ret.Make(result))
        {
            this.put(key, ret);
        }
        return ret;
    }
}//class BeaconPool
