package me.lijpeng.one.viewpager;

import android.support.v4.view.ViewPager;
import android.view.View;

/*
*设置Fragment切换时的动画为淡入淡出
*/

public class NoSlidingPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();    //得到view宽

        if (position <= -1) { // [-Infinity,-1]
            // This page is way off-screen to the left. 出了左边屏幕
            view.setTranslationX(0);

        } else if (position < 1) { // (-1,1)
            view.setTranslationX(-pageWidth * position);  //阻止页面的滑动,位置在左则设向右偏移位置，在右则设向左偏移位置
            float alphaFactor = 1 - Math.abs(position);
            //透明度改变
            view.setAlpha(alphaFactor);
            /*
            if (alphaFactor == 0)
                view.setVisibility(View.INVISIBLE);     //页面不在当前界面显示，则使其Invisible，这句是为了解决Fragment对上下滑动事件监听的错乱，暂不知原因
            else if (view.getVisibility() == View.INVISIBLE)
                view.setVisibility(View.VISIBLE);       //页面在当前界面显示，则使其Visible，这句是为了解决Fragment对上下滑动事件监听的错乱，暂不知原因
                */
        } else { // [1,+Infinity]
            // This page is way off-screen to the right.    出了右边屏幕
            view.setTranslationX(0);
        }
    }

}
