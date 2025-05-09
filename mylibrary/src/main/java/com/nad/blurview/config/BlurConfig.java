
package com.nad.blurview.config;

import android.graphics.Color;
import android.view.ViewGroup;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
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
