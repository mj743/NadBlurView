package com.nad.blurview.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import androidx.annotation.NonNull;

public class BlurCanvas extends Canvas {
    public BlurCanvas(@NonNull Bitmap bitmap) {
        super(bitmap);
    }
}
