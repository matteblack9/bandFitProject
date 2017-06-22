package com.bandfitproject.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bandfitproject.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.bandfitproject.login.LoginActivity.user;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    // Firebase
    //데이터베이스 클래스
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    // Views
    private ListView mListView;
    private EditText mEdtMessage;
    // Values
    private ChatAdapter mAdapter;
    private String userName;
    private  String chatRoomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        chatRoomName = intent.getStringExtra("chatRoomName");
        String boardName = intent.getStringExtra("boardName");

        setContentView(R.layout.chat_activity);
        setTitle(boardName);

        userName = user.getId();
        initViews();
        initFirebaseDatabase();
    }

    private void initViews() {
        mListView = (ListView) findViewById(R.id.list_message);
        mAdapter = new ChatAdapter(this, 0);
        mListView.setAdapter(mAdapter);

        mEdtMessage = (EditText) findViewById(R.id.edit_message);
        findViewById(R.id.btn_send).setOnClickListener(this);
    }

    private void initFirebaseDatabase() {
        //DB 인스턴스 생성 -> mysql로 치면 connection하는거
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //CREATE DB와 비슷한 역할
        mDatabaseReference = mFirebaseDatabase.getReference("boardChat").child(chatRoomName);

        //child에 대한 Event를 다룬다.
        mChildEventListener = new ChildEventListener() {
            @Override
            //child 노드 추가될때 발생되는 메소드
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);
                chatData.firebaseKey = dataSnapshot.getKey();
                mAdapter.add(chatData);
                //입력한쪽으로 view 이동!
                mListView.smoothScrollToPosition(mAdapter.getCount());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String firebaseKey = dataSnapshot.getKey();
                int count = mAdapter.getCount();
                for (int i = 0; i < count; i++) {
                    if (mAdapter.getItem(i).firebaseKey.equals(firebaseKey)) {
                        mAdapter.remove(mAdapter.getItem(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseReference.removeEventListener(mChildEventListener);
        Log.i("채팅 액티비티 onDestory", "리스너 삭제");
    }

    @Override
    public void onClick(View v) {
        String message = mEdtMessage.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            mEdtMessage.setText("");
            ChatData chatData = new ChatData();
            chatData.userName = userName;
            chatData.message = message;
            chatData.time = System.currentTimeMillis();
            mDatabaseReference.push().setValue(chatData);
        }
    }
}
