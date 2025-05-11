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
package com.nad.blurview.config;

import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * Konfigurasi untuk mengatur blur pada NadBlurView dan variannya.
 */
public class BlurConfig {

    public final ViewGroup blurRoot;
    public final ViewGroup blurTarget;
    public final Boolean clipToOutline;
    public final Float blurRadius;
    public final @Nullable Integer overlayColor;  // Bisa null
    public final Boolean fallbackEnabled;

    private BlurConfig(Builder builder) {
        this.blurRoot = builder.blurRoot;
        this.blurTarget = builder.blurTarget;
        this.clipToOutline = builder.clipToOutline;
        this.blurRadius = builder.blurRadius;
        this.overlayColor = builder.overlayColor;
        this.fallbackEnabled = builder.fallbackEnabled;
    }

    public static class Builder {
        private ViewGroup blurRoot;
        private ViewGroup blurTarget;
        private Boolean clipToOutline = false;
        private Float blurRadius = 16f;

        private @Nullable Integer overlayColor = null;

        private Boolean fallbackEnabled = true;

        public Builder setBlurRoot(ViewGroup blurRoot) {
            this.blurRoot = blurRoot;
            return this;
        }

        public Builder setBlurTarget(ViewGroup blurTarget) {
            this.blurTarget = blurTarget;
            return this;
        }

        public Builder setClipToOutline(Boolean clipToOutline) {
            this.clipToOutline = clipToOutline;
            return this;
        }

        public Builder setBlurRadius(@Nullable Float blurRadius) {
            this.blurRadius = blurRadius;
            return this;
        }

        public Builder setOverlayColor(@ColorInt int overlayColor) {
            this.overlayColor = overlayColor;
            return this;
        }

        public Builder setFallbackEnabled(Boolean fallbackEnabled) {
            this.fallbackEnabled = fallbackEnabled;
            return this;
        }

        public BlurConfig build() {
            if (blurRoot == null || blurTarget == null) {
                throw new IllegalArgumentException("blurRoot and blurTarget must not be null");
            }
            return new BlurConfig(this);
        }
    }
}
