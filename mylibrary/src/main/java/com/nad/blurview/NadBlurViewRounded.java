
package com.nad.blurview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NadBlurViewRounded extends NadBlurView {

    private float defaultRadius = 0f;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;

    private final Path clipPath = new Path();
    private final RectF rectF = new RectF();
    private boolean pathInvalid = true;
    private boolean shouldClipChildren = true;

    public NadBlurViewRounded(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public NadBlurViewRounded(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NadBlurViewRounded(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs != null) {
            try (TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NadBlur, 0, 0)) {
                defaultRadius = a.getDimension(R.styleable.NadBlur_nadBlur_cornerRadius, 0f);
                topLeftRadius = a.getDimension(R.styleable.NadBlur_nadBlur_topLeftCornerRadius, defaultRadius);
                topRightRadius = a.getDimension(R.styleable.NadBlur_nadBlur_topRightCornerRadius, defaultRadius);
                bottomLeftRadius = a.getDimension(R.styleable.NadBlur_nadBlur_bottomLeftCornerRadius, defaultRadius);
                bottomRightRadius = a.getDimension(R.styleable.NadBlur_nadBlur_bottomRightCornerRadius, defaultRadius);
            }
        }

        setOutlineProvider(new CustomOutlineProvider());
        setClipToOutline(true);
        setLayerType(View.LAYER_TYPE_NONE, null);
    }

    private class CustomOutlineProvider extends ViewOutlineProvider {
        @Override
        public void getOutline(View view, Outline outline) {
            if (view.getWidth() > 0 && view.getHeight() > 0) {
                float maxRadius = Math.max(Math.max(topLeftRadius, topRightRadius),
                        Math.max(bottomLeftRadius, bottomRightRadius));
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), maxRadius);
            }
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (!shouldClipChildren || getWidth() == 0 || getHeight() == 0 || getRootView() == null) {
            super.dispatchDraw(canvas);
            return;
        }

        if (pathInvalid) {
            rectF.set(0f, 0f, getWidth(), getHeight());
            float[] radii = new float[]{
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
            };
            clipPath.reset();
            clipPath.addRoundRect(rectF, radii, Path.Direction.CW);
            pathInvalid = false;
        }

        int save = canvas.save();
        canvas.clipPath(clipPath);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    // ==== Public API =====

    public void setCornerRadius(float radius) {
        this.defaultRadius = radius;
        this.topLeftRadius = radius;
        this.topRightRadius = radius;
        this.bottomLeftRadius = radius;
        this.bottomRightRadius = radius;
        pathInvalid = true;
        invalidate();
        invalidateOutline();
    }

    public void setTopLeftCornerRadius(float radius) {
        this.topLeftRadius = radius;
        pathInvalid = true;
        invalidate();
        invalidateOutline();
    }

    public void setTopRightCornerRadius(float radius) {
        this.topRightRadius = radius;
        pathInvalid = true;
        invalidate();
        invalidateOutline();
    }

    public void setBottomLeftCornerRadius(float radius) {
        this.bottomLeftRadius = radius;
        pathInvalid = true;
        invalidate();
        invalidateOutline();
    }

    public void setBottomRightCornerRadius(float radius) {
        this.bottomRightRadius = radius;
        pathInvalid = true;
        invalidate();
        invalidateOutline();
    }

    public void setClipChildrenEnabled(boolean enabled) {
        this.shouldClipChildren = enabled;
        invalidate();
    }

    public boolean isClipChildrenEnabled() {
        return shouldClipChildren;
    }

    public Path getClipPath() {
        return clipPath;
    }
}
