package com.bandfitproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bandfitproject.board.BoardMainActivity;
import com.bandfitproject.data.Flag;
import com.bandfitproject.data.User;
import com.bandfitproject.login.LoginActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import static com.bandfitproject.login.LoginActivity.user;

public class FirstActivity extends Activity {
    // 자동 로그인 //
    public static SharedPreferences mPref ;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //BandFitDataBase.getInstance().exit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_first);

        if(Flag.getInstance().getIsFirst()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Flag.getInstance().isNotFirst();
        }
        final DatabaseReference autoLoginRef =
                FirebaseDatabase.getInstance().getReference("information").child("");

        /**
         * DB 데이터 초기화
         */
        BandFitDataBase.getInstance().initBoardData();

        // 자동 로그인 user 데이터 가져옴 //
        mPref = getSharedPreferences("auto_login", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPref.getString("User",   null);
        user = gson.fromJson(json, User.class);

        if(user != null) {
            // 사용자 채팅 리스트뷰 정보 초기화//
            // 사용자 정보 최신화 -> 안하면 예전 정보가 그대로...//
            Query query = autoLoginRef.child(user.id);
            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            user = dataSnapshot.getValue(User.class);
                            BandFitDataBase.getInstance().initChatRoomData();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
            String token = FirebaseInstanceId.getInstance().getToken();
            DatabaseReference mTokenRef = FirebaseDatabase.getInstance().getReference("information").child(user.id);
            mTokenRef.child("fcmToken").setValue(token);
            // 2초간 대기 후 게시판 액티비티로 이동//
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(FirstActivity.this, BoardMainActivity.class);
                    // 로그인 상태 true로 전환 //
                    autoLoginRef.child(user.id).child("isLogin").setValue(true);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(FirstActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
    }
}
