package me.lijpeng.one.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import me.lijpeng.one.R;
import me.lijpeng.one.util.*;

import static me.lijpeng.one.MainActivity.client;

/**
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
    protected void getDataFromServer() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Request getPictureId = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/hp/idlist/0")
                        .get()
                        .build();
                Response response;
                String result;
                try {
                    response = client.newCall(getPictureId).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishLoadForError.sendMessage(new Message());
                    return;
                }
                Gson gson = new Gson();
                PictureList pictureList = gson.fromJson(result, PictureList.class);
                String pictureId = pictureList.getData().get(0);
                Request getPictureDetail = new Request.Builder()
                        .url("http://v3.wufazhuce.com:8000/api/hp/detail/" + pictureId)
                        .get()
                        .build();
                try {
                    response = client.newCall(getPictureDetail).execute();
                    result = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                    finishLoadForError.sendMessage(new Message());
                    return;
                }
                gson = new Gson();
                PictureDetail pictureDetail = gson.fromJson(result, PictureDetail.class);
                PictureDataInDetail pictureInfo = pictureDetail.getData();
                String pictureAuthor = pictureInfo.getHp_author();
                String oneText = pictureInfo.getHp_content();
                String pictureUrl = pictureInfo.getHp_img_original_url();
                String vol = pictureInfo.getHp_title();
                String time = pictureInfo.getHp_makettime();
                Request getPicture = new Request.Builder()
                        .url(pictureUrl)
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
                OneContent content = new OneContent();
                content.setBitmap(bitmap);
                content.setOneText(oneText);
                content.setPictureAuthor(pictureAuthor);
                content.setTime(time);
                content.setVol(vol);
                Message msg = new Message();
                msg.obj = content;
                setOneUiHandler.sendMessage(msg);
            }
        });
        t.start();
    }

    Handler setOneUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
    };

    Handler finishLoadForError = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mSwipeLayout.setRefreshing(false);
            mScrollView.startAnimation(appearAnimation);
            Toast.makeText(getActivity(),"网络错误",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onRefresh() {
        mScrollView.startAnimation(disappearAnimation);
        getDataFromServer();
    }
}
