package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 25/01/16.
 */
public abstract class LineRadarRenderer extends LineScatterCandleRadarRenderer {

    public static final String LOG_TAG = "MPAndroidChart";

    public LineRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws the provided path in filled mode with the provided drawable.
     *
     * @param c
     * @param filledPath
     * @param drawable
     */
    protected void drawFilledPath(Canvas c, Path filledPath, Drawable drawable) {

        if (clipPathSupported(c)) {

            c.save();
            c.clipPath(filledPath);

            drawable.setBounds((int) mViewPortHandler.contentLeft(),
                    (int) mViewPortHandler.contentTop(),
                    (int) mViewPortHandler.contentRight(),
                    (int) mViewPortHandler.contentBottom());
            drawable.draw(c);

            c.restore();
        } else {
            throw new RuntimeException("Clip path with hardware acceleration only working properly on API level 18 and above,"
                    + "Try disabling HWA by using chart.setHardwareAccelerationEnabled(false).");
        }
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     *
     * @param c
     * @param filledPath
     * @param fillColor
     * @param fillAlpha
     */
    protected void drawFilledPath(Canvas c, Path filledPath, int fillColor, int fillAlpha) {

        int color = (fillAlpha << 24) | (fillColor & 0xffffff);

        if (clipPathSupported(c)) {

            c.save();
            c.clipPath(filledPath);

            c.drawColor(color);
            c.restore();
        } else {

            // save
            Paint.Style previous = mRenderPaint.getStyle();
            int previousColor = mRenderPaint.getColor();

            // set
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(color);

            c.drawPath(filledPath, mRenderPaint);

            // restore
            mRenderPaint.setColor(previousColor);
            mRenderPaint.setStyle(previous);
        }
    }

    /**
     * Clip path with hardware acceleration only working properly on API level 18 and above.
     *
     * @return
     */
    private boolean clipPathSupported(Canvas c) {

        if (Utils.getSDKInt() >= 18) {
            return true;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 11 && !c.isHardwareAccelerated()) {
                return true;
            } else {
                Log.e(LOG_TAG, "Cannot enable/disable hardware acceleration for devices below API level 11.");
            }
        }
        return false;
    }
}
