package com.zpj.shouji.market.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.zpj.shouji.market.R;

public class DrawableTintTextView extends AppCompatTextView {

    private int drawableTintColor;

    public DrawableTintTextView(Context context) {
        this(context, null);
    }

    public DrawableTintTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DrawableTintTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawableTintTextView);
        drawableTintColor = typedArray.getColor(R.styleable.DrawableTintTextView_drawable_tint_color, Color.TRANSPARENT);
        typedArray.recycle();
        tintDrawables();
    }

    public void setDrawableTintColor(int drawableTintColor) {
        this.drawableTintColor = drawableTintColor;
        tintDrawables();
    }

    private void tintDrawables() {
        if (drawableTintColor != Color.TRANSPARENT) {
            Drawable[] drawables = getCompoundDrawablesRelative();
            for (int i = 0; i < drawables.length; i++) {
                Drawable drawable = drawables[i];
                if (drawable != null) {
                    final Drawable wrappedDrawable = DrawableCompat.wrap(drawable.mutate());
                    DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(drawableTintColor));
                    drawables[i] = wrappedDrawable;
                }
            }
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }
}
