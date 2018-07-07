package com.afrig.utilities;
public class Utils
{
    /**
     * bytesToHex method
     */
    public static String BytesToHex(byte[] in)
    {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : in)
        {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}// class Utils
