package com.nad.blurview.utils;

import android.graphics.Outline;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

public final class BlurOutlineProvider extends ViewOutlineProvider {
    private final ViewGroup targetGroup;

    public BlurOutlineProvider(ViewGroup targetGroup) {
        this.targetGroup = targetGroup;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        if (targetGroup.getBackground() != null) {
            targetGroup.getBackground().getOutline(outline);
            outline.setAlpha(1.0f);
        }
    }
}
