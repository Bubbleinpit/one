package me.lijpeng.one;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;

import me.lijpeng.one.preload.BaseData;

public class SplashActivity extends AppCompatActivity {

    private long startTime;
    public static OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_splash);

        startTime = System.currentTimeMillis();
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
            long endTime = System.currentTimeMillis();
            if ((endTime - startTime) < 800) {
                try {
                    Thread.sleep(startTime + 800 - endTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }   //如果预加载数据的时间没有800毫秒（一般都不会超过的），就让启动界面显示至800毫秒再报错跳转

            if ((int)msg.obj < 0)
                Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(intent);
            finish();
        }
    };
}
