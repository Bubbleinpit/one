package me.lijpeng.one.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/*
*设置Fragment切换时的动画为淡入淡出
*/

public class NoSlidingPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_ALPHA = 0.0f;    //最小透明度

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();    //得到view宽

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left. 出了左边屏幕
            view.setAlpha(0);

        } else if (position <= 1) { // [-1,1]
            if (position < 0) {
                //消失的页面
                view.setTranslationX(-pageWidth * position);  //阻止消失页面的滑动
            } else {
                //出现的页面
                //view.setTranslationX(pageWidth);        //直接设置出现的页面到底
                view.setTranslationX(-pageWidth * position);  //阻止出现页面的滑动
            }
            // Fade the page relative to its size.
            float alphaFactor = Math.max(MIN_ALPHA, 1 - Math.abs(position));
            //透明度改变Log
            view.setAlpha(alphaFactor);
            if (alphaFactor == 0)
                view.setVisibility(View.INVISIBLE);     //页面不在当前界面显示，则使其Invisible，这句是为了解决Fragment对上下滑动事件监听的错乱，暂不知原因
            else if (view.getVisibility() == View.INVISIBLE)
                view.setVisibility(View.VISIBLE);       //页面在当前界面显示，则使其Visible，这句是为了解决Fragment对上下滑动事件监听的错乱，暂不知原因
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.    出了右边屏幕
            view.setAlpha(0);
        }
    }

}
