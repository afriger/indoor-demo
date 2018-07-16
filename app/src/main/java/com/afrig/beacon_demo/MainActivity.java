package com.afrig.beacon_demo;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.afrig.utilities.AnchorPoint;
import com.afrig.utilities.DataFile;
import com.afrig.utilities.KalmanFilter;
import com.afrig.utilities.PointEx;
import com.afrig.plotter.Plotter;
import com.afrig.plotter.PlotterPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class MainActivity extends Activity
{
    private final String tag = "BeaconDemoActivity";
    private final String ttt = "TRA-CE";
    private BluetoothManager mBTManager;
    private BluetoothAdapter mBTAdapter;
    private Handler mScanHandler = new Handler();
    private boolean isScanning = false;
    private BluetoothLeScanner mBluetoothLeScanner;
    private final boolean mVersionKey = (Build.VERSION.SDK_INT < 21);
    private TextView m_res;
    private TextView m_zero;
    //----------------------------------------------------------------
    private Handler mHandler = new Handler();
    private int SCAN_PERIOD = 2000;
    private ScanSettings mScanSettings;
    private List<ScanFilter> filters;
    BeaconPool mBeaconPool = new BeaconPool();
    BeaconsNearby mBeaconsNearby = new BeaconsNearby();
    private Plotter mPlot;
    private final boolean mProc = true;

    //----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Make to run your application only in portrait mode
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Make to run your application only in LANDSCAPE mode
        setContentView(R.layout.activity_main);
        m_res = findViewById(R.id.textResult2);
        m_res.setText("");
        m_zero = findViewById(R.id.textView_null);
        mPlot = findViewById(R.id.plotter_id);
        mPlot.SetPlotterSize(20, 20, 50);
        if (mProc)
        {
            initBLE();
        }
    }

    private void initBLE()
    {
        mBTManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        mScanHandler.post(scanRunnable);
    }

    private void startBLE()
    {
        if (Build.VERSION.SDK_INT >= 21)
        {
            mBluetoothLeScanner = mBTAdapter.getBluetoothLeScanner();
            mScanSettings = new ScanSettings.Builder()
                    .setReportDelay(0)
                    //.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    //.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (mProc)
        {
            startBLE();
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback20 = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            if (device != null)
            {
                String name = device.getName();
                if (name != null)
                {
                    BeaconDeviceAdaptation bdp = BeaconDeviceAdaptation.Create(device, scanRecord, rssi);
                    if (bdp != null && bdp.isBeacon())
                    {
                        Log.i(tag + 20, bdp.toString());
                    }
                }
            }
        }
    };
    ScanCallback leScanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            super.onScanResult(callbackType, result);
            //Log.i(tag, "callbackType " + String.valueOf(callbackType));
            BluetoothDevice device = result.getDevice();
            if (device != null)
            {
                String name = device.getName();
                if (name != null)
                {
                    BeaconDeviceAdaptation bdp = mBeaconPool.get(name, result);
                    if (bdp != null && bdp.isBeacon())
                    {
                        mBeaconsNearby.add(bdp);
                        m_res.setTextColor(Color.BLACK);
                        Log.e(ttt, "address " + name + " : " + device.getAddress());
                    }
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results)
        {
            Log.i(ttt, "onBatchScanResults");
            for (ScanResult sr : results)
            {
                Log.i("ttt", "onBatchScanResults: " + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            Log.e(tag, "Scan Failed" + "Error Code: " + errorCode);
        }
    };

    private void StartScan()
    {
        Log.e(ttt, "StartScan");
        mBeaconsNearby.clear();
        if (mVersionKey)
        {
            mBTAdapter.startLeScan(leScanCallback20);
        }
        else
        {
            //mBluetoothLeScanner.startScan(leScanCallback);
            mBluetoothLeScanner.startScan(filters, mScanSettings, leScanCallback);
        }
        m_res.setTextColor(Color.BLUE);
    }

    private void StopScan()
    {
        Log.e(ttt, "StopScan");
        if (mVersionKey)//
        {
            mBTAdapter.stopLeScan(leScanCallback20);
        }
        else
        {
            mBluetoothLeScanner.stopScan(leScanCallback);
        }
        m_res.setTextColor(Color.RED);
        BeaconsNearby.Scene scene = mBeaconsNearby.getBeaconScene();
        toPlotter(scene);
    }

    private Runnable scanRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (isScanning)
            {
                StopScan();
            }
            else
            {
                StartScan();
            }
            isScanning = !isScanning;
            mScanHandler.postDelayed(this, SCAN_PERIOD);
        }
    };

    private void toPlotter(BeaconsNearby.Scene scene)
    {
        if (scene != null)
        {
            mPlot.reset();
            mPlot.invalidate();
            if (null != scene.pos)
            {
                m_res.setText(scene.Position());
                m_zero.setText("");
            }
            else
            {
                m_zero.setText("null");
            }
            for (AnchorPoint p : scene.beacons)
            {
                mPlot.addAnchorPoint(p, Color.BLUE);
            }
            mPlot.addAnchorPoint(scene.pos, Color.RED);
            mPlot.invalidate();
        }
    }

    private void toPlotter1(ArrayList<PointEx> list)
    {
        mPlot.reset();
        mPlot.invalidate();
        if (list != null)
        {
            for (PointF p : list)
            {
                if (p.x == Double.NaN || p.y == Double.NaN)
                {
                    continue;
                }
                PlotterPoint locPoint = new PlotterPoint(new PointF(p.x, p.y), Color.RED);
                mPlot.addPoint(locPoint);
            }
        }
        //
/*
        PointEx beacon1 = new PointEx(Test.a.x, Test.a.y);
        PointEx beacon2 = new PointEx(Test.b.x, Test.b.y);
        PointEx beacon3 = new PointEx(Test.c.x, Test.c.y);
        SinglePoint ble1Point = new SinglePoint(beacon1, Color.BLUE);
        SinglePoint ble2Point = new SinglePoint(beacon2, Color.BLUE);
        SinglePoint ble3Point = new SinglePoint(beacon3, Color.BLUE);
        mPlot.addPoint(ble1Point);
        mPlot.addPoint(ble2Point);
        mPlot.addPoint(ble3Point);
        AnchorPoint c1 = new AnchorPoint(beacon1, Test.a.r);
        AnchorPoint c2 = new AnchorPoint(beacon2, Test.b.r);
        AnchorPoint c3 = new AnchorPoint(beacon3, Test.c.r);
        mPlot.addCirle(c1);
        mPlot.addCirle(c2);
        mPlot.addCirle(c3);
*/
        mPlot.invalidate();
    }
}//MainActivity

