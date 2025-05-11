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

import android.graphics.Bitmap;
import android.os.Looper;

public class BlurUtils {

    private static final ThreadLocal<BlurBuffers> buffersThreadLocal = ThreadLocal.withInitial(BlurBuffers::new);

    public static Bitmap blur(Bitmap input, int radius, float scale, boolean canReuse) {
        if (radius < 1 || input == null || input.isRecycled()) return null;

        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Do not call blur on the main thread!");
        }

        radius = Math.min(radius, 25);
        int width = Math.max(1, Math.round(input.getWidth() * scale));
        int height = Math.max(1, Math.round(input.getHeight() * scale));

        Bitmap scaledBitmap;
        try {
            scaledBitmap = (scale != 1.0f)
                    ? Bitmap.createScaledBitmap(input, width, height, false)
                    : input;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Bitmap bitmap;
        try {
            bitmap = canReuse ? scaledBitmap : scaledBitmap.copy(Bitmap.Config.ARGB_8888, true);
        } catch (OutOfMemoryError | NullPointerException e) {
            e.printStackTrace();
            return null;
        }

        return applyStackBlur(bitmap, radius);
    }

    public static Bitmap blur(Bitmap input, int radius) {
        return blur(input, radius, 0.5f, false);
    }

    public static Bitmap applyStackBlur(Bitmap bitmap, int radius) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int wh = w * h;

            int[] pixels = new int[wh];
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

            BlurBuffers buffers = buffersThreadLocal.get();
            if (buffers == null) {
                buffers = new BlurBuffers();
                buffersThreadLocal.set(buffers);
            }
            buffers.ensureCapacity(wh, Math.max(w, h), radius);

            int[] r = buffers.r, g = buffers.g, b = buffers.b, dv = buffers.dv, vmin = buffers.vmin;
            int[][] stack = buffers.stack;

            int div = radius * 2 + 1;
            int yi = 0, yw = 0;

            for (int y = 0; y < h; y++) {
                int rinsum = 0, ginsum = 0, binsum = 0;
                int routsum = 0, goutsum = 0, boutsum = 0;
                int rsum = 0, gsum = 0, bsum = 0;

                for (int i = -radius; i <= radius; i++) {
                    int x = clamp(i, w - 1);
                    int p = pixels[yi + x];
                    int[] sir = stack[i + radius];
                    sir[0] = (p >> 16) & 0xff;
                    sir[1] = (p >> 8) & 0xff;
                    sir[2] = p & 0xff;

                    int rbs = radius + 1 - Math.abs(i);
                    rsum += sir[0] * rbs;
                    gsum += sir[1] * rbs;
                    bsum += sir[2] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }
                }

                int stackPointer = radius;

                for (int x = 0; x < w; x++) {
                    r[yi] = dv[rsum];
                    g[yi] = dv[gsum];
                    b[yi] = dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    int stackStart = (stackPointer - radius + div) % div;
                    int[] sir = stack[stackStart];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    int nextPixelIndex = yw + vmin[Math.min(x + radius + 1, w - 1)];
                    int p = pixels[nextPixelIndex];
                    sir[0] = (p >> 16) & 0xff;
                    sir[1] = (p >> 8) & 0xff;
                    sir[2] = p & 0xff;

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackPointer = (stackPointer + 1) % div;
                    sir = stack[stackPointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi++;
                }
                yw += w;
            }

            for (int x = 0; x < w; x++) {
                int rinsum = 0, ginsum = 0, binsum = 0;
                int routsum = 0, goutsum = 0, boutsum = 0;
                int rsum = 0, gsum = 0, bsum = 0;

                int yp = -radius * w;
                for (int i = -radius; i <= radius; i++) {
                    int yi2 = clamp(yp, h - 1) * w + x;
                    int[] sir = stack[i + radius];

                    sir[0] = r[yi2];
                    sir[1] = g[yi2];
                    sir[2] = b[yi2];

                    int rbs = radius + 1 - Math.abs(i);
                    rsum += r[yi2] * rbs;
                    gsum += g[yi2] * rbs;
                    bsum += b[yi2] * rbs;

                    if (i > 0) {
                        rinsum += sir[0];
                        ginsum += sir[1];
                        binsum += sir[2];
                    } else {
                        routsum += sir[0];
                        goutsum += sir[1];
                        boutsum += sir[2];
                    }

                    if (i < h - 1) yp++;
                }

                int yi2 = x;
                int stackPointer = radius;
                for (int y = 0; y < h; y++) {
                    pixels[yi2] = (0xff000000) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                    rsum -= routsum;
                    gsum -= goutsum;
                    bsum -= boutsum;

                    int stackStart = (stackPointer - radius + div) % div;
                    int[] sir = stack[stackStart];

                    routsum -= sir[0];
                    goutsum -= sir[1];
                    boutsum -= sir[2];

                    int p = x + vmin[Math.min(y + radius + 1, h - 1)] * w;
                    sir[0] = r[p];
                    sir[1] = g[p];
                    sir[2] = b[p];

                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];

                    rsum += rinsum;
                    gsum += ginsum;
                    bsum += binsum;

                    stackPointer = (stackPointer + 1) % div;
                    sir = stack[stackPointer];

                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];

                    rinsum -= sir[0];
                    ginsum -= sir[1];
                    binsum -= sir[2];

                    yi2 += w;
                }
            }

            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (Throwable t) {
            t.printStackTrace();
            return bitmap;
        }
    }

    private static int clamp(int val, int max) {
        return Math.max(0, Math.min(max, val));
    }

    private static class BlurBuffers {
        int[] r, g, b, dv, vmin;
        int[][] stack;

        void ensureCapacity(int pixelCount, int maxDim, int radius) {
            if (r == null || r.length < pixelCount) {
                r = new int[pixelCount];
                g = new int[pixelCount];
                b = new int[pixelCount];
            }

            int div = radius * 2 + 1;
            if (dv == null || dv.length < 256 * div * div) {
                dv = new int[256 * div * div];
                for (int i = 0; i < dv.length; i++) {
                    dv[i] = i / (div * div);
                }
            }

            if (stack == null || stack.length != div) {
                stack = new int[div][3];
            }

            if (vmin == null || vmin.length < maxDim) {
                vmin = new int[maxDim];
                for (int i = 0; i < maxDim; i++) vmin[i] = i;
            }
        }
    }
}