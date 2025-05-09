package com.nad.blurview.utils;

import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

public class BlurPreDrawListener implements ViewTreeObserver.OnPreDrawListener {

    private final int mode;
    private final Object target;

    public BlurPreDrawListener(@NonNull Object target, int mode) {
        this.mode = mode;
        this.target = target;
    }

    @Override
    public boolean onPreDraw() {
        switch (mode) {
            case 0:
                if (target instanceof CoordinatorLayout) {
                    ((CoordinatorLayout) target).setVisibility(View.VISIBLE); // kemungkinan reset behavior/anchor
                }
                break;
            case 1:
                if (target instanceof BlurViewManager) {
                    ((BlurViewManager) target).refreshBlur();
                }
                break;
        }
        return true;
    }
}