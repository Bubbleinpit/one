package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import me.lijpeng.one.R;
import me.lijpeng.one.circleimageview.CircleImageView;
import me.lijpeng.one.preload.BaseData;
import me.lijpeng.one.util.ArticleContent;
import me.lijpeng.one.util.response.article.ArticleDetailResponse;

import static android.text.TextUtils.isEmpty;
import static me.lijpeng.one.SplashActivity.client;

/**
 * Created by ljp on 2017/5/25.
 */

public class FragmentArticle extends BaseFragment {
    private SwipeRefreshLayout mSwipeLayout;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ScrollView mScrollView;

    @Override
    protected void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_article_container);
        mWebView = (WebView) mView.findViewById(R.id.articleWebView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.webViewLoading);
        mScrollView = (ScrollView) mView.findViewById(R.id.scroll_article_container);
        mWebView.setBackgroundColor(0);
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
                mProgressBar.setVisibility(View.INVISIBLE);//隐藏进度条
            }
        });
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
                String articleId = BaseData.getArticleId();
                if (isEmpty(articleId)) {
                    finishLoadForError.sendMessage(new Message());
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
                    finishLoadForError.sendMessage(new Message());
                    return;
                }
                Gson gson = new Gson();
                ArticleDetailResponse articleDetail = gson.fromJson(result, ArticleDetailResponse.class);

                Request getPicture = new Request.Builder()
                        .url(articleDetail.getData().getAuthor()[0].getWeb_url())
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
                    finishLoadForError.sendMessage(new Message());
                    return;
                }

                ArticleContent articleContent = new ArticleContent();
                articleContent.setTitle(articleDetail.getData().getHp_title());
                articleContent.setSubTitle(articleDetail.getData().getSub_title());
                articleContent.setAuthor(articleDetail.getData().getHp_author());
                articleContent.setAuthorInfo(articleDetail.getData().getAuth_it());
                articleContent.setAuthorWeibo(articleDetail.getData().getAuthor()[0].getWb_name());
                articleContent.setAuthorPhoto(bitmap);
                articleContent.setArticleContent(articleDetail.getData().getHp_content());
                articleContent.setCopyright(articleDetail.getData().getCopyright());
                Log.d("tag", "收到的copyright：" + articleDetail.getData().getCopyright());
                articleContent.setEditorInfo(articleDetail.getData().getHp_author_introduce() + " " + articleDetail.getData().getEditor_email());
                articleContent.setGuideWord(articleDetail.getData().getGuide_word());

                Message msg = new Message();
                msg.obj = articleContent;
                setArticleUiHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    Handler setArticleUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ArticleContent articleContent = (ArticleContent) msg.obj;
            mWebView.loadData(articleContent.getArticleContent(), "text/html; charset=UTF-8", null);
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            mWebView.getSettings().setJavaScriptEnabled(true);

            TextView mGuideWord = (TextView) mView.findViewById(R.id.tv_guide_word);
            TextView mArticleTitle = (TextView) mView.findViewById(R.id.tv_title);
            TextView mSubTitle = (TextView) mView.findViewById(R.id.tv_sub_title);
            TextView mAuthorTop = (TextView) mView.findViewById(R.id.tv_author_top);
            TextView mEditorInfo = (TextView) mView.findViewById(R.id.tv_editor_info);
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
    };

    Handler finishLoadForError = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSwipeLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void onRefresh() {
        mScrollView.startAnimation(disappearAnimation);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int result = BaseData.getBaseData(); //只有result值为0，才说明函数正常返回
                Message msg = new Message();
                msg.obj = result;
                finishLoad.sendMessage(msg);
            }
        });
        t.start();
    }

    Handler finishLoad = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if ((int)msg.obj < 0) {
                Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
                mSwipeLayout.setRefreshing(false);
            } else {
                if (!prepareGetData(true)) {
                    mSwipeLayout.setRefreshing(false);
                }   //获取失败得让界面显示回来
            }
        }
    };
}
