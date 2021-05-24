package com.ltgd.vslider.utils;

public class CommonUtil {

    public static float valueCut(float value, float max, float min) {
        if (value > max)
            value = max;
        if (value < min)
            value = min;
        return value;
    }

}
