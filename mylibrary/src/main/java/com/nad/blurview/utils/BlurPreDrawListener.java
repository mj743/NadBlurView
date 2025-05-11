/*
 * MIT License
 * Copyright (c) 2024 Muhamad Jaelani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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