package com.nad.blurview.utils;

import android.graphics.Canvas;

public interface BlurRenderer {

    BlurRenderer setBlurRadius(float radius);
    BlurRenderer setOverlayColor(int color);
    BlurRenderer enableBlur(boolean enable);
    BlurRenderer enableAutoUpdate(boolean enable);
    boolean draw(Canvas canvas);
    void update();

    // === Cleanup ===
    void destroy();
}
