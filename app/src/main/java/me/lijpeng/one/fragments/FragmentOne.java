package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.lang.ref.WeakReference;

import me.lijpeng.one.R;
import me.lijpeng.one.preload.BaseData;
import me.lijpeng.one.util.OneContent;
import me.lijpeng.one.util.response.picture.PictureDetailResponse;
import me.lijpeng.one.util.response.picture.PictureResponse;

import static android.text.TextUtils.isEmpty;
import static me.lijpeng.one.SplashActivity.client;

/*
 * Created by ljp on 2017/5/25.
 */

public class FragmentOne extends BaseFragment {
    private String[] monthList = {"Jan.","Feb.","Mar.","Apr.","Fri.","Jun.","Jul.","Aug.","Sep.","Oct.","Nov.","Dec."};
    private SwipeRefreshLayout mSwipeLayout;
    private ScrollView mScrollView;

    @Override
    protected void initView() {
        mSwipeLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_one_container);
        mScrollView = (ScrollView) mView.findViewById(R.id.scroll_one_container);
        int height = 0;
        TypedValue typedValue = new TypedValue();
        if (getContext().getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
            height = TypedValue.complexToDimensionPixelSize(typedValue.data,getResources().getDisplayMetrics());
        }
        mSwipeLayout.setProgressViewOffset(false, height - (int) (40 * getResources().getDisplayMetrics().density), height + (int) (64 * getResources().getDisplayMetrics().density)); //下移下拉刷新加载圈的位置
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
        return R.layout.one_layout;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        /*
        if (mSwipeLayout != null) {
            if (!isVisibleToUser) {
                //mSwipeLayout.setEnabled(false);
                //mScrollView.setVisibility(View.INVISIBLE);
                return;
            } else {
                //mSwipeLayout.setEnabled(true);
                //mScrollView.setVisibility(View.VISIBLE);
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
                errorMsg.obj = "网络错误";
                String pictureId = BaseData.getPictureId();
                if (isEmpty(pictureId)) {
                    errorMsg.obj = "刚刚网络不好，试试看下拉刷新";
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Response response;
                String result;
                Request getPictureDetail = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/hp/detail/" + pictureId)
                        .get()
                        .build();
                try {
                    response = client.newCall(getPictureDetail).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    msgHandler.sendMessage(errorMsg);
                    return;
                }
                Gson gson = new Gson();
                PictureResponse pictureInfo = gson.fromJson(result, PictureResponse.class);
                PictureDetailResponse pictureDetail = pictureInfo.getData();
                Request getPicture = new Request.Builder()
                        .url(pictureDetail.getHp_img_original_url())
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
                OneContent content = new OneContent();
                content.setBitmap(bitmap);
                content.setOneText(pictureDetail.getHp_content());
                content.setPictureAuthor(pictureDetail.getHp_author());
                content.setTime(pictureDetail.getHp_makettime());
                content.setVol(pictureDetail.getHp_title());
                Message msg = new Message();
                msg.what = 0;
                msg.obj = content;
                msgHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    private static class MsgHandler extends Handler {
        private WeakReference<FragmentOne> mFragment;

        MsgHandler(FragmentOne fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            FragmentOne fragment = mFragment.get();
            if (fragment != null) {
                switch (msg.what) {
                    case 0:
                        fragment.setOneUi(msg);
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

    public void setOneUi(Message msg) {
        OneContent content = (OneContent) msg.obj;
        TextView pictureAuthorView = (TextView) mView.findViewById(R.id.picture_author);
        TextView oneTextView = (TextView) mView.findViewById(R.id.one_text);
        TextView volView = (TextView) mView.findViewById(R.id.one_vol);
        ImageView image = (ImageView) mView.findViewById(R.id.one_picture);
        TextView dayView = (TextView) mView.findViewById(R.id.date_day);
        TextView monthAndYearView = (TextView) mView.findViewById(R.id.date_month_and_year);

        pictureAuthorView.setText(content.getPictureAuthor());
        oneTextView.setText(content.getOneText());
        volView.setText(content.getVol());
        image.setImageBitmap(content.getBitmap());
        dayView.setText(content.getTime().substring(8, 10));
        String year = content.getTime().substring(0, 4);
        int month = Integer.parseInt(content.getTime().substring(5, 7));
        year = monthList[month - 1] + year;
        monthAndYearView.setText(year);
        mSwipeLayout.setRefreshing(false);
        mScrollView.startAnimation(appearAnimation);
    }

    public void handleRefreshResult(Message msg) {
        if ((int)msg.obj < 0) {
            Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show();
            mSwipeLayout.setRefreshing(false);
            //mScrollView.startAnimation(appearAnimation);
        }else {
            if (!prepareGetData(true)) {
                mSwipeLayout.setRefreshing(false);
                //mScrollView.startAnimation(appearAnimation);
            }//获取失败得让界面显示回来
        }
    }

    public void handleNetRequestError(Message msg) {
        mSwipeLayout.setRefreshing(false);
        mScrollView.startAnimation(appearAnimation);
        Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
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
}
