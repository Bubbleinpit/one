package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.ref.WeakReference;

import me.lijpeng.one.R;
import me.lijpeng.one.customview.circleimageview.CircleImageView;
import me.lijpeng.one.customview.scrollview.ObservableScrollView;
import me.lijpeng.one.preload.BaseData;
import me.lijpeng.one.util.QuestionContent;
import me.lijpeng.one.util.response.question.QuestionResponse;

import static android.text.TextUtils.isEmpty;
import static me.lijpeng.one.SplashActivity.client;

/*
 * Created by ljp on 2017/5/25.
 */

public class FragmentQuestion extends BaseFragment implements ObservableScrollView.ScrollViewListener{
    private SwipeRefreshLayout mSwipeLayout;
    private ObservableScrollView mScrollView;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private Toolbar mToolbar;
    private BottomNavigationView mNavigationBar;

    @Override
    protected void initView() {
        mScrollView = (ObservableScrollView) mView.findViewById(R.id.scroll_question_container);
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_question_container);
        mWebView = (WebView) mView.findViewById(R.id.answerWebView);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.answerWebViewLoading);
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
        mWebView.loadUrl("file:///android_asset/answer_content.html");
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //mWebView.getSettings().setJavaScriptEnabled(true);

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
        return R.layout.question_layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareGetData(false);
    }

    @Override
    protected void getDataFromServer() {
        /*
         *请求url：http://v3.wufazhuce.com:8000/api/question/${id}?version=4.2.2
         */
        if (!mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(true);
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Message errorMsg = new Message();
                errorMsg.what = 2;
                errorMsg.obj = "网络错误";
                String questionID = BaseData.getQuestionID();
                if (isEmpty(questionID)) {
                    errorMsg.obj = "刚刚网络不好，试试看下拉刷新";
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Request getQuestion = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/question/" + questionID + "?version=4.2.2")
                        .get()
                        .build();
                Response response;
                String result;
                try {
                    response = client.newCall(getQuestion).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Gson gson = new Gson();
                QuestionResponse questionInfo = gson.fromJson(result, QuestionResponse.class);

                QuestionContent questionContent = new QuestionContent();

                Request getPicture = new Request.Builder()
                        .url(questionInfo.getData().getAsker().getWeb_url())
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
                questionContent.setAskerHead(bitmap);

                getPicture = new Request.Builder()
                        .url(questionInfo.getData().getAnswerer().getWeb_url())
                        .get()
                        .build();
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
                questionContent.setAnswererHead(bitmap);

                questionContent.setQuestionTitle(questionInfo.getData().getQuestion_title());
                questionContent.setQuestionContent(questionInfo.getData().getQuestion_content());
                questionContent.setAnswerContent(questionInfo.getData().getAnswer_content());
                questionContent.setEditorInfo(questionInfo.getData().getCharge_edt() + " " + questionInfo.getData().getCharge_email());
                questionContent.setAskerName(questionInfo.getData().getAsker().getUser_name());
                questionContent.setAnswererName(questionInfo.getData().getAnswerer().getUser_name());

                Message msg = new Message();
                msg.what = 0;
                msg.obj = questionContent;
                msgHandler.sendMessage(msg);
            }
        });
        t.start();
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

    private static class MsgHandler extends Handler {
        private WeakReference<FragmentQuestion> mFragment;

        MsgHandler(FragmentQuestion fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentQuestion fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 0:
                        fragment.setQuestionUi(msg);
                        break;
                    case 1:
                        fragment.handleRefreshResult(msg);
                        break;
                    case 2:
                        fragment.handleNetRequestError(msg);
                        break;
                }
            }
        }
    }

    MsgHandler msgHandler = new MsgHandler(this);

    public void setQuestionUi(Message msg) {
        QuestionContent questionContent = (QuestionContent) msg.obj;
        mWebView.loadData(questionContent.getAnswerContent(), "text/html; charset=UTF-8", null);

        TextView mQuestionTitle = (TextView) mView.findViewById(R.id.tv_question_title);
        TextView mAskerName = (TextView) mView.findViewById(R.id.tv_asker_name);
        TextView mQuestionContent = (TextView) mView.findViewById(R.id.tv_question_content);
        TextView mAnswererName = (TextView) mView.findViewById(R.id.tv_answerer_name);
        TextView mEditorInfo = (TextView) mView.findViewById(R.id.tv_question_editor_info);
        CircleImageView mAskerHead = (CircleImageView) mView.findViewById(R.id.iv_asker_head);
        CircleImageView mAnswererHead = (CircleImageView) mView.findViewById(R.id.iv_answerer_head);

        mQuestionTitle.setText(questionContent.getQuestionTitle());
        mAskerName.setText(questionContent.getAskerName());
        mQuestionContent.setText(questionContent.getQuestionContent());
        mAnswererName.setText(questionContent.getAnswererName());
        mEditorInfo.setText(questionContent.getEditorInfo());
        mAskerHead.setImageBitmap(questionContent.getAskerHead());
        mAnswererHead.setImageBitmap(questionContent.getAnswererHead());

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

    public void handleNetRequestError(Message msg) {
        mSwipeLayout.setRefreshing(false);
        mScrollView.startAnimation(appearAnimation);
        Toast.makeText(getActivity(), (String)msg.obj,Toast.LENGTH_SHORT).show();
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
