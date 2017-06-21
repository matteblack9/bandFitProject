package com.bandfitproject.register;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bandfitproject.R;
import com.bandfitproject.data.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {
    @BindView(R.id.register_et_id)EditText register_et_id;
    @BindView(R.id.register_et_password)EditText register_et_password;
    @BindView(R.id.register_et_password_check)EditText register_et_password_check;
    @BindView(R.id.register_et_name)EditText register_et_name;
    @BindView(R.id.register_et_email)EditText register_et_email;
    @BindView(R.id.register_btn_sign_in)Button register_btn_sign_in;

    //로그인 DB//
    DatabaseReference mDatabaseReference;
    ChildEventListener mChildEventListener;

    //infor//
    String id, password, password_check, name, email = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_btn_sign_in)
    void onSignInClick() {
        id = register_et_id.getText().toString();
        password = register_et_password.getText().toString();
        password_check = register_et_password_check.getText().toString();
        name = register_et_name.getText().toString();
        email = register_et_email.getText().toString();

        if(password.equals(password_check)) {
            //User 정보 입력//
            ArrayList<String> engaging_board = new ArrayList<String>();
            engaging_board.add("");
            User user = new User(id, password, name, email, engaging_board);
            user.fcmToken = FirebaseInstanceId.getInstance().getToken();
            //데이터베이스에 입력//
            mDatabaseReference = FirebaseDatabase.getInstance().getReference("information");
            mDatabaseReference.child(id).setValue(user);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
        }
    }
}
