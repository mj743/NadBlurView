package com.nad.blurview.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.fragment.app.Fragment;

public class BlurRefreshPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
    private final Object target;
    private final int mode;

    public BlurRefreshPreDrawListener(Object target, int mode) {
        this.target = target;
        this.mode = mode;
    }

    @Override
    public boolean onPreDraw() {
        switch (mode) {
            case 0:
                if (target instanceof BlurIndicatorManager) {
                    ((BlurIndicatorManager) target).refreshBlur();
                }
                break;
            case 1:
                if (target instanceof VisibleFrameTracker) {
                    View view = ((VisibleFrameTracker) target).targetView;
                    Rect visibleFrame = new Rect();
                    view.getWindowVisibleDisplayFrame(visibleFrame);
                    int height = view.getHeight();
                    // Tambahkan aksi jika diperlukan
                }
                break;
            case 2:
                if (target instanceof Fragment) {
                    ((Fragment) target).startPostponedEnterTransition();
                }
                break;
            default:
                break;
        }
        return true;
    }
}
