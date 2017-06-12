package com.roosterr.ui;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;


public class MaxNestedScrollView extends NestedScrollView {

    public MaxNestedScrollView(Context context) {
        super(context);
    }

    public MaxNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(180, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
