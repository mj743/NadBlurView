package com.nad.blurview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class StackBlurAlgorithm implements BlurAlgorithm {
    public final Paint blurPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    public Bitmap.Config getConfig() {
        return Bitmap.Config.ARGB_8888;
    }

    @Override
    public void prepare() {

    }

    @Override
    public Bitmap blur(Bitmap bitmap, float radius) {
        return BlurUtils.blur(bitmap, (int) radius);
    }

    @Override
    public void drawBlurred(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, 0f, 0f, blurPaint);

    }

    @Override
    public void clear() {

    }

    @Override
    public void destroy() {

    }
}
