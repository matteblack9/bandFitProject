package com.bandfitproject.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.bandfitproject.board.BoardMainActivity;
import com.bandfitproject.data.BoardData;
import com.bandfitproject.data.User;

import com.bandfitproject.register.RegisterActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bandfitproject.FirstActivity.mPref;

public class LoginActivity extends Activity {
    //위젯 바인딩//
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.btn_register)Button btn_register;
    @BindView(R.id.et_login)EditText et_login;
    @BindView(R.id.et_password)EditText et_password;
    @BindView(R.id.cb_autoLogin)CheckBox cb_autuLogin;

    //로그인시 공유되는 유저정보//
    public static User user;

    private String id ;
    private String password ;

    //데이터베이스에서 유저를 찾았는지 확인//
    boolean find;
    DatabaseReference mDatabaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.i(getClass().getName(), "여기는 onCreate 입니다.");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("information").child("");
        // DB 초기화 //
        //BandFitDataBase.getInstance().initBoardData();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_register)
     void onRegisterClick() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void search_end(boolean gFind) {
        if(gFind) {
            if((user.isLogin)) {
                //Toast.makeText(LoginActivity.this, "이미 접속중인 아이디입니다", Toast.LENGTH_SHORT).show();
            } else {
            }
            BandFitDataBase.getInstance().initChatRoomData();

            String token = FirebaseInstanceId.getInstance().getToken();
            DatabaseReference mTokenRef = FirebaseDatabase.getInstance().getReference("information").child(user.id);
            mTokenRef.child("fcmToken").setValue(token);
            DatabaseReference mTokenBoardRef;



            if(cb_autuLogin.isChecked()) {
                // 자동 로그인을 위한 객체 저장 //
                mPref = getSharedPreferences("auto_login", MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = mPref.edit();
                Gson gson = new Gson();
                String json = gson.toJson(user);
                prefsEditor.putString("User", json);
                prefsEditor.commit();
            }

            // 로그인 상태 true로 전환 //

            mDatabaseReference.child(user.id).child("isLogin").setValue(true);
            Intent intent = new Intent(LoginActivity.this, BoardMainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "아이디/비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //BandFitDataBase.getInstance().exit();
        Log.i(getClass().getName(), "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @OnClick(R.id.btn_login)
    void onLoginClick() {
        id = et_login.getText().toString();
        password = et_password.getText().toString();

        Query query = mDatabaseReference.child(id);
        if(id.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "빈칸을 채워주세요.", Toast.LENGTH_SHORT).show();
        } else {
            query.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                System.out.println("asdasdasdasdasd");
                                find = false;
                            } else {
                                user = dataSnapshot.getValue(User.class);
                                if(password.equals(user.password) ) {
                                //if(password.equals(user.password)) {
                                    find = true;
                                } else {
                                    find = false;
                                }
                            }
                            search_end(find);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }
    }
}
