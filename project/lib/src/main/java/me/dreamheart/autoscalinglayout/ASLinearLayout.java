package me.dreamheart.autoscalinglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ASLinearLayout extends LinearLayout {

    private ASViewGroupUtil mASViewGroupUtil;

    public ASLinearLayout(Context context, int designWidth, int designHeight) {
        super(context);
        mASViewGroupUtil = new ASViewGroupUtil();
        mASViewGroupUtil.init(designWidth, designHeight);
    }

    public ASLinearLayout(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(attributes);
    }

    @TargetApi(11)
    public ASLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(21)
    public ASLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (null == mASViewGroupUtil){
            mASViewGroupUtil = new ASViewGroupUtil();
            mASViewGroupUtil.init(this, attrs);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpecs[] = mASViewGroupUtil.onMeasure(this, widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(measureSpecs[0], measureSpecs[1]);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mASViewGroupUtil.onLayout(this, changed, l, t, r, b);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isInEditMode()){
            super.draw(canvas);
            return;
        }

        if (!mASViewGroupUtil.scaleSize(this)) {
            super.draw(canvas);
        }
        else
            this.invalidate();
    }
}
