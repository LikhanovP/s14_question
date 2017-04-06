package com.rosa.swift.core.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;

import com.rosa.motocross.R;

/**
 * Created by inurlikaev on 22.04.2016.
 */
public class ArcProgressBar extends Drawable {

    private static final int cStartAngle = 150;
    private static final int cTotalAngle = 240;

    private RectF mRectF;
    private Paint mPaintBackground;
    private int mStartAngle;
    private int mSweepAngle = 0;
    private Paint mPaintProgress;
    private float mMaxValue;
    private Context mContext;
    private float mWidth = 0;
    private float mHeight = 0;
    private float mStrokeWidth = 0;
    private int mStartColor;
    private int mCenterColor;
    private int mEndColor;
    private int mProgressColor;
    private int mBackgroundColor;
    private boolean mGradientSet = false;

    private float getDensity() {
        float density = 1;
        if (mContext != null)
            density = mContext.getResources().getDisplayMetrics().density;
        return density;
    }

    public ArcProgressBar(Context context) {
        mContext = context;

        mRectF = new RectF();

        mProgressColor = mContext.getResources().getColor(R.color.sdvor_yellow);
        mBackgroundColor = mContext.getResources().getColor(R.color.app_gray_color);

        mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setStrokeWidth(1);
        mPaintBackground.setStrokeCap(Paint.Cap.ROUND);

        mPaintProgress = new Paint();
        mPaintProgress.setAntiAlias(true);
        mPaintProgress.setStyle(Paint.Style.STROKE);
        mPaintProgress.setStrokeWidth(1);
        mPaintProgress.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public void draw(Canvas canvas) {

        mPaintBackground.setStrokeWidth(mStrokeWidth);
        mPaintBackground.setColor(mBackgroundColor);
        mPaintProgress.setStrokeWidth(mStrokeWidth / 3 * 2);

        float offset = mStrokeWidth / 2;
        float hOffset = (mHeight - mStrokeWidth) / 8;

        float rectWidth = mWidth - offset;
        float rectHeight = mHeight - offset + hOffset;

        if (mGradientSet) {
            int colors[] = {mStartColor, mStartColor, mCenterColor, mCenterColor, mEndColor, mEndColor};
            float positions[] = {0, 0.01f, 0.28f, 0.38f, 0.56f, 0.67f};
            float centerX = rectWidth / 2 + offset;
            float centerY = (rectHeight / 2) + offset + hOffset;
            SweepGradient sg = new SweepGradient(centerX, centerY, colors, positions);
            Matrix sgMatrix = new Matrix();
            sgMatrix.setRotate(150f, centerX, centerY);
            sg.setLocalMatrix(sgMatrix);
            mPaintProgress.setShader(sg);
        } else {
            mPaintProgress.setColor(mProgressColor);
        }

        mRectF.set(offset, offset + hOffset, rectWidth, rectHeight);
        // draw background line
        canvas.drawArc(mRectF, cStartAngle, cTotalAngle, false, mPaintBackground);
        // draw progress line
        if (mSweepAngle > 0)
            canvas.drawArc(mRectF, cStartAngle, mSweepAngle, false, mPaintProgress);

    }

    @Override
    public void setAlpha(int i) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }

    public void setMaxValue(float maxValue) {
        mMaxValue = maxValue;
    }

    public void setValue(float currVal) {
        float chekedVal = currVal;
        if (chekedVal > mMaxValue) chekedVal = mMaxValue;

        mSweepAngle = Math.round((cTotalAngle / mMaxValue) * chekedVal);
        invalidateSelf();
    }

    public void setArcStrokeWidth(float width) {
        mStrokeWidth = width * getDensity();
    }

    public void setWidth(float width) {
        mWidth = width * getDensity();
    }

    public void setHeight(float height) {
        mHeight = height * getDensity();
    }

    public void setProgressColor(int color) {
        mProgressColor = mContext.getResources().getColor(color);
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = mContext.getResources().getColor(color);
    }

    public void setGradient(int startColor, int centerColor, int endColor) {
        mGradientSet = true;
        mStartColor = mContext.getResources().getColor(startColor);
        mCenterColor = mContext.getResources().getColor(centerColor);
        mEndColor = mContext.getResources().getColor(endColor);
    }
}
