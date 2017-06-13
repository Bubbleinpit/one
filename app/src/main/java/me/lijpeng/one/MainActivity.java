package me.lijpeng.one;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

import me.lijpeng.one.fragments.FragmentArticle;
import me.lijpeng.one.fragments.FragmentOne;
import me.lijpeng.one.fragments.FragmentQuestion;
import me.lijpeng.one.customview.viewpager.NoSlidingPageTransformer;
import me.lijpeng.one.customview.viewpager.NoSlidingViewPaper;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private int scrollY = 0;    //article页面的scrollView的Y方向滚动量

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            ScrollView articleContainer = (ScrollView) findViewById(R.id.scroll_article_container);
            switch (item.getItemId()) {
                case R.id.navigation_one:
                    if (mViewPager.getCurrentItem() == 2) {
                        scrollY = articleContainer.getScrollY();
                        articleContainer.setVisibility(View.INVISIBLE);
                    }
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_article:
                    if (articleContainer.getVisibility() == View.INVISIBLE) {
                        articleContainer.setVisibility(View.VISIBLE);
                        if (articleContainer.getScrollY() != scrollY) {
                            articleContainer.smoothScrollTo(0, scrollY);  //不加这一句，会在设置VISIBLE之后页面自动跳转到webView开始的地方，toolbar和navigationBar也会随之隐藏，暂不知原因
                        }
                    }
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_question:
                    if (mViewPager.getCurrentItem() == 0) {
                        scrollY = articleContainer.getScrollY();
                        articleContainer.setVisibility(View.INVISIBLE);
                    }
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (NoSlidingViewPaper) findViewById(R.id.viewpager);
        final ArrayList<Fragment> fgLists = new ArrayList<>(3);
        fgLists.add(new FragmentOne());
        fgLists.add(new FragmentArticle());
        fgLists.add(new FragmentQuestion());
        NoSlidingPageTransformer pageTransformer = new NoSlidingPageTransformer();
        mViewPager.setPageTransformer(true, pageTransformer);
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fgLists.get(position);
            }

            @Override
            public int getCount() {
                return fgLists.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(2); //预加载剩下两页

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
