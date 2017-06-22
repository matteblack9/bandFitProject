package com.bandfitproject.board;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.BusEvent;
import com.bandfitproject.BusProvider;
import com.bandfitproject.R;
import com.bandfitproject.chat.ChatActivity;
import com.bandfitproject.chat.ChatData;
import com.bandfitproject.data.BoardData;
import com.bandfitproject.data.BoardData2;
import com.bandfitproject.data.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bandfitproject.board.BoardAdapter.FCM_MESSAGE_URL;
import static com.bandfitproject.board.BoardAdapter.SERVER_KEY;
import static com.bandfitproject.login.LoginActivity.user;


public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    Context context;
    List<BoardData> items;
    int item_layout;
    public static BoardData share_Data;

    public ChatRoomAdapter(Context context, List<BoardData> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    private void sendPostToFCM(User engaging_user, String msg) {
        final String message = user.id + " : " + msg;
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

    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom_list_item,
                parent, false);
        return new ChatRoomAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ChatRoomAdapter.ViewHolder holder, int position) {
        final BoardData item = items.get(position);
        String type = "[" + item.type + "]" ;
        holder.text_type.setText(type);
        holder.text_topic.setText(item.topic);
        holder.text_date.setText(item.date);
        if(!user.id.equals(item.admin)) {
            holder.btn_removeBoard.setText("나가기");
        }

        holder.btn_removeBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item.admin.equals(user.id)) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setMessage("방을 지우시겠습니까?").setCancelable(false).setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    BandFitDataBase.getInstance().removeBoard(item);
                                }
                            }).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = ab.create();
                    alert.setTitle("방삭제");
                    alert.show();
                }
                else{
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setMessage("방을 나가시겠습니까?").setCancelable(false).setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    BandFitDataBase.getInstance().outBoard(item);
                                    // 방을 나갔을 때, 나타나는 메세지 //
                                    ChatData mChatData = new ChatData();
                                    mChatData.userName = "ADMIN";
                                    mChatData.time = System.currentTimeMillis();
                                    mChatData.message = user.id + "님이 나갔습니다." ;
                                    DatabaseReference mRef =
                                            FirebaseDatabase.getInstance().getReference("boardChat").child(item.chat_room_name);
                                    mRef.push().setValue(mChatData);
                                    BusProvider.getInstance().post(new BusEvent("BoardActivity"));
                                    for(User engaging_user : item.en_people) {
                                        if(!engaging_user.id.equals(user.id))
                                            sendPostToFCM(engaging_user, mChatData.message);
                                    }
                                    // notifyDataSetChanged();
                                }
                            }).setNegativeButton("아니오",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = ab.create();
                    alert.setTitle("방삭제");
                    alert.show();
                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                String chatRoomName = item.chat_room_name;
                intent.putExtra("chatRoomName", chatRoomName);
                intent.putExtra("boardName", item.topic);
                share_Data = item;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.chatroom_cardview) CardView cardView;
        @BindView(R.id.chatroom_textview_type) TextView text_type;
        @BindView(R.id.chatroom_textview_topic) TextView text_topic;
        @BindView(R.id.chatroom_textview_date) TextView text_date;
        @BindView(R.id.btn_removeBoard) Button btn_removeBoard;

        // ++ //
        @BindView(R.id.tx_id) TextView tx_id;
        @BindView(R.id.tx_msg) TextView tx_msg;
        // ++ //

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}