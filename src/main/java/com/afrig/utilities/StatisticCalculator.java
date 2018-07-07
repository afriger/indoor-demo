package com.afrig.utilities;

import android.util.Log;

public class StatisticCalculator
{
    static final String tag = "Statistic";
    private double[] m_variance = null;
    private double m_mean = 0;
    private double m_variance_mean = 0;

    StatisticCalculator()
    {

    }

    public double Mean(double[] data)
    {
        int length = data.length;
        if (0 == length)
        {
            Log.i(tag, "length of data is 0.");
            return 0;
        }
        m_variance = new double[length];
        double mean = 0;
        for (double d : data)
        {
            mean += d;
        }
        m_mean = mean / length;
        return m_mean;
    }

    private void Variance(double[] data)
    {
        m_variance_mean = 0;
        if (m_variance != null && data.length > 0)
        {
            for (int k = 0; k < data.length; ++k)
            {
                m_variance[k] = (data[k] - m_mean);
                m_variance_mean += m_variance[k];
            }
            m_variance_mean /= data.length;
        }
    }

    public double StandardDeviation(double[] data)
    {
        if (m_variance == null)// don't divide by zero!
        {
            return 0;
        }
        Variance(data);
        if (0 == m_variance.length)// don't divide by zero!
        {
            return 0;
        }
        double sum = 0;

        for (int i = 0; i < m_variance.length; i++)
        {
            sum = sum + (m_variance[i] - m_variance_mean) * (m_variance[i] - m_variance_mean);
        }
        double squaredDiffMean = (sum) / (m_variance.length);
        double standardDev = (Math.sqrt(squaredDiffMean));

        return standardDev;
    }

    public static void GetStatistic(double[] data)
    {
        StatisticCalculator sc = new StatisticCalculator();
        double mean = sc.Mean(data);
        double sd = sc.StandardDeviation(data);
        Log.e(tag, "mean: " + mean + "; sd: " + sd + ";");
    }

    public static void Test()
    {
        final double[] data = new double[]
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

        KalmanFilter kalman = new KalmanFilter(3,3);
        for (double d : data)
        {
            kalman.applyFilter(d);
        }
        double x = kalman.GetRssi();
        Log.e(tag, "mean: " + mean + "; sd: " + sd + "; rssi: " + x);

    }

}//StatisticCalculator
