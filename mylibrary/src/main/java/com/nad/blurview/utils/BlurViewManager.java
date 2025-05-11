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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.nad.blurview.NadBlur;

public class BlurViewManager implements BlurRenderer{
    private final BlurAlgorithm blurAlgorithm;
    private BlurCanvas blurCanvas;
    private Bitmap blurBitmap;
    private final NadBlur blurView;
    private int overlayColor;

    private final ViewGroup rootViewGroup;

    private boolean autoUpdateEnabled = true;
    private float blurRadius = 16f;

    private final int[] rootLocation = new int[2];
    private final int[] viewLocation = new int[2];

    // Listener untuk preDraw, akan dihandle belakangan
    public final BlurPreDrawListener preDrawListener = new BlurPreDrawListener(this, 1);

    private boolean isBlurPrepared = false;

    public BlurViewManager(NadBlur blurView, ViewGroup rootViewGroup, int overlayColor, BlurAlgorithm blurAlgorithm) {
        this.rootViewGroup = rootViewGroup;
        this.blurView = blurView;
        this.overlayColor = overlayColor;
        this.blurAlgorithm = blurAlgorithm;
        // Inject context jika diperlukan oleh algoritma
        if (blurAlgorithm instanceof RenderEffectBlurAlgorithm) {
            ((RenderEffectBlurAlgorithm) blurAlgorithm).setContext(blurView.getContext());
        }
        updateBitmapAndCanvas(blurView.getMeasuredWidth(), blurView.getMeasuredHeight());
    }
    private void updateBitmapAndCanvas(int width, int height) {
        blurAlgorithm.clear();
        boolean invalidSize = width / 6.0f <= 0 || height / 6.0f <= 0;

        if (invalidSize) {
            blurView.setWillNotDraw(true);
            return;
        }

        blurView.setWillNotDraw(false);

        int scaledWidth = (int) Math.ceil(width / 6.0f);
        int alignedWidth = scaledWidth + (64 - scaledWidth % 64) % 64;

        int scaledHeight = (int) Math.ceil((float) height / ((float) width / alignedWidth));

        blurBitmap = Bitmap.createBitmap(alignedWidth, scaledHeight, blurAlgorithm.getConfig());
        blurCanvas = new BlurCanvas(blurBitmap);
        isBlurPrepared = true;

        refreshBlur();
    }
    private void updateCanvasTransform() {
        rootViewGroup.getLocationOnScreen(rootLocation);
        blurView.getLocationOnScreen(viewLocation);

        int dx = viewLocation[0] - rootLocation[0];
        int dy = viewLocation[1] - rootLocation[1];

        float scaleY = (float) blurView.getHeight() / blurBitmap.getHeight();
        float scaleX = (float) blurView.getWidth() / blurBitmap.getWidth();

        blurCanvas.translate(-dx / scaleX, -dy / scaleY);
        blurCanvas.scale(1f / scaleX, 1f / scaleY);
    }
    public void refreshBlur() {
        try {
            if (autoUpdateEnabled && isBlurPrepared) {
                blurBitmap.eraseColor(0);
                blurCanvas.save();
                updateCanvasTransform();
                rootViewGroup.draw(blurCanvas);
                blurCanvas.restore();

                blurBitmap = blurAlgorithm.blur(blurBitmap, blurRadius);
                blurAlgorithm.prepare();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    @Override
    public BlurRenderer setOverlayColor(int color) {
        if (this.overlayColor != color) {
            this.overlayColor = color;
            this.blurView.invalidate();
        }
        return this;
    }

    @Override
    public BlurRenderer enableBlur(boolean enable) {
        this.autoUpdateEnabled = enable;
        this.blurView.invalidate();
        return this;
    }

    @Override
    public BlurRenderer enableAutoUpdate(boolean enable) {
        ViewTreeObserver observer = rootViewGroup.getViewTreeObserver();
        if (observer != null && observer.isAlive()) {
            observer.removeOnPreDrawListener(preDrawListener);
            if (enable) {
                observer.addOnPreDrawListener(preDrawListener);
            }
        }
        return this;
    }

    @Override
    public void update() {
        updateBitmapAndCanvas(blurView.getMeasuredWidth(), blurView.getMeasuredHeight());
    }

    @Override
    public void destroy() {
        enableAutoUpdate(false);
        blurAlgorithm.destroy();
        isBlurPrepared = false;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (!autoUpdateEnabled || !isBlurPrepared) return true;
        if (canvas instanceof BlurCanvas) return false;

        float scaleY = (float) blurView.getHeight() / blurBitmap.getHeight();
        float scaleX = (float) blurView.getWidth() / blurBitmap.getWidth();

        canvas.save();
        canvas.scale(scaleX, scaleY);
        blurAlgorithm.drawBlurred(canvas, blurBitmap);
        canvas.restore();

        if (overlayColor != 0) {
            canvas.drawColor(overlayColor);
        }

        return true;
    }

    @Override
    public BlurRenderer setBlurRadius(float radius) {
        this.blurRadius = radius;
        return this;
    }

}
