package com.ltgd.vslider;

class CommonUtil {


    static float valueCut(float value, float max, float min) {
        if (value > max)
            value = max;
        if (value < min)
            value = min;
        return value;
    }

    static float convertDpToPx(float dp, float density) {
        return dp * density;
    }

    static int getProgressFromPosition(float p1, float p2, float pT, int max) {
        return Math.round(((p2 - pT) / (p2 - p1)) * (float) max);
    }

    static float getPositionFromProgress(float p1, float p2, int progress, int max) {
        return -((float) progress / (float) max * (p2 - p1)) + p2;
    }

}
