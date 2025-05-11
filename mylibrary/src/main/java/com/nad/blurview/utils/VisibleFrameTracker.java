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