package me.lijpeng.one.fragments;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.animation.Animation;
import android.widget.ScrollView;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import me.lijpeng.one.R;

import static me.lijpeng.one.MainActivity.client;

/**
 * Created by ljp on 2017/5/25.
 */

public class FragmentArticle extends BaseFragment {
    private SwipeRefreshLayout mSwipeLayout;
    private ScrollView mScrollView;

    @Override
    protected void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_article_container);
        mScrollView = (ScrollView) mView.findViewById(R.id.scroll_article_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeLayout.setRefreshing(true);
        appearAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                mScrollView.setAlpha(1);    //不在动画执行前设置为不透明，就看不见动画的渐变过程（一直全透明）
            }

            @Override
            public void onAnimationEnd(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        disappearAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mScrollView.setAlpha(0);
            }   //为了保证动画执行完后界面的确透明了

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

    }
    @Override
    public int getLayoutId() {
        return R.layout.article_layout;
    }
    @Override
    protected void getDataFromServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Request getArticle = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/essay/2433")
                        .get()
                        .build();
                Response response;
                String result;
                try {
                    response = client.newCall(getArticle).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishLoadForError.sendMessage(new Message());
                    return;
                }
                System.out.println(result);
            }
        });
        t.start();
    }

    @Override
    public void onRefresh() {

    }

    Handler finishLoadForError = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSwipeLayout.setRefreshing(false);
            mScrollView.startAnimation(appearAnimation);
            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    };
}
