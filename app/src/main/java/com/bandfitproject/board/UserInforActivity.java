package com.bandfitproject.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bandfitproject.R;

public class UserInforActivity extends Fragment {
    @Override
    public void onResume() {
        super.onResume();
       // Log.i(this.getClass().getName(), "여기는 유저정보 onResume 입니다.");
    }

    @Override
    public void onPause() {
     //   Log.i(this.getClass().getName(), "여기는 유저정보 onPause 입니다.");
        super.onPause();
    }

    @Override
    public void onStop() {
     //   Log.i(this.getClass().getName(), "여기는 유저정보 onStop 입니다.");
        super.onStop();
    }
    @Override
    public void onDestroy() {
      //  Log.i(this.getClass().getName(), "여기는 유저정보 onDestroy 입니다.");
        super.onDestroy();
    }

    public UserInforActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.board_user_infor_fragment, container, false);
        return view;
    }
}
