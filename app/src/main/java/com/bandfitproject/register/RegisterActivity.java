package com.bandfitproject.register;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.bandfitproject.data.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bandfitproject.login.LoginActivity.user;

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
    boolean result = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        ButterKnife.bind(this);
    }

    public boolean check_overlap() {
        Query query = FirebaseDatabase.getInstance().getReference("information").child(id);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 중복되는 값이 없다.
                        if(dataSnapshot.getValue() == null) {
                            result = true;
                            end(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );
        if(result) {
            result = false;
            return true;
        }
        return false;
    }
    public boolean end(boolean result) {
        return true;
    }
    @OnClick(R.id.register_btn_sign_in)
    void onSignInClick() {
        id = register_et_id.getText().toString();
        password = register_et_password.getText().toString();
        password_check = register_et_password_check.getText().toString();
        name = register_et_name.getText().toString();
        email = register_et_email.getText().toString();
        Query query = FirebaseDatabase.getInstance().getReference("information").child(id);
        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // 중복되는 값이 없다.
                        if(dataSnapshot.getValue() == null) {
                            if(email == null || !email.contains("@")) {
                                Toast.makeText(RegisterActivity.this, "이메일을 확인하세요", Toast.LENGTH_SHORT).show();
                            } else if(!password.equals(password_check)) {
                                Toast.makeText(RegisterActivity.this, "비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                            } else if(name == null) {
                                Toast.makeText(RegisterActivity.this, "이름을 확인하세요", Toast.LENGTH_SHORT).show();
                            } else if(password == null) {
                                Toast.makeText(RegisterActivity.this, "비밀번호 란을 채워주세요", Toast.LENGTH_SHORT).show();
                            } else if(password_check == null) {
                                Toast.makeText(RegisterActivity.this, "비밀번호 확인란을 채워주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                if(password.length() < 8) {
                                    Toast.makeText(RegisterActivity.this, "비밀번호를 8자리 이상으로 해주세요.", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog.Builder ab = new AlertDialog.Builder(RegisterActivity.this);
                                    ab.setMessage("회원가입을 완료하시겠습니까?").setCancelable(false).setPositiveButton("예",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int dialog_id) {
                                                    //User 정보 입력//
                                                    ArrayList<String> engaging_board = new ArrayList<String>();
                                                    engaging_board.add("");
                                                    User user = new User(id, password, name, email, engaging_board);
                                                    user.fcmToken = FirebaseInstanceId.getInstance().getToken();
                                                    //데이터베이스에 입력//
                                                    mDatabaseReference = FirebaseDatabase.getInstance().getReference("information");
                                                    mDatabaseReference.child(id).setValue(user);
                                                    finish();
                                                }
                                            }).setNegativeButton("아니오",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int dialog_id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alert = ab.create();
                                    alert.setTitle("회원가입");
                                    alert.show();
                                }
                            }
                        }else {
                            Toast.makeText(RegisterActivity.this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
}
