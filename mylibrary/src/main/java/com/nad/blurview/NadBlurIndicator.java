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

package com.nad.blurview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nad.blurview.config.BlurConfig;
import com.nad.blurview.utils.BlurAlgorithm;
import com.nad.blurview.utils.BlurIndicatorManager;
import com.nad.blurview.utils.BlurOutlineProvider;
import com.nad.blurview.utils.OutlineClipController;
import com.nad.blurview.utils.RenderEffectCompatBlurAlgorithm;
import com.nad.blurview.utils.SimpleOutlineClipController;
import com.nad.blurview.utils.StackBlurAlgorithm;

public class NadBlurIndicator extends FrameLayout {

    public OutlineClipController outlineClipController;
    public int overlayColor;

    public NadBlurIndicator(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public NadBlurIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NadBlurIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.outlineClipController = new SimpleOutlineClipController();

        if (attrs != null) {
            try (TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.NadBlur, 0, 0)) {
                overlayColor = a.getColor(
                        R.styleable.NadBlur_nadBlur_overlayColor,
                        getContext().getColor(R.color.blur_default)
                );
            }
        }
    }

    private BlurAlgorithm getBlurAlgorithm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new RenderEffectCompatBlurAlgorithm();
        } else {
            return new StackBlurAlgorithm();
        }
    }

    public void setClippingEnabled(boolean enabled) {
        this.outlineClipController.enableClipping(enabled);
    }

    public void setOverlayColor(int color) {
        this.overlayColor = color;
        this.outlineClipController.setOutlineColor(color);
    }

    private BlurIndicatorManager configureInternal(ViewGroup rootView, ViewGroup blurTarget, Boolean clipToOutline, Float blurRadius) {
        float radius = Math.max(blurRadius != null ? blurRadius : 20.0f, 2.0f);
        this.outlineClipController.destroy();
        BlurIndicatorManager manager = new BlurIndicatorManager(this, blurTarget, this.overlayColor, getBlurAlgorithm());
        this.outlineClipController = manager;
        manager.setBlurRadius(radius);

        if (Boolean.TRUE.equals(clipToOutline) && rootView.getBackground() != null) {
            rootView.setClipToOutline(true);
            rootView.setOutlineProvider(new BlurOutlineProvider(rootView));
        }
        return manager;
    }

    public void configure(@NonNull BlurConfig config) {
        if (config.overlayColor != null) {
            setOverlayColor(config.overlayColor);
        }

        BlurIndicatorManager manager = configureInternal(
                config.blurRoot,
                config.blurTarget,
                config.clipToOutline,
                config.blurRadius
        );
        setClippingEnabled(Boolean.TRUE.equals(config.clipToOutline));
        manager.enableAutoUpdate(true);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (this.outlineClipController.draw(canvas)) {
            super.draw(canvas);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isHardwareAccelerated()) {
            this.outlineClipController.enableAutoUpdate(true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.outlineClipController.enableAutoUpdate(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.outlineClipController.prepare();
    }
}
