package com.nad.blurview;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nad.blurview.utils.BlurAlgorithm;
import com.nad.blurview.utils.BlurOutlineProvider;
import com.nad.blurview.utils.BlurRenderer;
import com.nad.blurview.utils.BlurViewManager;
import com.nad.blurview.utils.NoOpRenderer;
import com.nad.blurview.utils.RenderEffectBlurAlgorithm;
import com.nad.blurview.utils.StackBlurAlgorithm;

import java.util.Objects;


public class NadBlur extends FrameLayout {

    public BlurRenderer blurRenderer;
    public int overlayColor;

    public NadBlur(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public NadBlur(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NadBlur(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        blurRenderer = new NoOpRenderer();
        if (attrs != null) {
            try (TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.NadBlur, 0, 0)) {
                overlayColor = a.getColor(
                        R.styleable.NadBlur_nadBlur_overlayColor,
                        getContext().getColor(R.color.blur_default)
                );
            }
        }


    }

    private BlurAlgorithm getBlurAlgorithm() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return new RenderEffectBlurAlgorithm();
        } else {
            return new StackBlurAlgorithm();
        }
    }

    public void setupWithFallback(ViewGroup rootView, ViewGroup blurTarget, Boolean clipToOutline, Float requestedRadius) {
        float capped = Math.max(2f, Math.min(requestedRadius != null ? requestedRadius : 20f, 25f));
        blurRenderer.destroy();

        BlurViewManager blurManager = new BlurViewManager(this, blurTarget, this.overlayColor, getBlurAlgorithm());
        this.blurRenderer = blurManager;
        blurManager.setBlurRadius(capped);

        if (!Objects.equals(clipToOutline, Boolean.TRUE) || rootView.getBackground() == null) {
            return;
        }

        rootView.setClipToOutline(true);
        rootView.setOutlineProvider(new BlurOutlineProvider(rootView));
    }

    public void attachToRoot(ViewGroup rootView) {
        BlurAlgorithm blurAlgorithm = getBlurAlgorithm();
        blurRenderer.destroy();
        blurRenderer = new BlurViewManager(this, rootView, overlayColor, blurAlgorithm);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (blurRenderer != null) {
            try {
                if (blurRenderer.draw(canvas)) {
                    super.onDraw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (blurRenderer != null) {
            blurRenderer.enableAutoUpdate(false);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (blurRenderer != null) {
            blurRenderer.update();
        }
    }
    public void setOverlayColorInternal(int overlayColor) {
        this.overlayColor = overlayColor;
    }

    public BlurRenderer getBlurRenderer() {
        return blurRenderer;
    }
}