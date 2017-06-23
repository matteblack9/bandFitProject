package com.bandfitproject.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.R;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import butterknife.Unbinder;

public class BoardActivity extends Fragment {
    @BindView(R.id.board_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.board_sp_search) Spinner board_sp_search;

    BoardAdapter rAdapter = null;

    private Unbinder unbinder;

    @OnItemSelected(R.id.board_sp_search)
    void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i(this.getClass().getName(), "스피너");
        String sp_type = parent.getItemAtPosition(position).toString();
        rAdapter.filter(sp_type);
        /*if(!sp_type.equals("종목을 선택하세요")) {
            rAdapter.filter(sp_type);
        }*/
    }

    public BoardActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getName(), FirebaseDatabase.getInstance().toString() + " onDestroy");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(getClass().getName(), "onCreateView");
        View view = inflater.inflate(R.layout.board_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);

        rAdapter = new BoardAdapter(getContext(), BandFitDataBase.getInstance().board_Items, R.layout.board_fragment);
        recyclerView.setAdapter(rAdapter);
        return view;
    }
}
