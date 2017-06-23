package com.bandfitproject.board;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bandfitproject.R;
import com.bandfitproject.chat.ChatActivity;
import com.bandfitproject.data.BoardData;
import com.bandfitproject.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import static com.bandfitproject.board.ChatRoomAdapter.share_Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import static com.bandfitproject.login.LoginActivity.user;

public class BoardMakeActivity extends AppCompatActivity {
    //리소스와 바인딩//
    @BindView(R.id.make_board_spinner_type) Spinner make_board_spinner_type;
    @BindView(R.id.make_board_et_topic)EditText make_board_et_topic;
    @BindView(R.id.make_board_et_desc)EditText make_board_et_desc;
    @BindView(R.id.make_board_et_place)Button make_board_et_place;
    @BindView(R.id.make_board_et_people)EditText make_board_et_people;
    @BindView(R.id.make_board_et_date)Button make_board_et_date;
    @BindView(R.id.make_board_btn)Button make_board_btn;

    // board의 변수들 //
    private String type, topic, place, date, description, people, chatRoomName = "";

    public static final int CLICK_SUCCESSFUL = 1;

    // 데이터베이스 //
    DatabaseReference mRef  = FirebaseDatabase.getInstance().getReference("board");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_make_activity);
        setTitle(user.id);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CLICK_SUCCESSFUL || resultCode == CLICK_SUCCESSFUL){
            String address = data.getStringExtra("Address");
            make_board_et_place.setText(address);
        }
    }

    public void show_Place(View view) {
        Intent intent = new Intent(getApplicationContext(), SearchMapActivity.class);
        startActivityForResult(intent, CLICK_SUCCESSFUL);
    }

    @OnItemSelected(R.id.make_board_spinner_type)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = parent.getItemAtPosition(position).toString();
    }

    @OnClick(R.id.make_board_et_date)
    void onDateTextClick() {
        GregorianCalendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(BoardMakeActivity.this, dateSetListener, year, month, day).show();
    }

    /**
     * 방 만들기 버튼을 누를 때 발생하는 이벤트
     * DB에 게시판 내용을 저장하고, 채팅서버를 만든다.
     */
    @OnClick(R.id.make_board_btn)
    void onClick() {
        //뷰의 정보를 긁어온다//

        topic = make_board_et_topic.getText().toString();
        description = make_board_et_desc.getText().toString();
        place = make_board_et_place.getText().toString();
        people = make_board_et_people.getText().toString();
        date = make_board_et_date.getText().toString();


        if(topic.length() != 0 && description.length() !=0 && place.length() != 0 && people.length() != 0 && date.length() != 0) {
            // 다이얼 로그 생성, 완료 시 방이 만들어진다. //

            AlertDialog.Builder ab = new AlertDialog.Builder(BoardMakeActivity.this);
            ab.setMessage("방을 만드시겠습니까?").setCancelable(false).setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /**
                             * 데이터 베이스에 접근해서, 게시판 내용을 삽입
                             * engaging_people -> en_people
                             */
                            chatRoomName = mRef.push().getKey(); //게시판 이름 결정

                            // 게시판 채팅방, 참가한 사람을 기록하는 변수들
                            ArrayList<User> en_people = new ArrayList<User>();
                            en_people.add(user);
                            ArrayList<String> chatRoom = new ArrayList<String>();

                            BoardData bData = new BoardData(topic, type, 1,
                                    Integer.parseInt(people),
                                    date, chatRoomName, place,
                                    description, chatRoom, en_people);
                            bData.admin = user.id;
                            // 데이터베이스에 정보 입력 -> 게시판 정보
                            mRef.child(chatRoomName).setValue(bData);

                            // 데이터베이스에 유저가 방을 만들면서 채팅방에 참가한것을 갱신함
                            DatabaseReference tRef = FirebaseDatabase.getInstance().getReference("information")
                                    .child(user.id).child("engaging_board") ;
                            user.engaging_board.add(chatRoomName);
                            tRef.setValue(user.engaging_board);

                            setResult(RESULT_OK);
                            Intent intent = new Intent(BoardMakeActivity.this, ChatActivity.class);
                            String chatRoomName = bData.chat_room_name;
                            intent.putExtra("chatRoomName", chatRoomName);
                            intent.putExtra("boardName", bData.topic);
                            share_Data = bData;
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("아니오",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = ab.create();
            alert.setTitle("방 만들기");
            alert.show();
        }
        else {
            AlertDialog.Builder alert = new AlertDialog.Builder(BoardMakeActivity.this);
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                }
            });
            alert.setMessage("모든 정보를 입력해주십시오");
            alert.show();
        }
    }

    /**
     * 날짜를 표현해 주는 곳입니다.
     * DatePickerDialog.OnDateSetListener 는 (년, 월, 일)을 표시해 주고,
     * 캘린더에서 작업을 완료했으면 TimePickerDialog.OnTimeSetListener을 호출해서
     * 시, 분을 조정합니다.
     */
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date = String.format("%d월 %d일",month+1, dayOfMonth);
            new TimePickerDialog(BoardMakeActivity.this
                    , timeSetListener, 0, 0, false).show();
        }
    };

    /**
     * DatePickerDialog.OnDateSetListener에 의해 호출되며
     * 시, 분을 조정하고, date Text에 표시합니다.
     */
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String dateView = date;
            date += " " + String.format("%d:%d", hourOfDay, minute);
            if(hourOfDay > 12) {
                dateView += " 오후 " + (hourOfDay-12) + " : "  + minute;
            } else {
                dateView += " 오전 " + hourOfDay + " : " + minute;
            }
            make_board_et_date.setText(dateView);
        }
    };

}
