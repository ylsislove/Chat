package com.yain.controller.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.chat.EMClient;
import com.yain.R;
import com.yain.model.Model;
import com.yain.model.bean.UserInfo;

/**
 * 欢迎页面
 */
public class SplashActivity extends Activity {

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            // 如果当前activity已经退出，那么我就不处理handler中的消息
            if(isFinishing()) {
                return;
            }
            // 判断进入主页面还是登录页面
            toMainOrLogin();
        }
    };

    // 判断进入主页面还是登录页面
    private void toMainOrLogin() {
        Model.getInstance().getGlobalThreadPool().execute(() -> {
            // 判断当前账号是否已经登录过
            if(EMClient.getInstance().isLoggedInBefore()) {// 登录过
                // 获取到当前登录用户的信息
                UserInfo account = Model.getInstance().getUserAccountDao()
                        .getAccountByHxId(EMClient.getInstance().getCurrentUser());
                if(account == null) {
                    // 跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    // 登录成功后的方法
                    Model.getInstance().loginSuccess(account);
                    // 跳转到主页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }else {// 没登录过
                // 跳转到登录页面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            // 结束当前页面
            finish();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 发送2s钟的延时消息
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁消息
        handler.removeCallbacksAndMessages(null);
    }

}
