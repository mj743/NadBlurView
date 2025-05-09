package com.nad.blurview.utils;


import android.view.View;


public class VisibleFrameTracker {

    public final View targetView;

    public int pollingLimit;

    public VisibleFrameTracker(View targetView, int pollingLimit) {
        this.targetView = targetView;
        this.pollingLimit = pollingLimit;
    }

    public VisibleFrameTracker(View targetView) {
        this(targetView, 150);
    }


    public View getView() {
        return targetView;
    }
    public void attachPreDrawListener() {
        targetView.getViewTreeObserver().addOnPreDrawListener(
                new BlurRefreshPreDrawListener(this, 1)
        );
    }
    public void detachPreDrawListener() {
        targetView.getViewTreeObserver().removeOnPreDrawListener(
                new BlurRefreshPreDrawListener(this, 1)
        );
    }
}