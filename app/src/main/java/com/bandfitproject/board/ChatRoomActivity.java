package com.bandfitproject.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.bandfitproject.data.BoardData;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ChatRoomActivity extends Fragment {
    @BindView(R.id.chatroom_recycler_view )
    RecyclerView recyclerView;

    ChatRoomAdapter cAdapter = null;
    List<BoardData> items = new ArrayList<>();

    private Unbinder unbinder;


    public ChatRoomActivity() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.i(this.getClass().getName(), "여기는 채팅방 onResume 입니다.");
    }

    @Override
    public void onPause() {
        //Log.i(this.getClass().getName(), "여기는 채팅방 onPause 입니다.");
        super.onPause();
    }

    @Override
    public void onStop() {
        //Log.i(this.getClass().getName(), "여기는 채팅방 onStop 입니다.");
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("6chatroom에 아이템 몇개일까여? " , BandFitDataBase.getChatRoom_Items().size()  + "개 입니다.");

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("4chatroom에 아이템 몇개일까여? " , BandFitDataBase.getChatRoom_Items().size()  + "개 입니다.");

        View view = inflater.inflate(R.layout.board_chat_room_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        cAdapter = new ChatRoomAdapter(getContext(), BandFitDataBase.getInstance().getChatRoom_Items()
                , R.layout.board_chat_room_fragment);
        recyclerView.setAdapter(cAdapter);
        return view;
    }
}
