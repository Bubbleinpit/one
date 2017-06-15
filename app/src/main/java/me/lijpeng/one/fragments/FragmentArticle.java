package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.ref.WeakReference;

import me.lijpeng.one.R;
import me.lijpeng.one.customview.circleimageview.CircleImageView;
import me.lijpeng.one.customview.scrollview.ObservableScrollView;
import me.lijpeng.one.preload.BaseData;
import me.lijpeng.one.util.ArticleContent;
import me.lijpeng.one.util.response.article.ArticleResponse;

import static android.text.TextUtils.isEmpty;
import static me.lijpeng.one.SplashActivity.client;

/*
 * Created by ljp on 2017/5/25.
 */

public class FragmentArticle extends BaseFragment implements ObservableScrollView.ScrollViewListener {
    private SwipeRefreshLayout mSwipeLayout;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ObservableScrollView mScrollView;
    private Toolbar mToolbar;
    private BottomNavigationView mNavigationBar;

    @Override
    protected void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_article_container);
        mWebView = (WebView) mView.findViewById(R.id.articleWebView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.articleWebViewLoading);
        mScrollView = (ObservableScrollView) mView.findViewById(R.id.scroll_article_container);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.one_toolbar);
        mNavigationBar = (BottomNavigationView) getActivity().findViewById(R.id.navigation_bar);
        mScrollView.setScrollViewListener(this);
        int height = 0;
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
            height = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }
        mSwipeLayout.setProgressViewOffset(false, height - (int) (40 * getResources().getDisplayMetrics().density), height + (int) (64 * getResources().getDisplayMetrics().density)); //下移下拉刷新加载圈的位置
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeLayout.setRefreshing(true);
        mWebView.setBackgroundColor(0);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);//显示进度条
                mWebView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.INVISIBLE);//隐藏进度条
                mWebView.setVisibility(View.VISIBLE);
            }
        });
        mWebView.loadUrl("file:///android_asset/article_content.html");
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setJavaScriptEnabled(true);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        /*
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
        */
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
                Message errorMsg = new Message();
                errorMsg.what = 2;
                String articleId = BaseData.getArticleId();
                if (isEmpty(articleId)) {
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Request getArticle = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/essay/" + articleId + "?version=4.2.2")
                        .get()
                        .build();
                Response response;
                String result;
                try {
                    response = client.newCall(getArticle).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Gson gson = new Gson();
                ArticleResponse articleInfo = gson.fromJson(result, ArticleResponse.class);

                Request getPicture = new Request.Builder()
                        .url(articleInfo.getData().getAuthor()[0].getWeb_url())
                        .get()
                        .build();
                Bitmap bitmap;
                try {
                    response = client.newCall(getPicture).execute();
                    byte[] bytes = response.body().bytes();
                    if (bytes.length == 0) {
                        Log.d("tag", "没有收到图片");
                        return;
                    } else {
                        Log.d("tag", "开始解码图片，图片大小：" + bytes.length + "字节");
                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msgHandler.sendMessage(errorMsg);
                    return;
                }

                ArticleContent articleContent = new ArticleContent();
                articleContent.setTitle(articleInfo.getData().getHp_title());
                articleContent.setSubTitle(articleInfo.getData().getSub_title());
                articleContent.setAuthor(articleInfo.getData().getHp_author());
                articleContent.setAuthorInfo(articleInfo.getData().getAuth_it());
                articleContent.setAuthorWeibo(articleInfo.getData().getAuthor()[0].getWb_name());
                articleContent.setAuthorPhoto(bitmap);
                articleContent.setArticleContent(articleInfo.getData().getHp_content());
                articleContent.setCopyright(articleInfo.getData().getCopyright());
                articleContent.setEditorInfo(articleInfo.getData().getHp_author_introduce() + " " + articleInfo.getData().getEditor_email());
                articleContent.setGuideWord(articleInfo.getData().getGuide_word());

                Message msg = new Message();
                msg.what = 0;
                msg.obj = articleContent;
                msgHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    private static class MsgHandler extends Handler {
        private WeakReference<FragmentArticle> mFragment;

        MsgHandler(FragmentArticle fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentArticle fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 0:
                        fragment.setArticleUi(msg);
                        break;
                    case 1:
                        fragment.handleRefreshResult(msg);
                        break;
                    case 2:
                        fragment.handleNetRequestError();
                        break;
                }
            }
        }
    }   //额，防止内存泄漏把handler写成一个静态类

    MsgHandler msgHandler = new MsgHandler(this);

    public void setArticleUi(Message msg) {
        ArticleContent articleContent = (ArticleContent) msg.obj;
        System.out.println("-------------------------------------------测试输出 网页内容：" + articleContent.getArticleContent());
        mWebView.loadData(articleContent.getArticleContent(), "text/html; charset=UTF-8", null);

        TextView mGuideWord = (TextView) mView.findViewById(R.id.tv_guide_word);
        TextView mArticleTitle = (TextView) mView.findViewById(R.id.tv_title);
        TextView mSubTitle = (TextView) mView.findViewById(R.id.tv_sub_title);
        TextView mAuthorTop = (TextView) mView.findViewById(R.id.tv_author_top);
        TextView mEditorInfo = (TextView) mView.findViewById(R.id.tv_article_editor_info);
        TextView mCopyright = (TextView) mView.findViewById(R.id.tv_copyright);
        CircleImageView mHeadView = (CircleImageView) mView.findViewById(R.id.iv_head);
        TextView mAuthorBottom = (TextView) mView.findViewById(R.id.tv_author_bottom);
        TextView mAuthorInfo = (TextView) mView.findViewById(R.id.tv_author_info);

        mGuideWord.setText(articleContent.getGuideWord());
        mArticleTitle.setText(articleContent.getTitle());
        if (articleContent.getSubTitle().length() != 0)
            mSubTitle.setText(articleContent.getSubTitle());
        else
            mSubTitle.setText("━━━━");
        String authorTop = "文/" + articleContent.getAuthor();
        mAuthorTop.setText(authorTop);
        mEditorInfo.setText(articleContent.getEditorInfo());
        mCopyright.setText(articleContent.getCopyright());
        mHeadView.setImageBitmap(articleContent.getAuthorPhoto());
        String authorBottom = articleContent.getAuthor() + " " + articleContent.getAuthorWeibo();
        mAuthorBottom.setText(authorBottom);
        mAuthorInfo.setText(articleContent.getAuthorInfo());

        mSwipeLayout.setRefreshing(false);
        mScrollView.startAnimation(appearAnimation);
    }

    public void handleRefreshResult(Message msg) {
        if ((int)msg.obj != 0) {
            Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
            mSwipeLayout.setRefreshing(false);
        } else {
            if (!prepareGetData(true)) {
                mSwipeLayout.setRefreshing(false);
            }
        }
    }

    public void handleNetRequestError() {
        mSwipeLayout.setRefreshing(false);
        mScrollView.startAnimation(appearAnimation);
        Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        //mScrollView.startAnimation(disappearAnimation);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int result = BaseData.getBaseData(); //只有result值为0，才说明函数正常返回
                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                msgHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    private boolean barIsGone = false;
    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (oldy < y && Math.abs(oldy - y) > 20) {
            if (!barIsGone) {
                mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
                mNavigationBar.animate().translationY(mNavigationBar.getHeight()).setInterpolator(new AccelerateInterpolator(1));
                barIsGone = true;
            }
        } else if (oldy > y && Math.abs(oldy - y) > 20) {
            if (barIsGone) {
                mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
                mNavigationBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(1));
                barIsGone = false;
            }
        }
    }
}
