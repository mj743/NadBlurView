package com.nad.blurview.utils;

import android.graphics.Canvas;
import android.graphics.Paint;

public class SimpleOutlineClipController implements OutlineClipController{
    private boolean clippingEnabled = true;
    private final Paint paint = new Paint();
    public SimpleOutlineClipController() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
    }
    @Override
    public OutlineClipController setOutlineColor(int color) {
        paint.setColor(color);
        return this;
    }

    @Override
    public OutlineClipController enableClipping(boolean enabled) {
        this.clippingEnabled = enabled;
        return this;
    }

    @Override
    public OutlineClipController enableAutoUpdate(boolean enabled) {
        return this;
    }

    @Override
    public void prepare() {

    }

    @Override
    public boolean draw(Canvas canvas) {
        if (!clippingEnabled) return false;

        int width = canvas.getWidth();
        int height = canvas.getHeight();
        canvas.drawRect(0, 0, width, height, paint);
        return true;
    }

    @Override
    public void destroy() {

    }
}
