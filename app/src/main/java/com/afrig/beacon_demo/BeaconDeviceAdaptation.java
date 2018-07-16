package com.afrig.beacon_demo;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.afrig.interfaces.IBeacon;
import com.afrig.utilities.DataFile;
import com.afrig.utilities.KalmanFilter;
import com.afrig.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.afrig.utilities.Utils.BytesToHex;

public class BeaconDeviceAdaptation implements IBeacon
{
    static final String tag = "BeaconDeviceParameters";
    private final String ttt = "TRA-CE";
    public static enum Result
    {
        EXIST,
        NEWOB,
        ERROR
    }
    private String mAddress = null;
    private Boolean mIsBeacon = false;
    protected String mName = null;
    /**
     * A 16 byte UUID that typically represents the company owning a number of iBeacons
     * Example: E2C56DB5-DFFB-48D2-B060-D0F5A71096E0
     */
    protected String mProximityUuid = null;
    /**
     * A 16 bit integer typically used to represent a group of iBeacons
     */
    protected int mMajor = -1;
    /**
     * A 16 bit integer that identifies a specific iBeacon within a group
     */
    protected int mMinor = -1;
    protected int mTxPower = 0;
    protected double mRssi;
    private double mDistance = -1;
    private KalmanFilter mKalman = null;

    public BeaconDeviceAdaptation()
    {
        mKalman = new KalmanFilter(3.0, 3.0);
    }

    @Override
    public String getName()
    {
        return mName;
    }

    @Override
    public int getMajor()
    {
        return mMajor;
    }

    @Override
    public int getMinor()
    {
        return mMinor;
    }

    @Override
    public int getProximity()
    {
        return 0;
    }

    @Override
    public double getRssi()
    {
        return mRssi;
    }

    @Override
    public int getTxPower()
    {
        return mTxPower;
    }

    @Override
    public double getDistance()
    {
        calculateDistance();
        return mDistance;
    }

    @Override
    public String getProximityUuid()
    {
        return mProximityUuid;
    }

    @Override
    public String getAddress()
    {
        return mAddress;
    }

    @Override
    public boolean isBeacon()
    {
        return mIsBeacon;
    }

    public Result Make(final ScanResult result)
    {
        return Make(result.getDevice(), result.getScanRecord().getBytes(), result.getRssi());
    }

    public Result Make(final BluetoothDevice device, final byte[] scanRecord, int rssi)
    {
        if (device == null)
        {
            return Result.ERROR;
        }
        if (this.mAddress == device.getAddress())
        {
            mRssi = mKalman.applyFilter(rssi);
            return Result.EXIST;
        }
        int startByte = 0;
        boolean patternFound = false;
        while (startByte <= 5)
        {
            if (((int) scanRecord[startByte] & 0xff) == 0x4c &&
                    ((int) scanRecord[startByte + 1] & 0xff) == 0x00 &&
                    ((int) scanRecord[startByte + 2] & 0xff) == 0x02 &&
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
            {
                // yes!  This is an iBeacon
                patternFound = true;
                break;
            }
            else if (((int) scanRecord[startByte] & 0xff) == 0x2d &&
                    ((int) scanRecord[startByte + 1] & 0xff) == 0x24 &&
                    ((int) scanRecord[startByte + 2] & 0xff) == 0xbf &&
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x16)
            {
                return Result.ERROR;
            }
            startByte++;
        }
        if (patternFound == false)
        {
            // This is not an iBeacon
            Log.d(tag, "This is not an iBeacon advertisment (no 4c000215 seen in bytes 2-5).  The bytes I see are: " + BytesToHex(scanRecord));
            return Result.ERROR;
        }
        this.mIsBeacon = true;
        this.mAddress = device.getAddress();
        this.mName = device.getName();
        this.mMajor = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
        this.mMinor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
        this.mTxPower = (int) scanRecord[startByte + 24]; // this one is signed
        //this.mTxPower = setTxpower(this.mAddress);
        this.mRssi = mKalman.applyFilter(rssi);
        this.mProximityUuid = getProximityUuid(scanRecord, startByte);
        return Result.NEWOB;
    }

    public int setTxpower(final String address)
    {
        JSONObject obj = DataFile.getJSONObject("mac", address);
        if (null != obj)
        {
            try
            {
                return obj.getInt("tx");
            } catch (JSONException e)
            {
                Log.e(ttt, e.getMessage());
            }
        }
        return -59;
    }

    private static String getProximityUuid(byte[] scanRecord, int startByte)
    {
        // beacon.calculateDistance();
        // AirLocate:
        // 02 01 1a 1a ff 4c 00 02 15  # Apple's fixed iBeacon advertising prefix
        // e2 c5 6d b5 df fb 48 d2 b0 60 d0 f5 a7 10 96 e0 # iBeacon profile uuid
        // 00 00 # major
        // 00 00 # minor
        // c5 # The 2's complement of the calibrated Tx Power
        // Estimote:
        // 02 01 1a 11 07 2d 24 bf 16
        // 394b31ba3f486415ab376e5c0f09457374696d6f7465426561636f6e00000000000000000000000000000000000000000000000000
        byte[] proximityUuidBytes = new byte[16];
        System.arraycopy(scanRecord, startByte + 4, proximityUuidBytes, 0, 16);
        String hexString = BytesToHex(proximityUuidBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0, 8));
        sb.append("-");
        sb.append(hexString.substring(8, 12));
        sb.append("-");
        sb.append(hexString.substring(12, 16));
        sb.append("-");
        sb.append(hexString.substring(16, 20));
        sb.append("-");
        sb.append(hexString.substring(20, 32));
        return sb.toString();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(mName);
        sb.append("; Major: ").append(mMajor);
        sb.append("; Minor: ").append(mMinor);
        sb.append("; Tx: ").append(mTxPower);
        sb.append("; RSSI: ").append(mRssi);
        sb.append("; UUID: ").append(mProximityUuid);
        return sb.toString();
    }

    public String res()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(mName).append(" (").append(mMajor).append(":").append(mMinor).append(")");
        sb.append("; RSSI: ").append(mRssi);
//        sb.append("; Tx: ").append(mTxPower).append("; RSSI: ").append(mRssi);
        sb.append("; d:").append(String.format("%.3f", mDistance));
        //sb.append("\nUUID: ").append(mProximityUuid);
        return sb.toString();
    }

    private final static double RSSI_TO_DISTANCE_A = 60;
    private final static double RSSI_TO_DISTANCE_N = 3.3;

    public static double rssiToDistance(double rssi)
    {
        return Math.pow(10, (Math.abs(rssi) - RSSI_TO_DISTANCE_A) / (10 * RSSI_TO_DISTANCE_N));
    }

    public void calculateDistance()
    {
        Double rssi = mKalman.applyFilter(mRssi);
        if (rssi == 0)
        {
            mDistance = -1.0; // if we cannot determine accuracy, return -1.
            return;
        }
        mRssi = (int) Math.round(rssi);
        mDistance = rssiToDistance(mRssi);

/*
        double ratio = mRssi * 1.0 / mTxPower;
        if (ratio < 1.0)
        {
            mDistance = Math.pow(ratio, 10);
        }
        else
        {
            mDistance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
        Log.e(ttt, "distance-1: " +mDistance+ " : "+rssiToDistance(mRssi)+"rssi: " +rssi+"; Tx:"+mTxPower);
*/
    }

    @Override
    public int compareTo(/*@NonNull */IBeacon o)
    {
        return (int) Math.round(o.getRssi() - this.getRssi());
    }
}//class BeaconDeviceParameters
