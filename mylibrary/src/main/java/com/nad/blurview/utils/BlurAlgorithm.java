package com.nad.blurview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface BlurAlgorithm {
    Bitmap.Config getConfig();
    void prepare();
    Bitmap blur(Bitmap bitmap, float radius);
    void drawBlurred(Canvas canvas, Bitmap bitmap);
    void clear();
    void destroy();
}
