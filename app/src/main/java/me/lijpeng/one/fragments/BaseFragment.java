package me.lijpeng.one.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import me.lijpeng.one.R;

/*
 * Created by ljp on 2017/5/25.
 */

public abstract class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    protected View mView;
    protected boolean isViewInitiated; //当前页面是否初始化
    protected boolean isVisibleToUser; //当前页面是否显示
    protected boolean isDataRequested; //是否已经请求了数据
    protected Context mContext;
    Animation appearAnimation;
    Animation disappearAnimation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mContext = getContext();
        mView = inflater.inflate(getLayoutId(), null);
        isViewInitiated = true;
        appearAnimation = AnimationUtils.loadAnimation(mContext, R.anim.appear_anim);
        disappearAnimation = AnimationUtils.loadAnimation(mContext, R.anim.disappear_anim);
        initView();
        prepareGetData();
        return mView;
    }

    /*初始化页面布局和数据*/
    protected abstract void initView();
    /*布局*/
    public abstract int getLayoutId();
    /*从服务器获取数据*/
    protected abstract void getDataFromServer();

    /**
     * 如果只想第一次进入该页面请求数据，return prepareGetData(false)
     * 如果想每次进入该页面就请求数据，return prepareGetData(true)
     */
    private boolean prepareGetData(){
        return prepareGetData(false);
    }

    /**
     * 判断是否从服务器器获取数据
     * @param isforceUpdate 强制更新的标记
     */
    protected boolean prepareGetData(boolean isforceUpdate) {
        if(isVisibleToUser && isViewInitiated && (!isDataRequested || isforceUpdate)){
            /*从服务器获取数据*/
            getDataFromServer();
            isDataRequested = true;
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isDataRequested = false;    //如果没有这句，会造成如果fragment被销毁，那么下次创建时，不会从服务器去读取数据
        isViewInitiated = false;
    }
}
