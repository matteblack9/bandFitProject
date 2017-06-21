package com.bandfitproject.board;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.bandfitproject.data.BoardData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bandfitproject.login.LoginActivity.user;

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
