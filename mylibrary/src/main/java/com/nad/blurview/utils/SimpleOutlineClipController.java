/*
 * MIT License
 * Copyright (c) 2024 Muhamad Jaelani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
