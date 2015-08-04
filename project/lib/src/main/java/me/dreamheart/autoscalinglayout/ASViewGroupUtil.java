package me.dreamheart.autoscalinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewGroup自动缩放组件
 */
public class ASViewGroupUtil {

    private static final int TYPE_FIT_INSIDE = 0;
    private static final int TYPE_FIT_WIDTH = 1;
    private static final int TYPE_FIT_HEIGHT = 2;

    // 原设计宽高
    private int mDesignWidth;
    private int mDesignHeight;
    // 当前宽高
    private float mCurrentWidth;
    private float mCurrentHeight;
    // 是否开启自动缩放
    private boolean mAutoScaleEnable;
    // 缩放模式
    private int mScaleType;

    // 直接用宽高初始化
    public void init(int designWidth, int designHeight){
        mDesignWidth = designWidth;
        mDesignHeight = designHeight;
        mCurrentWidth = mDesignWidth;
        mCurrentHeight = mDesignHeight;
        mAutoScaleEnable = true;
        mScaleType = TYPE_FIT_INSIDE;
    }

    // 用AttributeSet初始化
    public void init(ViewGroup vg, AttributeSet attrs){
        mScaleType = TYPE_FIT_INSIDE;
        String scaleTypeStr = null;
        TypedArray a = null;
        try{
            a = vg.getContext().obtainStyledAttributes(
                    attrs, R.styleable.AutoScalingLayout);

            // 获得设计宽高
            mDesignWidth = a.getDimensionPixelOffset(R.styleable.AutoScalingLayout_designWidth, 0);
            mDesignHeight = a.getDimensionPixelOffset(R.styleable.AutoScalingLayout_designHeight, 0);
            // 是否开启自动缩放
            mAutoScaleEnable = a.getBoolean(R.styleable.AutoScalingLayout_autoScaleEnable, true);
            scaleTypeStr = a.getString(R.styleable.AutoScalingLayout_autoScaleType);
        }catch (Throwable e){
            // 用户使用jar时，没有R.styleable.AutoScalingLayout，需要根据字符串解析参数
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
                else if ("autoScaleType".equals(attrs.getAttributeName(i))) {
                    scaleTypeStr = attrs.getAttributeValue(i);
                }
            }
        }finally {
            if(null != a)
                a.recycle();
        }

        if (null != scaleTypeStr){
            if (scaleTypeStr.equals("fitWidth"))
                mScaleType = TYPE_FIT_WIDTH;
            else if (scaleTypeStr.equals("fitHeight"))
                mScaleType = TYPE_FIT_HEIGHT;
        }

        mCurrentWidth = mDesignWidth;
        mCurrentHeight = mDesignHeight;

        // 背景为空时，不进入draw函数，这里必须设置默认背景
        if (null == vg.getBackground())
            vg.setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 是否开启自动缩放
     * @return true or false
     */
    public boolean isAutoScaleEnable(){
        return mAutoScaleEnable;
    }

    /**
     * 测量宽高(只有一方数值确定，另一方为WRAP_CONTENT才需要测量，用于保持纵横比)
     * @param vg ViewGroup
     * @param widthMeasureSpec  宽度
     * @param heightMeasureSpec 高度
     * @return 测量好的宽高
     */
    public int[] onMeasure(ViewGroup vg, int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpecs[] = new int[2];
        measureSpecs[0] = widthMeasureSpec;
        measureSpecs[1] = heightMeasureSpec;

        if (!mAutoScaleEnable)
            return measureSpecs;

        if (0 == mDesignWidth || 0 == mDesignHeight)
            return measureSpecs;

        if ( TYPE_FIT_INSIDE != mScaleType)
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

    /**
     * 缩放ViewGroup
     * @param vg    ViewGroup
     * @return true表示进行了缩放 false表示不需要缩放
     */
    public boolean scaleSize(ViewGroup vg) {
        if (!mAutoScaleEnable)
            return false;

        if (0 == mDesignWidth && TYPE_FIT_HEIGHT != mScaleType)
            return false;

        if (0 == mDesignHeight && TYPE_FIT_WIDTH != mScaleType)
            return false;

        // 当前宽高
        int width = vg.getWidth();
        int height = vg.getHeight();

        if (0 == width || 0 == height)
            return false;

        //Log.i("ASViewGroupUtil", "scaleSize");
        // 如果大小改变则进行缩放
        if(width != this.mCurrentWidth || height != this.mCurrentHeight) {
            // 计算缩放比例
            float scale;

            if (TYPE_FIT_HEIGHT == mScaleType)
                scale = (float)height / this.mCurrentHeight;
            else if (TYPE_FIT_WIDTH == mScaleType)
                scale = (float)width / this.mCurrentWidth;
            else {
                float wScale = (float)width / this.mCurrentWidth;
                float hScale = (float)height / this.mCurrentHeight;
                scale = Math.min(wScale, hScale);
            }

            if (scale < 1.02 && scale > 0.98)
                return false;

            // 保存当前宽高
            this.mCurrentWidth = width;
            this.mCurrentHeight = height;

            //Log.i("ASViewGroupUtil", "scaleSize " + scale);
            // 缩放ViewGroup
            ScalingUtil.scaleViewAndChildren(vg, scale, 0);

            return true;
        }

        return false;
    }

    /**
     * 获取dimension的像素值
     * @param context View的Context
     * @param value dimension的字符串
     * @return 像素值
     */
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
