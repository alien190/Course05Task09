package com.example.alien.course05task09;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {
    public static final int DIVISION_BEGIN_ANGEL = 135;
    public static final int DIVISION_END_ANGEL = 405;
    public static final int DIVISION_MAIN_SECTION_COUNT = 6;
    public static final int DIVISION_INTERMEDIATE_SECTION_COUNT = 5;
    public static final float DIVISION_MAIN_SECTION_HEIGHT_SCALE = 0.1f;
    public static final float DIVISION_INTERMEDIATE_SECTION_HEIGHT_SCALE = 0.05f;
    public static final int DIVISION_LINE_WIDTH_MIN = 5;
    public static final float DIVISION_LINE_WIDTH_SCALE = 0.01f;
    public static final int VALUE_LINE_WIDTH_MIN = 8;
    public static final float VALUE_LINE_WIDTH_SCALE = 0.02f;
    public static final float DIVISION_ARC_SCALE = 0.82f;

    private int mWidthSpecSize;
    private int mHeightSpecSize;
    private float mRadius;
    private float mCx;
    private float mCy;
    private RectF mMainBounds;
    private RectF mDivisionArcBounds;
    private RectF mDivisionArcBackgroundBounds;
    private RectF mValueArcBounds;
    private RectF mValueArcBackgroundBounds;
    private int mDivisionTotalCount;
    private float mDivisionAngel;
    private Paint mDivisionPaint;
    private Paint mValuePaint;
    private Paint mBackgroundPaint;
    private float mDivisionLineWidth;
    private float mValueLineWidth;
    private int mValue;
    private String valueString;


    public CircleProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDivisionPaint = new Paint();
        mDivisionPaint.setColor(Color.BLACK);
        mDivisionPaint.setAntiAlias(true);
        mDivisionPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint();
        mValuePaint.setColor(Color.BLACK);
        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.FILL);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(Color.WHITE);
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mDivisionArcBounds = new RectF();
        mDivisionArcBackgroundBounds = new RectF();
        mMainBounds = new RectF();
        mValueArcBounds = new RectF();
        mValueArcBackgroundBounds = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        mHeightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        mRadius = Math.min(mWidthSpecSize, mHeightSpecSize) / 2 * 0.9f;

        calculateLineWidth();

        mCx = mWidthSpecSize / 2;
        mCy = mHeightSpecSize / 2;

        calculateBounds();

        mDivisionTotalCount = DIVISION_MAIN_SECTION_COUNT * DIVISION_INTERMEDIATE_SECTION_COUNT;
        mDivisionAngel = ((float) DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL) / mDivisionTotalCount;

        setMeasuredDimension(mWidthSpecSize, mHeightSpecSize);
    }

    private void calculateLineWidth() {
        mDivisionLineWidth = mRadius * DIVISION_LINE_WIDTH_SCALE;
        if (mDivisionLineWidth < DIVISION_LINE_WIDTH_MIN) {
            mDivisionLineWidth = DIVISION_LINE_WIDTH_MIN;
        }
        mDivisionPaint.setStrokeWidth(mDivisionLineWidth);

        mValueLineWidth = mRadius * VALUE_LINE_WIDTH_SCALE;
        if (mValueLineWidth < VALUE_LINE_WIDTH_MIN) {
            mValueLineWidth = VALUE_LINE_WIDTH_MIN;
        }
        mValuePaint.setStrokeWidth(mDivisionLineWidth);
    }

    private void calculateBounds() {
        mMainBounds.set(mCx - mRadius, mCy - mRadius, mCx + mRadius, mCy + mRadius);
        float divisionArcRadius = mRadius * DIVISION_ARC_SCALE;
        float divisionBackgroundRadius = divisionArcRadius - mDivisionLineWidth;
        float valueArcRadius = (divisionArcRadius - divisionBackgroundRadius) / 2 + divisionBackgroundRadius + mDivisionLineWidth / 2;
        float valueBackgroundRadius = valueArcRadius + mDivisionLineWidth / 2;

        mDivisionArcBounds.set(mCx - divisionArcRadius,
                mCy - divisionArcRadius,
                mCx + divisionArcRadius,
                mCy + divisionArcRadius);

        mDivisionArcBackgroundBounds.set(mCx - divisionBackgroundRadius,
                mCy - divisionBackgroundRadius,
                mCx + divisionBackgroundRadius,
                mCy + divisionBackgroundRadius);

        mValueArcBounds.set(mCx - valueArcRadius,
                mCy - valueArcRadius,
                mCx + valueArcRadius,
                mCy + valueArcRadius);

        mValueArcBackgroundBounds.set(mCx - valueBackgroundRadius,
                mCy - valueBackgroundRadius,
                mCx + valueBackgroundRadius,
                mCy + valueBackgroundRadius);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDivisions(canvas);
        drawValueArc();
    }

    private void drawValueArc() {
        float angel = (float) mValue / 100 * (DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL);

    }

    private void drawDivisions(Canvas canvas) {
        Point pointStart = new Point();
        Point pointEnd = new Point();
        float centerScale;
        float currentAngel;

        canvas.drawArc(mMainBounds, 0, 360, true, mBackgroundPaint);

        for (int i = 0; i <= mDivisionTotalCount; i++) {
            currentAngel = DIVISION_BEGIN_ANGEL + i * mDivisionAngel;

            if (i % DIVISION_INTERMEDIATE_SECTION_COUNT == 0) {
                polarToDecart(currentAngel, mRadius, pointStart);
                polarToDecart(currentAngel,
                        mRadius * (1 - DIVISION_MAIN_SECTION_HEIGHT_SCALE), pointEnd);
            } else {

                centerScale = 1 - DIVISION_MAIN_SECTION_HEIGHT_SCALE / 2;
                polarToDecart(currentAngel,
                        mRadius * (centerScale + DIVISION_INTERMEDIATE_SECTION_HEIGHT_SCALE / 2), pointStart);
                polarToDecart(currentAngel,
                        mRadius * (centerScale - DIVISION_INTERMEDIATE_SECTION_HEIGHT_SCALE / 2), pointEnd);
            }
            canvas.drawLine(pointStart.x, pointStart.y, pointEnd.x, pointEnd.y, mDivisionPaint);
        }

        canvas.drawArc(mDivisionArcBounds, DIVISION_BEGIN_ANGEL, DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL, true, mDivisionPaint);
        canvas.drawArc(mDivisionArcBackgroundBounds, 0, 360, true, mBackgroundPaint);
    }

    private void polarToDecart(float angel, float radius, @NonNull Point point) {
        point.x = (int) (radius * Math.cos(Math.toRadians(-angel)) + mCx);
        point.y = (int) (mCy - radius * Math.sin(Math.toRadians(-angel)));
    }

    public void setValue(int value) {
        if (value >= 0 && value <= 100) {
            mValue = value;
        } else if (value < 0) {
            mValue = 0;
        } else {
            mValue = 100;
        }
        valueString = String.valueOf(mValue);
        invalidate();
    }
}
