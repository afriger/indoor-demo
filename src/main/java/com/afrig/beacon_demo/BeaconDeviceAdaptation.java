package com.afrig.beacon_demo;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import com.afrig.interfaces.IBeacon;
import com.afrig.utilities.KalmanFilter;

import static com.afrig.utilities.Utils.BytesToHex;

public class BeaconDeviceAdaptation implements IBeacon
{
    static final String tag = "BeaconDeviceParameters";
    private String mAddress;
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
    protected int mRssi;
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
    public int getRssi()
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

    public static BeaconDeviceAdaptation Make(final ScanResult result)
    {
        return BeaconDeviceAdaptation.Make(result.getDevice(), result.getScanRecord().getBytes(), result.getRssi());
    }

    public static BeaconDeviceAdaptation Make(final BluetoothDevice device, final byte[] scanRecord, int rssi)
    {
        if (device == null)
        {
            return null;
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
                return null;
            }
            startByte++;
        }
 /*       int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5)
        {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15)
            { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }
*/
        if (patternFound == false)
        {
            // This is not an iBeacon
            Log.d(tag, "This is not an iBeacon advertisment (no 4c000215 seen in bytes 2-5).  The bytes I see are: " + BytesToHex(scanRecord));
            return null;
        }
        BeaconDeviceAdaptation beacon = new BeaconDeviceAdaptation();
        beacon.mIsBeacon = true;
        beacon.mName = device.getName();
        beacon.mMajor = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);
        beacon.mMinor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);
        beacon.mTxPower = (int) scanRecord[startByte + 24]; // this one is signed
        beacon.mRssi = rssi;
        beacon.mAddress = device.getAddress();
        beacon.calculateDistance();
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
        beacon.mProximityUuid = sb.toString();
        return beacon;
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

    public void calculateDistance()
    {
        Double rssi = mKalman.applyFilter(mRssi);
        if (rssi == 0)
        {
            mDistance = -1.0; // if we cannot determine accuracy, return -1.
            return;
        }
        mRssi = (int) Math.round(rssi);
        double ratio = mRssi * 1.0 / mTxPower;
        if (ratio < 1.0)
        {
            mDistance = Math.pow(ratio, 10);
        }
        else
        {
            mDistance = (0.89976) * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    @Override
    public int compareTo(/*@NonNull */IBeacon o)
    {
        return (o.getRssi() - this.getRssi());
    }
}//class BeaconDeviceParameters
