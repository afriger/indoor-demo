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
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.afrig.utilities.DataFile;
import com.afrig.utilities.KalmanFilter;
import com.afrig.utilities.PointEx;
import com.afrig.plotter.Plotter;
import com.afrig.plotter.PlotterPoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    private final String tag = "SASA";
    private final String ttt = "TRACE";

    private BluetoothManager mBTManager;
    private BluetoothAdapter mBTAdapter;
    private Handler mScanHandler = new Handler();
    private boolean isScanning = false;
    private BluetoothLeScanner mBluetoothLeScanner;
    private final boolean mVersionKey = (Build.VERSION.SDK_INT < 21);
    private TextView m_res;
    private final int mMaxLines = 55;
    private int mLines = 0;
    //----------------------------------------------------------------
    private Handler mHandler = new Handler();
    private int SCAN_PERIOD = 5000;
    private ScanSettings mScanSettings;
    private List<ScanFilter> filters;
    KalmanFilter mKalman = new KalmanFilter(3.0, 3.0);
    BeaconScene mFieldprint = new BeaconScene();
    private Plotter mPlot;
    private double mDeviceX = 4.0;
    private double mDeviceY = 4.0;

    //----------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_res = findViewById(R.id.textResult2);
        m_res.setText("");
        mLines = 0;
        mPlot = findViewById(R.id.plotter_id);
        mPlot.SetPlotterSize(500, 500, 20);

  /*      // init BLE
        mBTManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        mScanHandler.post(scanRunnable);*/
    }

    @Override
    protected void onStart()
    {
        super.onStart();
/*
        if (Build.VERSION.SDK_INT >= 21)
        {
            mBluetoothLeScanner = mBTAdapter.getBluetoothLeScanner();
            mScanSettings = new ScanSettings.Builder()
                    //.setReportDelay(10)
                    //.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    //.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
                    .build();
            filters = new ArrayList<ScanFilter>();
        }
*/
//        ArrayList<PointF> list = lgorithms.test();
        //       ArrayList<PointEx> list = Test.go();
        //      toPlotter(list);
        DataFile.test();
        toPlotter();
    }

    void toPlotter()
    {
        mPlot.reset();
        mPlot.invalidate();
        BeaconScene beacons = BeaconScene.getBeaconScene1();
        beacons.addToPlotter1(mPlot, Color.BLUE);
        //PointF center = beacons.Center(mPlot,Color.RED);
        //mPlot.addLine(center, beacons.get(3).getPoint());

        mPlot.invalidate();
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
                    BeaconDeviceAdaptation bdp = BeaconDeviceAdaptation.Make(device, scanRecord, rssi);
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
                    BeaconDeviceAdaptation bdp = BeaconDeviceAdaptation.Make(result);
                    if (bdp != null && bdp.isBeacon())
                    {
                        mFieldprint.add(bdp);
                        if (mLines > mMaxLines)
                        {
                            m_res.setText("");
                            mLines = 0;
                        }
                        m_res.setTextColor(Color.BLACK);
                        //m_res.append(bdp.res() + '\n');
                        mLines++;
                        int rssi = result.getRssi();
                        double kal = mKalman.applyFilter(rssi);
                        Log.e("AD-RESS", name + " : " + device.getAddress());
                        Log.i("STAT-IC", "name: ;" + name + "; rssi: ;" + rssi + "; kal: ;" + kal);
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
        mFieldprint.clear();
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
        if (mVersionKey)//
        {
            mBTAdapter.stopLeScan(leScanCallback20);
        }
        else
        {
            mBluetoothLeScanner.stopScan(leScanCallback);
        }
//        MathTool.Point loc = mFieldprint.getLocation(mDeviceX, mDeviceY);
        m_res.setTextColor(Color.RED);
/*        m_res.setText(loc.toString());

        if (loc.isValid())
        {
            toPlotter((float) loc.x, (float) loc.y);
        }
*/
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

    private void toPlotter(ArrayList<PointEx> list)
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

