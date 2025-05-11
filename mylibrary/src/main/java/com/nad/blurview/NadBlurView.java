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
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nad.blurview.config.BlurConfig;
import com.nad.blurview.utils.BlurRenderer;
import com.nad.blurview.utils.BlurViewManager;
import com.nad.blurview.utils.NoOpRenderer;

import java.util.Objects;

public class NadBlurView extends FrameLayout {

    private NadBlur cpnBlur;

    public NadBlurView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public NadBlurView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NadBlurView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        cpnBlur = new NadBlur(context, attrs);
        cpnBlur.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));
        addView(cpnBlur);
    }

    public void attachToRoot(@NonNull ViewGroup rootView) {
        if (cpnBlur != null) {
            cpnBlur.attachToRoot(rootView);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    public void configureBlur(@NonNull ViewGroup blurRoot,
                              @NonNull ViewGroup blurTarget,
                              @Nullable Boolean clipToOutline,
                              @Nullable Float blurRadius,
                              int overlayColor,
                              @Nullable Boolean fallbackEnabled) {

        if (cpnBlur == null) throw new IllegalStateException("CpnBlur is not initialized");

        cpnBlur.setOverlayColorInternal(overlayColor);
        BlurRenderer renderer = cpnBlur.getBlurRenderer().setOverlayColor(overlayColor);

        if (!(renderer instanceof NoOpRenderer)) {
            if (renderer instanceof BlurViewManager) {
                renderer.update();
            }
        } else {
            if (!Objects.equals(fallbackEnabled, Boolean.TRUE)) {
                setBlurEnabled(false);
                return;
            }
            cpnBlur.setupWithFallback(blurRoot, blurTarget, clipToOutline, blurRadius);
        }
    }

    public static void configureBlurCompat(NadBlurView view,
                                           ViewGroup blurRoot,
                                           ViewGroup blurTarget,
                                           Boolean clipToOutline,
                                           Float blurRadius,
                                           Boolean fallbackEnabled,
                                           int bitmask) {
        if ((bitmask & 4) != 0) clipToOutline = Boolean.FALSE;
        if ((bitmask & 8) != 0) blurRadius = null;
        if ((bitmask & 16) != 0) fallbackEnabled = Boolean.FALSE;

        view.configureBlur(blurRoot, blurTarget, clipToOutline, blurRadius, 0, fallbackEnabled);
    }

    public void fallbackBlurSetup(ViewGroup root, ViewGroup target, Boolean clip, Float radius) {
        if (cpnBlur != null) {
            cpnBlur.setupWithFallback(root, target, clip, radius);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    public void setBlurAutoUpdate(boolean autoUpdate) {
        if (cpnBlur != null) {
            cpnBlur.getBlurRenderer().enableAutoUpdate(autoUpdate);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    public void setBlurEnabled(boolean enabled) {
        if (cpnBlur != null) {
            cpnBlur.getBlurRenderer().enableBlur(enabled);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    public void setBlurRadius(float radius) {
        if (cpnBlur != null) {
            cpnBlur.getBlurRenderer().setBlurRadius(radius);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    public void setOverlayColor(int color) {
        if (cpnBlur != null) {
            cpnBlur.setOverlayColorInternal(color);
            cpnBlur.getBlurRenderer().setOverlayColor(color);
        } else {
            throw new IllegalStateException("CpnBlur is not initialized");
        }
    }

    /**
     * Konfigurasi blur dengan menggunakan BlurConfig builder.
     */
    public void configure(@NonNull BlurConfig config) {
        if (config.overlayColor != null) {
            setOverlayColor(config.overlayColor);
        }

        configureBlur(
                config.blurRoot,
                config.blurTarget,
                config.clipToOutline,
                config.blurRadius,
                config.overlayColor != null ? config.overlayColor : this.cpnBlur.overlayColor,
                config.fallbackEnabled
        );
    }
}
