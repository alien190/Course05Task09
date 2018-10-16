package com.example.alien.course05task09;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
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
    public static final float VALUE_LINE_WIDTH_SCALE = 0.07f;
    public static final float DIVISION_ARC_SCALE = 0.82f;
    private final static float VALUE_TEXT_SIZE_SCALE = 0.7f;
    private final static float PERCENT_TEXT_SIZE_SCALE = 0.3f;

    private int mWidthSpecSize;
    private int mHeightSpecSize;
    private float mRadius;
    private float mCx;
    private float mCy;
    private RectF mMainBounds;
    private RectF mDivisionArcBounds;
    private Rect mValueTextBounds;
    private int mDivisionTotalCount;
    private float mDivisionAngel;
    private Paint mDivisionPaint;
    private Paint mValuePaint;
    private Paint mBackgroundPaint;
    private Paint mValueTextPaint;
    private Paint mPercentTextPaint;
    private float mDivisionLineWidth;
    private float mValueLineWidth;
    private int mValue;
    private String valueString;
    private float mDivisionArcRadius;
    private int mColorMin;
    private int mColorMax;


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
        mDivisionPaint.setAntiAlias(true);
        mDivisionPaint.setStyle(Paint.Style.STROKE);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setStyle(Paint.Style.STROKE);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mValueTextPaint = new Paint();

        mPercentTextPaint = new Paint();

        mDivisionArcBounds = new RectF();
        mMainBounds = new RectF();
        mValueTextBounds = new Rect();

        TypedArray mainTypedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleProgressBar,
                0, R.style.DefaultCircleProgressBar);

        setValue(mainTypedArray.getInteger(R.styleable.CircleProgressBar_value, 0));
        setColor(mainTypedArray.getColor(R.styleable.CircleProgressBar_color, Color.BLACK));
        setBackgroundColor(mainTypedArray.getColor(R.styleable.CircleProgressBar_backgroundColor, Color.WHITE));
        setProgressBarColorMin(mainTypedArray.getColor(R.styleable.CircleProgressBar_progressBarColorMin, Color.BLUE));
        setProgressBarColorMax(mainTypedArray.getColor(R.styleable.CircleProgressBar_progressBarColorMax, -1));
        mainTypedArray.recycle();

        //setValue(100);
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
        calculateTextSize();

        mDivisionTotalCount = DIVISION_MAIN_SECTION_COUNT * DIVISION_INTERMEDIATE_SECTION_COUNT;
        mDivisionAngel = ((float) DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL) / mDivisionTotalCount;

        setMeasuredDimension(mWidthSpecSize, mHeightSpecSize);
    }

    private void calculateTextSize() {
        float textSize = 1;
        mValueTextPaint.setTextAlign(Paint.Align.CENTER);
        mPercentTextPaint.setTextAlign(Paint.Align.CENTER);
        mValueTextPaint.setTextSize(textSize);
        while (mValueTextPaint.measureText("100") <= mRadius * VALUE_TEXT_SIZE_SCALE) {
            textSize += 1;
            mValueTextPaint.setTextSize(textSize);
        }
        mPercentTextPaint.setTextSize(mValueTextPaint.getTextSize() * PERCENT_TEXT_SIZE_SCALE);
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
        mValuePaint.setStrokeWidth(mValueLineWidth);
    }

    private void calculateBounds() {
        mDivisionArcRadius = mRadius * DIVISION_ARC_SCALE;
        mMainBounds.set(mCx - mRadius, mCy - mRadius, mCx + mRadius, mCy + mRadius);
        mDivisionArcBounds.set(mCx - mDivisionArcRadius,
                mCy - mDivisionArcRadius,
                mCx + mDivisionArcRadius,
                mCy + mDivisionArcRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawDivisions(canvas);
        drawValueArc(canvas);
        drawValueText(canvas);
    }

    private void drawValueText(Canvas canvas) {
        mValueTextPaint.getTextBounds(valueString, 0, valueString.length(), mValueTextBounds);
        canvas.drawText(String.valueOf(mValue), mCx, mCy + mValueTextBounds.height() / 2, mValueTextPaint);

        canvas.drawText("%", mCx, mCy + mValueTextBounds.height(), mPercentTextPaint);
    }

    private void drawValueArc(Canvas canvas) {

        if (mValue != 0) {
            if(mColorMax==-1) {
                mValuePaint.setColor(mColorMin);
            } else {
                mValuePaint.setColor((mColorMax-mColorMin)*mValue/100 + mColorMin);
            }

            float angel = (float) mValue / 100 * (DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL);
            canvas.drawArc(mDivisionArcBounds, DIVISION_BEGIN_ANGEL, angel, false, mValuePaint);
            drawTerminateCircle(canvas, DIVISION_BEGIN_ANGEL);
            drawTerminateCircle(canvas, DIVISION_BEGIN_ANGEL + angel);
        }
    }

    private void drawTerminateCircle(Canvas canvas, float angel) {
        Point point = new Point();
        polarToDecart(angel, mDivisionArcRadius, point);
        canvas.drawCircle(point.x, point.y, 0.01f, mValuePaint);
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

        canvas.drawArc(mDivisionArcBounds, DIVISION_BEGIN_ANGEL, DIVISION_END_ANGEL - DIVISION_BEGIN_ANGEL, false, mDivisionPaint);
        //canvas.drawArc(mDivisionArcBackgroundBounds, 0, 360, true, mBackgroundPaint);
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

    public void setColor(int color) {
        mDivisionPaint.setColor(color);
        mValueTextPaint.setColor(color);
        mPercentTextPaint.setColor(color);
        invalidate();
    }

    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
        invalidate();
    }

    private void setProgressBarColorMin(int color) {
        mColorMin = color;
        invalidate();
    }
    private void setProgressBarColorMax(int color) {
        mColorMax = color;
        invalidate();
    }
}
