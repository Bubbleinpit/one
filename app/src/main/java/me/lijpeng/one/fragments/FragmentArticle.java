package me.lijpeng.one.fragments;

import android.app.Fragment;
import android.view.View;

import me.lijpeng.one.R;

/**
 * Created by ljp on 2017/5/25.
 */

public class FragmentArticle extends BaseFragment {

    @Override
    protected void initView() {

    }
    @Override
    public int getLayoutId() {
        return R.layout.article_layout;
    }
    @Override
    protected void getDataFromServer() {
        /*
         * 请求url：http://v3.wufazhuce.com:8000/api/essay/${id}
         */
    }

    @Override
    public void onRefresh() {

    }
}
