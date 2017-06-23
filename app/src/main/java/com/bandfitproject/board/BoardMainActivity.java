package com.bandfitproject.board;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bandfitproject.BandFitDataBase;
import com.bandfitproject.BusEvent;
import com.bandfitproject.BusProvider;

import com.bandfitproject.R;
import com.bandfitproject.login.LoginActivity;
import com.bandfitproject.register.RegisterActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.otto.Subscribe;


import static com.bandfitproject.login.LoginActivity.user;


public class BoardMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    DatabaseReference logState = FirebaseDatabase.getInstance().getReference("information").child(user.id)
            .child("isLogin");
    public static boolean isChanged = false;
    boolean logout = false;
    public static final int MAKR_BOARD_SUCCESS = 1;
    BroadcastReceiver mReceiver;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board_main);
        BusProvider.getInstance().register(this);
        DatabaseReference logState = FirebaseDatabase.getInstance().getReference("information").child(user.id)
                .child("isLogin");


         //브로드캐스트의 액션을 등록하기 위한 인텐트 필터
         IntentFilter intentFilter = new IntentFilter();
         intentFilter.addAction("com.bandfit.SEND_BROAD_CAST");
         //동적 리시버 구현
         mReceiver = new BroadcastReceiver(){
            @Override public void onReceive(Context context, Intent intent){
                isChanged = true;
                mSectionsPagerAdapter.notifyDataSetChanged();
                isChanged = false;
            }
         };
         //Receiver 등록
         registerReceiver(mReceiver, intentFilter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        // 프레그먼트 최신화를 요청하는 이벤트를 받는다. //
        navigationView.setNavigationItemSelectedListener(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //Log.i("테스트입니다. 여기는 " + mSectionsPagerAdapter.getItem(position).getClass() +  "입니다.", "onPageSelected 입니다.");
                if(isChanged) {
                    switch (position) {
                        case 0 :
                            mSectionsPagerAdapter.getItemPosition(UserInforActivity.class);
                            break;
                        case 1 :
                            mSectionsPagerAdapter.getItemPosition(BoardActivity.class);
                            break;
                        case 2 :
                            mSectionsPagerAdapter.getItemPosition(ChatRoomAdapter.class);
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // 방만들기 버튼 //
        FloatingActionButton board_main_fab = (FloatingActionButton) findViewById(R.id.board_main_fab);

        board_main_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoardMainActivity.this, BoardMakeActivity.class);
                startActivityForResult(intent, MAKR_BOARD_SUCCESS);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            AlertDialog.Builder ab = new AlertDialog.Builder(BoardMainActivity.this);
            ab.setMessage("로그아웃을 하시겠습니까?").setCancelable(false).setPositiveButton("예",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // 로그인 상태 false로 바꿔줌
                            DatabaseReference logState = FirebaseDatabase.getInstance().getReference("information").child(user.id)
                                    .child("isLogin");
                            logState.setValue(false);

                            Intent intent = new Intent(BoardMainActivity.this, LoginActivity.class);
                            logout = true;
                            BandFitDataBase.getInstance().exit();

                            BandFitDataBase.getInstance().initBoardData();

                            // 자동로그인 상태 해제 //
                            SharedPreferences logRef = getSharedPreferences("auto_login", MODE_PRIVATE);
                            SharedPreferences.Editor editor = logRef.edit();
                            editor.clear();
                            editor.commit();
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
            alert.setTitle("로그아웃");
            alert.show();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     *
     * @param requestCode : 게시판 만들어진거 완료되었는가?
     * @param resultCode : OK이면 정상적으로 만들어짐
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAKR_BOARD_SUCCESS) {
            if(resultCode == RESULT_OK) {
                // isChanged가 true어야 뷰페이져 프래그먼트 갱신이 가능 //
                isChanged = true;
                mSectionsPagerAdapter.notifyDataSetChanged();
                isChanged = false;
            }
        }
    }

    @Subscribe
    public void changeDetect(BusEvent mBusEvnet) {
        System.out.println("eeeeeeeeeeee");
        isChanged = true;
        mSectionsPagerAdapter.notifyDataSetChanged();
        System.out.println("구독을 시작합니다.");
    }


    @Override
    protected void onDestroy() {
        logState.setValue(false);
        if(!logout)
            BandFitDataBase.getInstance().exit();
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_board_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.board_main, container, false);
            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return new UserInforActivity();
                case 1:
                    return new BoardActivity();
                case 2:
                    return new ChatRoomActivity();
            }
            return PlaceholderFragment.newInstance(position + 1);
        }
        @Override
        public int getItemPosition(Object object) {
            Log.i("asdasdsad", "sadjkgjkdflgjkdflg");
            if (object.getClass() == BoardActivity.class || object.getClass() == ChatRoomActivity.class) {
                return  POSITION_NONE;
            } else
                return POSITION_UNCHANGED;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "내정보";
                case 1:
                    return "게시판";
                case 2:
                    return "채팅방";
            }
            return null;
        }
    }
}
