package com.sgk.myworldmogilev.helper;

public class ValueInterpolator {
    private final float mRangeMapFromMin;
    private final float mRangeMapToMin;
    private final float mScaleFactor;

    public ValueInterpolator(float[] rangeMapFrom, float[] rangeMapTo) {
        this(rangeMapFrom[0], rangeMapFrom[1], rangeMapTo[0], rangeMapTo[1]);
    }

    public ValueInterpolator(float rangeMapFromMin, float rangeMapFromMax, float rangeMapToMin, float rangeMapToMax) {
        mRangeMapFromMin = rangeMapFromMin;
        mRangeMapToMin = rangeMapToMin;

        float rangeMapFromSpan = rangeMapFromMax - rangeMapFromMin;
        float rangeMapToSpan = rangeMapToMax - rangeMapToMin;

        mScaleFactor = rangeMapToSpan / rangeMapFromSpan;
    }

    public float map(float value) {
        return mRangeMapToMin + ((value - mRangeMapFromMin) * mScaleFactor);
    }
}
