package com.nad.blurview.utils;

import android.graphics.Canvas;

public interface OutlineClipController {

    OutlineClipController setOutlineColor(int color);
    OutlineClipController enableClipping(boolean enabled);
    OutlineClipController enableAutoUpdate(boolean enabled);
    void prepare();
    boolean draw(Canvas canvas);
    void destroy();
}
