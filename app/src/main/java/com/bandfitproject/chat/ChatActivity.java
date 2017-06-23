package com.bandfitproject.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.bandfitproject.R;
import com.bandfitproject.data.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bandfitproject.board.BoardAdapter.FCM_MESSAGE_URL;
import static com.bandfitproject.board.BoardAdapter.SERVER_KEY;
import static com.bandfitproject.board.ChatRoomAdapter.share_Data;
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
    private String chatRoomName;
    private String boardName ;
    private long def_time = 0;

    @Override protected void onResume(){
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        chatRoomName = intent.getStringExtra("chatRoomName");
        boardName = intent.getStringExtra("boardName");

        setContentView(R.layout.chat_activity);
        setTitle(boardName);

        userName = user.getId();
        initViews();
        initFirebaseDatabase();
    }

    private void sendPostToFCM(User engaging_user, String msg) {
        final String message = "게시판: " + boardName + '\n' + user.id + " : " + msg;
        final String fcmToken = engaging_user.fcmToken;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", message);
                    notification.put("title", "test");
                    root.put("notification", notification);
                    root.put("to", fcmToken);
                    // FMC 메시지 생성 end

                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
                //수정
                mListView.setSelection(mAdapter.getCount());
                //mListView.smoothScrollToPosition(mAdapter.getCount());
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
        def_time = 0;
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

            mListView.smoothScrollToPosition(mAdapter.getCount());
        }
        for(User engaging_user : share_Data.en_people) {
            if(!engaging_user.id.equals(user.id)) {
                sendPostToFCM(engaging_user, message);
            }
        }
    }
}