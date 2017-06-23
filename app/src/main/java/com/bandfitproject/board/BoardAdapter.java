package com.bandfitproject.board;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.BusEvent;
import com.bandfitproject.BusProvider;
import com.bandfitproject.R;
import com.bandfitproject.chat.ChatData;
import com.bandfitproject.data.BoardData;
import com.bandfitproject.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bandfitproject.login.LoginActivity.user;

public class BoardAdapter extends RecyclerView.Adapter<BoardAdapter.ViewHolder> {
    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    public static final String SERVER_KEY = "AAAAd0d5xTc:APA91bFiEx-ZTM5vbH9dmlKbQDP5bDgG8qmx" +
            "m2mDBDxKT1ko8Q8QsTSORLIv3JMwpnKsxhc9dtjszQWVaeKEeSXwowUvndSfWukL--7jjEHhriQo2rStUwCrAvbRebL6WEisaC_g-gMM";

    Context context;
    List<BoardData> items;
    int item_layout;

    // 검색 //
    List<BoardData> searchList;

    private void sendPostToFCM(User engaging_user) {
        final String message = user.id + "님이 참가했습니다.";
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

    public BoardAdapter(Context context, List<BoardData> items, int item_layout) {
        this.context = context;
        this.items = items;

        // 검색 //
        this.searchList = new ArrayList<BoardData>();
        this.searchList.addAll(this.items);

        this.item_layout = item_layout;
    }

    @Override
    public BoardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.board_list_item,
                parent, false);
        return new BoardAdapter.ViewHolder(v);
    }


    // 검색 //
   public void filter(String type) {
       if(!type.equals("종목을 선택하세요")) {
           items.clear();
           for (BoardData bData : searchList) {
               if (type.equals(bData.type)) {
                   items.add(bData);
               }
           }
       } else {
           items.clear();
           for (BoardData bData : searchList) {
               items.add(bData);
           }
       }
       notifyDataSetChanged();
   }

    @Override
    public void onBindViewHolder(final BoardAdapter.ViewHolder holder, int position) {
        final BoardData item = items.get(position);
        String type = "[" + item.type + "]" ;
        holder.text_type.setText(type);
        holder.text_topic.setText(item.topic);
        holder.text_date.setText(item.date);
        holder.text_place.setText(item.place);
        String people = item.engaged_people + " / " + item.need_people;
        holder.text_people.setText(people);
        holder.tv_desc.setText(item.desc);

        holder.engageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user.engaging_board.contains(item.chat_room_name)) {
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                    alt_bld.setMessage("이미 참가한 방입니다.").setCancelable(
                            false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Action for 'Yes' Button
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    // Title for AlertDialog
                    alert.setTitle("문제가 있어요!");
                    // Icon for AlertDialog
                    alert.show();
                }
                else if(item.engaged_people == item.need_people && !user.engaging_board.contains(item.chat_room_name)){
                    AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);
                    alt_bld.setMessage("방이 다찼습니다.").setCancelable(
                            false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // Action for 'Yes' Button
                                }
                            });
                    AlertDialog alert = alt_bld.create();
                    // Title for AlertDialog
                    alert.setTitle("문제가 있어요!");
                    // Icon for AlertDialog
                    alert.show();
                }
                else {
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setMessage("참가 하시겠습니까?").setCancelable(false).setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    /**
                                     * 참가 했을 경우
                                     * 참가한 인원 하나 증가
                                     * 참가한 인원 아이디 추가
                                     */
                                    BoardData bData = item;
                                    bData.en_people.add(user);
                                    bData.engaged_people += 1;
                                    user.engaging_board.add(item.chat_room_name);

                                    // 처음 입장했을때, 나타나는 메세지 //
                                    ChatData mChatData = new ChatData();
                                    mChatData.userName = "ADMIN";
                                    mChatData.time = System.currentTimeMillis();
                                    mChatData.message = user.id + "님이 참가하셨습니다." ;
                                    DatabaseReference mRef =
                                            FirebaseDatabase.getInstance().getReference("boardChat").child(item.chat_room_name);
                                    mRef.push().setValue(mChatData);

                                    for(User engaging_user : bData.en_people) {
                                        if(!engaging_user.id.equals(user.id))
                                            sendPostToFCM(engaging_user);
                                    }
                                    // 수정된 데이터를 데이터베이스에 넣는다.
                                    BandFitDataBase.getInstance().push_Engaging_Board(user.engaging_board, bData);
                                    BusProvider.getInstance().post(new BusEvent("BoardActivity"));

                                    Log.i("방 생성 완료", "방이름: " + bData.chat_room_name);
                                }
                            }).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = ab.create();
                    alert.setTitle("참가하기");
                    alert.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cardview) CardView cardView;
        @BindView(R.id.board_textview_type) TextView text_type;
        @BindView(R.id.board_textview_topic) TextView text_topic;
        @BindView(R.id.board_textview_date) TextView text_date;
        @BindView(R.id.board_textview_people) TextView text_people;
        @BindView(R.id.board_textview_place)TextView text_place;
        @BindView(R.id.board_tv_desc) TextView tv_desc;
        @BindView(R.id.btn_engage) Button engageBtn;
        //@BindView(R.id.board_sp_search) Spinner board_sp_search;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}