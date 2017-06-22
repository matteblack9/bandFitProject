package com.bandfitproject.board;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bandfitproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserInforActivity extends Fragment {
    @BindView(R.id.webView) WebView mWebView;

    private Unbinder unbinder;

    public UserInforActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_user_infor_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("http://m.sports.naver.com/index.nhn");
        mWebView.setWebViewClient(new UserInforActivity.WebViewClientClass());
        mWebView.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && mWebView.canGoBack()) {
                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }

        });
        return view;
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    private void webViewGoBack(){
        mWebView.goBack();
    }
    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }*/
}

