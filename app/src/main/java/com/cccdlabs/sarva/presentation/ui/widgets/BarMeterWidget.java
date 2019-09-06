package com.cccdlabs.sarva.presentation.ui.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.cccdlabs.sarva.R;

public class BarMeterWidget extends View {

    protected static final int DEFAULT_HEIGHT                 = 40; // Configurable with android:layout_height
    protected static final int BORDER_WIDTH                   = 1;  // NOT CONFIGURABLE
    protected static final int DEFAULT_NUM_ACTIVE_BARS        = 0;
    protected static final int DEFAULT_NUM_TOTAL_BARS         = 15;
    protected static final int DEFAULT_BAR_WIDTH              = 15;
    protected static final int DEFAULT_INNER_PADDING          = 10;
    protected static final int DEFAULT_OUTER_CORNER_RADIUS    = 8;
    protected static final int DEFAULT_BAR_CORNER_RADIUS      = 3;
    protected static final int DEFAULT_BAR_FILL_COLOR         = 0xFF00AA00;
    protected static final int DEFAULT_BAR_BASE_COLOR         = 0xFF272727;
    protected static final int DEFAULT_BAR_BORDER_COLOR       = 0xFF666666;
    protected static final int DEFAULT_BORDER_COLOR           = 0xFF666666;
    protected static final int DEFAULT_INNER_FILL_COLOR       = 0xFF222222;

    private int maxWidth;
    private int maxHeight;
    private int numActiveBars;
    private int numTotalBars;
    private int barWidth;
    private int innerPadding;
    private int outerCornerRadius;
    private int barCornerRadius;
    private int barFillColor;
    private int barBaseColor;
    private int barBorderColor;
    private int borderColor;
    private int innerFillColor;

    private Paint barFillPaint;
    private Paint barBasePaint;
    private Paint barBorderPaint;
    private Paint borderPaint;
    private Paint innerFillPaint;

    private static class SavedState extends BaseSavedState {

        int numActiveBars;
        int barFillColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            numActiveBars = in.readInt();
            barFillColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(numActiveBars);
            out.writeInt(barFillColor);
        }

        @Override
        public String toString() {
            String str = "BarMeterWidget.SavedState{ numActiveBars: ";
            str += numActiveBars + ", barFillColor: " +barFillColor + " }";
            return str;
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public BarMeterWidget(Context context) {
        super(context);
    }

    public BarMeterWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BarMeterWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BarMeterWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setSaveEnabled(true);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BarMeterWidget, 0, 0);
        numActiveBars = ta.getInt(R.styleable.BarMeterWidget_numActiveBars, DEFAULT_NUM_ACTIVE_BARS);
        numTotalBars = ta.getInt(R.styleable.BarMeterWidget_numTotalBars, DEFAULT_NUM_TOTAL_BARS);
        barWidth = ta.getDimensionPixelSize(R.styleable.BarMeterWidget_barWidth, DEFAULT_BAR_WIDTH);
        innerPadding = ta.getDimensionPixelSize(R.styleable.BarMeterWidget_innerPadding, DEFAULT_INNER_PADDING);
        outerCornerRadius = ta.getDimensionPixelSize(R.styleable.BarMeterWidget_outerCornerRadius, DEFAULT_OUTER_CORNER_RADIUS);
        barCornerRadius = ta.getDimensionPixelSize(R.styleable.BarMeterWidget_barCornerRadius, DEFAULT_BAR_CORNER_RADIUS);
        barFillColor = ta.getColor(R.styleable.BarMeterWidget_barFillColor, DEFAULT_BAR_FILL_COLOR);
        barBaseColor = ta.getColor(R.styleable.BarMeterWidget_barBaseColor, DEFAULT_BAR_BASE_COLOR);
        barBorderColor = ta.getColor(R.styleable.BarMeterWidget_barBorderColor, DEFAULT_BAR_BORDER_COLOR);
        borderColor = ta.getColor(R.styleable.BarMeterWidget_borderColor, DEFAULT_BORDER_COLOR);
        innerFillColor = ta.getColor(R.styleable.BarMeterWidget_innerFillColor, DEFAULT_INNER_FILL_COLOR);
        ta.recycle();

        barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barFillPaint.setStyle(Paint.Style.FILL);
        barFillPaint.setColor(barFillColor);

        barBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBasePaint.setStyle(Paint.Style.FILL);
        barBasePaint.setColor(barBaseColor);

        barBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barBorderPaint.setStyle(Paint.Style.STROKE);
        barBorderPaint.setStrokeWidth(BORDER_WIDTH);
        barBorderPaint.setColor(barBorderColor);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(BORDER_WIDTH);
        borderPaint.setColor(borderColor);

        innerFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerFillPaint.setStyle(Paint.Style.FILL);
        innerFillPaint.setColor(innerFillColor);
    }

    public int getBarFillColor() {
        return barFillColor;
    }

    public void setBarFillColor(int colorHex) {
        barFillColor = colorHex;
        barFillPaint.setColor(barFillColor);
        invalidate();
        requestLayout();
    }

    public int getNumActiveBars() {
        return numActiveBars;
    }

    public void setNumActiveBars(int numActiveBars) {
        if (numActiveBars < 0) {
            this.numActiveBars = 0;
        } else if (numActiveBars >= numTotalBars) {
            this.numActiveBars = numTotalBars;
        } else {
            this.numActiveBars = numActiveBars;
        }
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getMeasuredWidth(widthMeasureSpec), getMeasuredHeight(heightMeasureSpec));
    }

    protected int getMeasuredWidth(int widthMeasureSpec) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        return maxWidth;
    }

    protected int getMeasuredHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specHeight = MeasureSpec.getSize(heightMeasureSpec);
        int padding = getPaddingTop() + getPaddingBottom();

        switch(specMode) {
            case MeasureSpec.EXACTLY:
                maxHeight = specHeight;
                break;
            case MeasureSpec.AT_MOST:
                if (specHeight < (DEFAULT_HEIGHT + padding)) {
                    maxHeight = specHeight;
                }
                break;
            case MeasureSpec.UNSPECIFIED:
                maxHeight = DEFAULT_HEIGHT + padding;
        }

        return maxHeight;
    }

    protected int getWidgetWidth() {
        if (maxWidth == 0) {
            return 0;
        }
        int padding = getPaddingStart() + getPaddingEnd();
        int totalBars = getMaxTotalBars();
        int widgetWidth = (totalBars * barWidth) + ((totalBars + 1) * innerPadding) + (2 * BORDER_WIDTH);
        return widgetWidth - padding;
    }

    protected int getWidgetHeight() {
        if (maxHeight == 0) {
            return 0;
        }
        int padding = getPaddingTop() + getPaddingBottom();
        return maxHeight - padding;
    }

    protected int getMaxTotalBars() {
        if (maxWidth == 0) {
            return 0;
        }
        int minWidth = (2 * (BORDER_WIDTH + innerPadding)) + barWidth; // minimum one bar + padding + border
        if (maxWidth < minWidth) {
            return 0;
        } else if (maxWidth == minWidth) {
            return 1;
        }

        float totalBarsCalc = (maxWidth - innerPadding - (2 * BORDER_WIDTH)) / (barWidth + innerPadding);
        int totalBars = (int) Math.floor(totalBarsCalc);
        return totalBars < numTotalBars ? totalBars : numTotalBars;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawContainer(canvas);
        drawBars(canvas);
    }

    protected void drawContainer(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        int xStart = getPaddingStart();
        int yStart = getPaddingTop();
        int xEnd = getWidgetWidth();
        int yEnd = yStart + getWidgetHeight();

        canvas.drawRoundRect(xStart, yStart, xEnd, yEnd, outerCornerRadius, outerCornerRadius, innerFillPaint);
        canvas.drawRoundRect(xStart, yStart, xEnd, yEnd, outerCornerRadius, outerCornerRadius, borderPaint);
    }

    protected void drawBars(Canvas canvas) {
        if (canvas == null) {
            return;
        }
        int numBars = getMaxTotalBars();
        int widgetHeight = getWidgetHeight();
        int start = BORDER_WIDTH + innerPadding;
        int barHeight = widgetHeight - (2 * start);
        int spacing = barWidth + innerPadding;
        int yStart = getPaddingTop() + BORDER_WIDTH + innerPadding;
        int yEnd = yStart + barHeight;
        int xStart, xEnd;

        for (int i = 0; i < numBars; i++) {
            xStart = start + (spacing * i);
            xEnd = xStart + barWidth;
            canvas.drawRoundRect(xStart, yStart, xEnd, yEnd, barCornerRadius, barCornerRadius, barBorderPaint);
            if (i < numActiveBars) {
                canvas.drawRoundRect(xStart, yStart, xEnd, yEnd, barCornerRadius, barCornerRadius, barFillPaint);
            } else {
                canvas.drawRoundRect(xStart, yStart, xEnd, yEnd, barCornerRadius, barCornerRadius, barBasePaint);
            }
        }
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.numActiveBars = numActiveBars;
        savedState.barFillColor = barFillColor;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        numActiveBars = savedState.numActiveBars;
        barFillColor = savedState.barFillColor;
    }
}
