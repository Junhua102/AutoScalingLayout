package me.dreamheart.autoscalinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 *
 */
public class ASViewGroupUtil {

    private int mDesignWidth;
    private int mDesignHeight;
    private float mCurrentWidth;
    private float mCurrentHeight;
    private boolean mAutoScaleEnable;

    public void init(int designWidth, int designHeight){
        mDesignWidth = designWidth;
        mDesignHeight = designHeight;
        mCurrentWidth = mDesignWidth;
        mCurrentHeight = mDesignHeight;
        mAutoScaleEnable = true;
    }

    public void init(ViewGroup vg, AttributeSet attrs){
        TypedArray a = null;
        try{
            a = vg.getContext().obtainStyledAttributes(
                    attrs, R.styleable.AutoScalingLayout);

            mDesignWidth = a.getDimensionPixelOffset(R.styleable.AutoScalingLayout_designWidth, 0);
            mDesignHeight = a.getDimensionPixelOffset(R.styleable.AutoScalingLayout_designHeight, 0);
            mAutoScaleEnable = a.getBoolean(R.styleable.AutoScalingLayout_autoScaleEnable, true);
        }catch (Throwable e){
            mAutoScaleEnable = true;
            mDesignWidth = 0;
            mDesignHeight = 0;
            for (int i = 0; i < attrs.getAttributeCount(); i++){
                if ("designWidth".equals(attrs.getAttributeName(i))){
                    String designWidthStr = attrs.getAttributeValue(i);
                    mDesignWidth = getDimensionPixelOffset(vg.getContext(), designWidthStr);
                }
                else if ("designHeight".equals(attrs.getAttributeName(i))) {
                    String designHeightStr = attrs.getAttributeValue(i);
                    mDesignHeight = getDimensionPixelOffset(vg.getContext(), designHeightStr);
                }
                else if ("autoScaleEnable".equals(attrs.getAttributeName(i))) {
                    String autoScaleEnableStr = attrs.getAttributeValue(i);
                    if (autoScaleEnableStr.equals("false"))
                        mAutoScaleEnable = false;
                }
            }
        }finally {
            if(null != a)
                a.recycle();
        }

        mCurrentWidth = mDesignWidth;
        mCurrentHeight = mDesignHeight;
    }

    public boolean isAutoScaleEnable(){
        return mAutoScaleEnable;
    }

    public int[] onMeasure(ViewGroup vg, int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpecs[] = new int[2];
        measureSpecs[0] = widthMeasureSpec;
        measureSpecs[1] = heightMeasureSpec;

        if (!mAutoScaleEnable)
            return measureSpecs;

        if (0 == mDesignWidth || 0 == mDesignHeight)
            return measureSpecs;

        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);

        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);

        ViewGroup.LayoutParams params = vg.getLayoutParams();

        if (widthMode != View.MeasureSpec.EXACTLY
                && heightMode == View.MeasureSpec.EXACTLY
                && ViewGroup.LayoutParams.WRAP_CONTENT == params.width) {
            // 高度为match_parent或具体值，宽度wrap_content
            width = (height * mDesignWidth / mDesignHeight);
            measureSpecs[0] = View.MeasureSpec.makeMeasureSpec(width,
                    View.MeasureSpec.EXACTLY);
        }else if (widthMode == View.MeasureSpec.EXACTLY
                && heightMode != View.MeasureSpec.EXACTLY
                && ViewGroup.LayoutParams.WRAP_CONTENT == params.height) {
            // 宽度为match_parent或具体值，高度为wrap_content
            height = (width * mDesignHeight / mDesignWidth);
            measureSpecs[1] = View.MeasureSpec.makeMeasureSpec(height,
                    View.MeasureSpec.EXACTLY);
        }

        return measureSpecs;
    }

    public boolean scaleSize(ViewGroup vg) {
        if (!mAutoScaleEnable)
            return false;

        if (0 == mDesignWidth || 0 == mDesignHeight)
            return false;

        // Real width and height.
        int width = vg.getWidth();
        int height = vg.getHeight();

        if (0 == width || 0 == height)
            return false;

        //Log.i("ASViewGroupUtil", "scaleSize");
        // Scale if the size changed
        if(width != this.mCurrentWidth || height != this.mCurrentHeight) {
            float wScale = (float)width / this.mCurrentWidth;
            float hScale = (float)height / this.mCurrentHeight;

            float scale = Math.min(wScale, hScale);
            if (scale < 1.02 && scale > 0.98)
                return false;

            // Save the width and height
            this.mCurrentWidth = width;
            this.mCurrentHeight = height;

            //Log.i("ASViewGroupUtil", "scaleSize " + scale);
            // Scale the ViewGroup
            ScalingUtil.scaleViewRecurse(vg, scale);

            return true;
        }

        return false;
    }

    private int getDimensionPixelOffset(Context context, String value){
        if (value.endsWith("px")){
            float v = Float.parseFloat(value.substring(0, value.length() - 2));
            return (int)v;
        }else if (value.endsWith("dp")){
            float v = Float.parseFloat(value.substring(0, value.length() - 2));
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (v * density + 0.5f);
        }else if (value.endsWith("dip")){
            float v = Float.parseFloat(value.substring(0, value.length() - 3));
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (v * density + 0.5f);
        }
        return 0;
    }
}
