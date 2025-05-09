package com.nad.blurview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.os.Build;

public final class RenderEffectCompatBlurAlgorithm implements BlurAlgorithm {

    private int height;
    private int width;
    private StackBlurAlgorithm fallbackAlgorithm;
    private Context context;
    private float blurRadius = 1.0f;
    private RenderNode renderNode;

    public RenderEffectCompatBlurAlgorithm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            renderNode = new RenderNode("CpnBlurRenderNodeCompat");
        }
    }
    public void setContext(Context context) {
        this.context = context;
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
        this.blurRadius = radius;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && renderNode != null) {
            int bmpHeight = bitmap.getHeight();
            int bmpWidth = bitmap.getWidth();

            if (bmpHeight != height || bmpWidth != width) {
                height = bmpHeight;
                width = bmpWidth;
                renderNode.setPosition(0, 0, width, height);
            }

            RecordingCanvas canvas = renderNode.beginRecording();
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            renderNode.endRecording();

            RenderEffect effect = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                effect = RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.MIRROR);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                renderNode.setRenderEffect(effect);
            }
        } else {
            // Fallback jika tidak mendukung RenderEffect
            if (fallbackAlgorithm == null && context != null) {
                fallbackAlgorithm = new StackBlurAlgorithm();
            }
            if (fallbackAlgorithm != null) {
                return fallbackAlgorithm.blur(bitmap, radius);
            }
        }

        return bitmap;
    }


    @Override
    public void drawBlurred(Canvas canvas, Bitmap bitmap) {
        if (canvas.isHardwareAccelerated()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                canvas.drawRenderNode(renderNode);
            }
        } else {
            if (fallbackAlgorithm == null && context != null) {
                fallbackAlgorithm = new StackBlurAlgorithm();
            }
            if (fallbackAlgorithm != null) {
                fallbackAlgorithm.blur(bitmap, blurRadius);
                fallbackAlgorithm.drawBlurred(canvas, bitmap);
            }
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
