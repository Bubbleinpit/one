package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import me.lijpeng.one.R;
import me.lijpeng.one.util.ArticleDetailResponse;

import static me.lijpeng.one.MainActivity.client;

/**
 * Created by ljp on 2017/5/25.
 */

public class FragmentArticle extends BaseFragment {
    private SwipeRefreshLayout mSwipeLayout;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_article_container);
        mWebView = (WebView) mView.findViewById(R.id.articleWebView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.webViewLoading);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeLayout.setRefreshing(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);//显示进度条
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.INVISIBLE);//显示webView
            }
        });
        appearAnimation.setAnimationListener(new Animation.AnimationListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                mWebView.setAlpha(1);    //不在动画执行前设置为不透明，就看不见动画的渐变过程（一直全透明）
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
                mWebView.setAlpha(0);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        this.isVisibleToUser = isVisibleToUser;
        if (mSwipeLayout != null) {
            if (!isVisibleToUser) {
                //mSwipeLayout.setEnabled(false);
                //mWebView.setVisibility(View.INVISIBLE);
                return;
            } else {
                //mSwipeLayout.setEnabled(true);
                //mWebView.setVisibility(View.VISIBLE);
            }
        }
        prepareGetData(false);
    }

    @Override
    protected void getDataFromServer() {
        if (!mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(true);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Request getArticle = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/essay/1720?version=4.2.2")
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
                Gson gson = new Gson();
                ArticleDetailResponse articleDetail = gson.fromJson(result, ArticleDetailResponse.class);

                Message msg = new Message();
                msg.obj = articleDetail.getData().getHp_content();
                setArticleUiHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    @Override
    public void onRefresh() {
        mWebView.startAnimation(disappearAnimation);
        if (!prepareGetData(true)) {
            mSwipeLayout.setRefreshing(false);
            mWebView.startAnimation(appearAnimation);
        }   //获取失败得让界面显示回来

    }

    Handler setArticleUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String htmlContent = (String) msg.obj;
            mWebView.loadData(htmlContent, "text/html; charset=UTF-8", null);
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mSwipeLayout.setRefreshing(false);
            mWebView.startAnimation(appearAnimation);
        }
    };

    Handler finishLoadForError = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSwipeLayout.setRefreshing(false);
            mWebView.startAnimation(appearAnimation);
            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    };
}
