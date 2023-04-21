package com.sgk.myworldmogilev.helper;

import android.graphics.Color;

public class GradientBackgroundHelper {

    public int getGradientColors(int startColor, int endColor, int size, int position) {

        int color;

        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        ValueInterpolator interpolatorR = new ValueInterpolator(0, size - 1, endR, startR);
        ValueInterpolator interpolatorG = new ValueInterpolator(0, size - 1, endG, startG);
        ValueInterpolator interpolatorB = new ValueInterpolator(0, size - 1, endB, startB);

        color = Color.argb(255, (int) interpolatorR.map(position), (int) interpolatorG.map(position), (int) interpolatorB.map(position));

        return color;
    }
}
