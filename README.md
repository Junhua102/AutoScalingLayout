# AutoScalingLayout
Auto-Scaling Layouts for Android.  

        Android studio users import release/AutoScalingLayout.aar .

        Eclipse users import release/AutoScalingLayout.jar, and copy attr.xml into value folder.

------------

Replace the Layout: 
============
All we need is to replace the root layouts required auto-scaling, the child layouts will also achieve auto-scaling.

Original layout  | AutoScalingLayout
------------- | -------------
RelativeLayout  | ASRelativeLayout
LinearLayout  | ASLinearLayout
FrameLayout  | ASFrameLayout

For now the three layouts above are supported.

Add the attribute:
============
designWidth and designHeight are the screen sizes you are using when you are designing the GUI in the xml editor. For instance, I use Nexus 4 as my design device (I treat all the devices as Nexus 4), so the generic screen size converting to dp is 384dp and 575dp (exclude the statebar and the actionbar).

```
<me.dreamheart.autoscalinglayout.ASRelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    custom:designWidth="384dp"
    custom:designHeight="575dp"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".LoginActivity"
    android:background="@drawable/login_bg"
    >
```

```
    xmlns:custom="http://schemas.android.com/apk/res-auto"
    custom:designWidth="384dp"
    custom:designHeight="575dp"
```

1. designWidth and designHeight are very import. If you assign wrong values, you would not see the expected layouts. 
2. The unit used in the xml GUI editor should keep constant. For example, if you use dp for designWidth and designHeight, all the unit of the child views has to be dp, even the font size can not be sp, so as px, pt. If you just want to copy the pixel values given by GUI, just use px for all components.
3. When applying AutoScalingLayout, the screen will keep the aspect ratio of designWidth and designHeight, so don’t worry about turning square layouts into rectangle unexpectedly.
