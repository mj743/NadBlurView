package com.nad.blurview.utils;

import android.graphics.Canvas;

public class NoOpRenderer implements BlurRenderer {
    @Override
    public BlurRenderer setBlurRadius(float radius) {
        return this;
    }

    @Override
    public BlurRenderer setOverlayColor(int color) {
        return this;
    }

    @Override
    public BlurRenderer enableBlur(boolean enable) {
        return this;
    }

    @Override
    public BlurRenderer enableAutoUpdate(boolean enable) {
        return this;
    }

    @Override
    public boolean draw(Canvas canvas) {
        return true;
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }
}
