package me.lijpeng.one;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.util.ArrayList;

import me.lijpeng.one.GestureListener.HorizontalSlideListener;
import me.lijpeng.one.customview.scrollview.ObservableScrollView;
import me.lijpeng.one.fragments.FragmentArticle;
import me.lijpeng.one.fragments.FragmentOne;
import me.lijpeng.one.fragments.FragmentQuestion;
import me.lijpeng.one.customview.viewpager.NoSlidingPageTransformer;
import me.lijpeng.one.customview.viewpager.NoSlidingViewPaper;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            ObservableScrollView articleContainer = (ObservableScrollView) findViewById(R.id.scroll_article_container);
            ObservableScrollView questionContainer = (ObservableScrollView) findViewById(R.id.scroll_question_container);
            switch (item.getItemId()) {
                case R.id.navigation_one:
                    if (mViewPager.getCurrentItem() == 2 && articleContainer.getVisibility() == View.VISIBLE) {
                        articleContainer.setVisibility(View.INVISIBLE);
                    }
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_article:
                    if (mViewPager.getCurrentItem() == 1) {
                        articleContainer.smoothScrollTo(0, 0);
                        return true;
                    }
                    if (articleContainer.getVisibility() == View.INVISIBLE) {
                        articleContainer.setVisibility(View.VISIBLE);
                    }
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_question:
                    if (mViewPager.getCurrentItem() == 2) {
                        questionContainer.smoothScrollTo(0, 0);
                        return true;
                    }
                    if (mViewPager.getCurrentItem() == 0 && articleContainer.getVisibility() == View.VISIBLE) {
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
        Toolbar mToolbar = (Toolbar) findViewById(R.id.one_toolbar);
        mToolbar.setLongClickable(true);
        mToolbar.setOnTouchListener(new MyGestureListener(this));
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

    /**
     * 继承HorizontalSlideListener，重写left和right方法
     */
    private class MyGestureListener extends HorizontalSlideListener {
        MyGestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean left() {
            Toast.makeText(MainActivity.this, "常雨婧，如果你能看到", Toast.LENGTH_SHORT).show();
            return super.left();
        }

        @Override
        public boolean right() {
            Toast.makeText(MainActivity.this, "我喜欢你", Toast.LENGTH_SHORT).show();
            return super.right();
        }
    }

}
