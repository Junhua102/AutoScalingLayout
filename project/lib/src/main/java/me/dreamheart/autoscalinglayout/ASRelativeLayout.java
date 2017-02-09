package me.dreamheart.autoscalinglayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class ASRelativeLayout extends RelativeLayout implements AutoScaleLayout{

    private ASViewGroupUtil mASViewGroupUtil;

    public ASRelativeLayout(Context context, int designWidth, int designHeight) {
        super(context);
        mASViewGroupUtil = new ASViewGroupUtil();
        mASViewGroupUtil.init(designWidth, designHeight);
    }

    public ASRelativeLayout(Context context, AttributeSet attributes) {
        super(context, attributes);
        init(attributes);
    }

    public ASRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(21)
    public ASRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    public boolean isAutoScaleEnable(){
        return mASViewGroupUtil.isAutoScaleEnable();
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
        //Log.i("ASRelativeLayout", "w=" + this.getWidth() + " h=" + this.getHeight());
        if (!mASViewGroupUtil.scaleSize(this)) {
            super.draw(canvas);
        }
        else
            this.invalidate();
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
    }
}
