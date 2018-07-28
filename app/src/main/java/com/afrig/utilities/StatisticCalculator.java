package com.afrig.utilities;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StatisticCalculator
{
    static final String tag = "Statistic";
    private static String mPrefix = null;
    private static final int DATA_COUNT = 50;
    private List<Double> mVariance = new ArrayList<>();
    public static List<Double> mData = new ArrayList<>();
    private double mMean = 0;
    private double mVarianceMean = 0;

    StatisticCalculator()
    {
    }

    public static void Reset(final String prefix)
    {
        mPrefix = (null == prefix) ? "" : prefix;
        mData.clear();
    }

    public static int Add(double d, final String name)
    {
        mData.add(d);
        if (mData.size() > DATA_COUNT)
        {
            mPrefix += (null == name) ? "" : name;
            GetStatistic(mData);
            mData.clear();
        }
        int dataCount = mData.size();
        Log.e(tag, "mean data-count: " + dataCount);
        return dataCount;
    }

    public static void GetStatistic(List<Double> data)
    {
        StatisticCalculator sc = new StatisticCalculator();
        double mean = sc.Mean(data);
        double sd = sc.StandardDeviation(data);
        String msg = mPrefix + ":: mean: " + mean + "; sd: " + sd + ";";
        Utils.write("Statistic.txt", msg);
        Log.e(tag, msg);
    }

    public double Mean(List<Double> data)
    {
        int length = data.size();
        if (0 == length)
        {
            Log.i(tag, "length of data is 0.");
            return 0;
        }
        double mean = 0;
        for (double d : data)
        {
            mean += d;
        }
        mMean = mean / length;
        return mMean;
    }

    private void Variance(List<Double> data)
    {
        int size = data.size();
        mVariance.clear();
        mVarianceMean = 0;
        if (mVariance != null && 0 < size)
        {
            for (int k = 0; k < size; ++k)
            {
                double var = (data.get(k) - mMean);
                mVariance.add(k, var);
                mVarianceMean += var;
            }
            mVarianceMean /= size;
        }
    }

    public double StandardDeviation(List<Double> data)
    {
        if (mVariance == null)// don't divide by zero!
        {
            return 0;
        }
        Variance(data);
        int size = mVariance.size();
        if (0 == size)// don't divide by zero!
        {
            return 0;
        }
        double sum = 0;
        for (int i = 0; i < size; i++)
        {
            sum = sum + (mVariance.get(i) - mVarianceMean) * (mVariance.get(i) - mVarianceMean);
        }
        double squaredDiffMean = (sum) / (size);
        double standardDev = (Math.sqrt(squaredDiffMean));
        return standardDev;
    }

    public static void Test()
    {
/*        final double[] data = new double[]
                {
                        -38, -39, -51, -81, -50, -50, -52, -50,
                        -48, -39, -38, -38, -39, -50, -49, -46,
                        -47, -40, -38, -40, -40, -39, -50, -49,
                        -39, -39, -39, -39, -38, -52, -50, -38,
                        -39, -39, -38, -36, -37, -41, -39, -52,
                        -52, -50, -48, -47, -47, -40
                };
        StatisticCalculator sc = new StatisticCalculator();
        double mean = sc.Mean(data);
        double sd = sc.StandardDeviation(data);
        KalmanFilter kalman = new KalmanFilter(3, 3);
        for (double d : data)
        {
            kalman.applyFilter(d);
        }
        double x = kalman.GetRssi();
        Log.e(tag, "mean: " + mean + "; sd: " + sd + "; rssi: " + x);*/
    }
}//StatisticCalculator
