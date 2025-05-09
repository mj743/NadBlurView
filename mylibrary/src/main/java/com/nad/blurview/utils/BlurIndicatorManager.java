package com.nad.blurview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.nad.blurview.NadBlurIndicator;

public class BlurIndicatorManager implements OutlineClipController {
    private final ViewGroup rootViewGroup;
    private final NadBlurIndicator blurView;
    private final BlurAlgorithm blurAlgorithm;

    private float blurRadius = 16.0f;
    private int overlayColor;

    private final int[] rootLocation = new int[2];
    private final int[] viewLocation = new int[2];

    private BlurCanvas blurCanvas;
    private Bitmap blurBitmap;

    private final ViewTreeObserver.OnPreDrawListener preDrawListener;

    private boolean blurEnabled = true;
    private boolean blurReady = false;
    public BlurIndicatorManager(NadBlurIndicator blurView, ViewGroup rootViewGroup, int overlayColor, BlurAlgorithm blurAlgorithm) {
        this.blurView = blurView;
        this.rootViewGroup = rootViewGroup;
        this.overlayColor = overlayColor;
        this.blurAlgorithm = blurAlgorithm;

        this.preDrawListener = new BlurRefreshPreDrawListener(this,0);

        if (blurAlgorithm instanceof RenderEffectBlurAlgorithm) {
            ((RenderEffectBlurAlgorithm) blurAlgorithm).setContext(blurView.getContext());
        }

        updateBitmapAndCanvas(blurView.getMeasuredWidth(), blurView.getMeasuredHeight());
    }
    @Override
    public OutlineClipController setOutlineColor(int color) {
        if (this.overlayColor != color) {
            this.overlayColor = color;
            this.blurView.invalidate();
        }
        return this;
    }

    @Override
    public OutlineClipController enableClipping(boolean enabled) {
        this.blurEnabled = enabled;
        enableAutoUpdate(enabled);
        this.blurView.invalidate();
        return this;
    }

    @Override
    public OutlineClipController enableAutoUpdate(boolean enabled) {
        ViewTreeObserver observer = rootViewGroup.getViewTreeObserver();
        observer.removeOnPreDrawListener(preDrawListener);
        if (enabled) {
            observer.addOnPreDrawListener(preDrawListener);
        }
        return this;
    }

    @Override
    public void prepare() {
        updateBitmapAndCanvas(blurView.getMeasuredWidth(), blurView.getMeasuredHeight());
    }

    @Override
    public void destroy() {
        enableAutoUpdate(false);
        blurAlgorithm.destroy();
        blurReady = false;
    }

    @Override
    public boolean draw(Canvas canvas) {
        if (!blurEnabled || !blurReady || blurBitmap == null) return true;
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
    private void updateBitmapAndCanvas(int width, int height) {
        enableAutoUpdate(true);
        blurAlgorithm.clear();

        if ((int) Math.ceil(height / 6.0f) == 0 || (int) Math.ceil(width / 6.0f) == 0) {
            blurView.setWillNotDraw(true);
            return;
        }

        blurView.setWillNotDraw(false);

        int scaledWidth = (int) Math.ceil(width / 6.0f);
        int alignedWidth = scaledWidth + (64 - scaledWidth % 64) % 64;
        int scaledHeight = (int) Math.ceil(height / ((float) width / alignedWidth));

        blurBitmap = Bitmap.createBitmap(alignedWidth, scaledHeight, blurAlgorithm.getConfig());
        blurCanvas = new BlurCanvas(blurBitmap);
        blurReady = true;

        refreshBlur();
    }

    public void refreshBlur() {
        if (!blurEnabled || !blurReady || blurBitmap == null || blurCanvas == null) return;

        blurBitmap.eraseColor(0);
        blurCanvas.save();

        rootViewGroup.getLocationOnScreen(rootLocation);
        blurView.getLocationOnScreen(viewLocation);

        int dx = viewLocation[0] - rootLocation[0];
        int dy = viewLocation[1] - rootLocation[1];

        float scaleY = (float) blurView.getHeight() / blurBitmap.getHeight();
        float scaleX = (float) blurView.getWidth() / blurBitmap.getWidth();

        blurCanvas.translate(-dx / scaleX, -dy / scaleY);
        blurCanvas.scale(1f / scaleX, 1f / scaleY);
        rootViewGroup.draw(blurCanvas);

        blurCanvas.restore();

        blurBitmap = blurAlgorithm.blur(blurBitmap, blurRadius);
        blurAlgorithm.prepare();
    }

    public void setBlurRadius(float radius) {
        this.blurRadius = radius;
    }
}
