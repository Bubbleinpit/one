package me.lijpeng.one.thirdparty.viewpager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 取消滑动页面的响应
 */
public class NoSlidingViewPaper extends ViewPager {
	public NoSlidingViewPaper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public NoSlidingViewPaper(Context context) {
		super(context);
	}
	/*
	 * 表示把滑动事件传递给下一个view
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
