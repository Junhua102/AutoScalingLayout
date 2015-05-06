package me.dreamheart.autoscalinglayout;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

public class ScalingUtil {

    public static void scaleViewRecurse(View view, float factor) {
        try{
            Method method = view.getClass().getMethod("isAutoScaleEnable");
            if(!(Boolean)method.invoke(view))
                return;
        }catch (Exception e){
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // Scale if not MATCH_PARENT WRAP_CONTENT ...
        if(layoutParams.width > 0) {
            layoutParams.width *= factor;
        }
        if(layoutParams.height > 0) {
            layoutParams.height *= factor;
        }

        // Scale margin
        if(layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)layoutParams;
            marginParams.leftMargin *= factor;
            marginParams.topMargin *= factor;
            marginParams.rightMargin *= factor;
            marginParams.bottomMargin *= factor;
        }
        view.setLayoutParams(layoutParams);

        // EditText has special padding, not scale
        if(!(view instanceof EditText)) {
            // Scale padding
            view.setPadding(
                    (int)(view.getPaddingLeft() * factor),
                    (int)(view.getPaddingTop() * factor),
                    (int)(view.getPaddingRight() * factor),
                    (int)(view.getPaddingBottom() * factor)
            );
        }

        // Scale the text size
        if(view instanceof TextView) {
            scaleTextSize((TextView) view, factor, layoutParams);
        }

        // Recurse
        if(view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)view;
            for(int i = 0; i < vg.getChildCount(); i++) {
                scaleViewRecurse(vg.getChildAt(i), factor);
            }
        }
    }

    // Scale the text size
    public static void scaleTextSize(TextView tv, float factor, ViewGroup.LayoutParams layoutParams) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * factor);
    }
}
