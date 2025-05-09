package com.nad.blurview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.os.Build;

public class RenderEffectBlurAlgorithm implements BlurAlgorithm {
    private int height;
    private int width;
    private StackBlurAlgorithm fallbackAlgorithm;
    private  Context context;
    private RenderNode renderNode;
    private float blurRadius = 1.0f;
    public RenderEffectBlurAlgorithm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            renderNode = new RenderNode("CpnBlurRenderNodeCompat");
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
    @Override
    public Bitmap.Config getConfig() {
        return Bitmap.Config.ARGB_8888;
    }
    @Override
    public void prepare() {}
    @Override
    public void clear() {}

    @Override
    public Bitmap blur(Bitmap bitmap, float radius) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (fallbackAlgorithm == null) {
                fallbackAlgorithm = new StackBlurAlgorithm();
            }
            return fallbackAlgorithm.blur(bitmap, radius);
        }

        this.blurRadius = radius;
        int bmpHeight = bitmap.getHeight();
        int bmpWidth = bitmap.getWidth();
        if (bmpHeight != height || bmpWidth != width) {
            height = bmpHeight;
            width = bmpWidth;
            renderNode.setPosition(0, 0, width, height);
        }
        var canvas = renderNode.beginRecording();
        canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);
        renderNode.endRecording();

        RenderEffect effect = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            effect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.MIRROR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            renderNode.setRenderEffect(effect);
        }

        return bitmap;
    }
    @Override
    public void drawBlurred(Canvas canvas, Bitmap bitmap) {
        if (canvas.isHardwareAccelerated() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            canvas.drawRenderNode(renderNode);
        } else {
            if (fallbackAlgorithm == null) {
                fallbackAlgorithm = new StackBlurAlgorithm();
            }
            fallbackAlgorithm.blur(bitmap, blurRadius);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, fallbackAlgorithm.blurPaint);
        }
    }

    @Override
    public void destroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            renderNode.discardDisplayList();
        }
        if (fallbackAlgorithm != null) {
            fallbackAlgorithm.destroy();
        }
    }
}
