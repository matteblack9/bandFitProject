package com.bandfitproject;

import android.util.Log;

import com.bandfitproject.data.BoardData;
import com.bandfitproject.data.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.bandfitproject.login.LoginActivity.user;

/**
 * 데이터베이스 자료를 구하는 싱글톤 객체
 * 프로그램 전체를 아우르며 계속 참조되어야 되기때문에 제작
 */
public class BandFitDataBase {
    public static ChildEventListener mBoardEventListener;
    public static ChildEventListener mChatRoomEventListener;
    public static ChildEventListener mChatEventListener;
    public static ChildEventListener mInforEventListener;

    /**
     * 필요한 지점마다 레퍼런스를 해주어야된다.
     */
    public static DatabaseReference mBoardRef = FirebaseDatabase.getInstance().getReference("board");
    public static DatabaseReference mInforRef = FirebaseDatabase.getInstance().getReference("information");
    public static DatabaseReference mChatRef = FirebaseDatabase.getInstance().getReference("boardChat");

    public static List<BoardData> board_Items = new ArrayList<BoardData>();
    public static List<BoardData> chatRoom_Items = new ArrayList<BoardData>();

    // Getter 부분//
    public static DatabaseReference getmBoardRef() {return mBoardRef;}
    public static DatabaseReference getmInforRef() {return mInforRef;}
    public static DatabaseReference getmChatRef() {return mChatRef;}
    public static List<BoardData> getChatRoom_Items() {
        return chatRoom_Items;
    }
    public static List<BoardData> getBoard_items() {
        return board_Items;
    }

    public boolean isfirstevent = true;
    public boolean ischatevent = true;
    public boolean isinforevent = true;


    // 싱글톤 객체 생성 //
    private static BandFitDataBase dataBase_Instance ;
    private BandFitDataBase() {}
    public static BandFitDataBase getInstance() {
        if (dataBase_Instance == null) {
            dataBase_Instance =  new BandFitDataBase();
        }
        return dataBase_Instance;
    }

    /**
     * 게시판 정보를 긁어와서 리스트에 보관
     * 비동기 쓰레드로 작동하는데 이 작업이 빨리 되어야 게시판을 띄울 수 있다
     * 나중에 게시판 삭제하고 지우고 할때 주의해야 하므로 수정해야 될 듯 하다
     */
    public void initBoardData() {
        mBoardEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BoardData bData = dataSnapshot.getValue(BoardData.class);
                bData.firebaseKey = dataSnapshot.getKey();
                board_Items.add(bData);
                BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try{ Thread.sleep(1000);
                }catch(Exception e){}
                BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                for(BoardData b : board_Items) {
                    if(key.equals(b.firebaseKey)) {
                        board_Items.remove(b);
                        BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));
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
        board_Items.clear();
        //mBoardRef.removeEventListener(mChatRoomEventListener);
        mBoardRef.addChildEventListener(mBoardEventListener);
    }

    /**
     * 채팅룸 정보를 긁어옴
     */
    public void initChatRoomData() {
        System.out.println("ㅁ낭ㅇ허ㅏㅣㄹ엉라ㅣ노ㅓㅏㅣㄴㅇ로ㅓㅏㅣㅗㅓ");
        mChatRoomEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                BoardData bData = dataSnapshot.getValue(BoardData.class);
                if(!(chatRoom_Items.contains(bData))) {
                    if(user.engaging_board.contains(bData.chat_room_name)) {
                        chatRoom_Items.add(bData);
                    }
                }else {
                }
                BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                try{ Thread.sleep(1000);
                }catch(Exception e){}
                BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                BusProvider.getInstance().post(new BusEvent("ChatRoomActivity"));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        chatRoom_Items.clear();
        mBoardRef.removeEventListener(mChatRoomEventListener);
        mBoardRef.addChildEventListener(mChatRoomEventListener);
    }

    /**
     * 방 지울때, 모든 유저들 내용에서 삭제
     */
    public void removeInforEvent(final BoardData bData) {
        mInforEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User tempUser = dataSnapshot.getValue(User.class);
                for(User tempU : bData.en_people) {
                    if(tempU.id.equals(tempUser.id)) {
                        tempUser.engaging_board.remove(bData.chat_room_name);
                        mInforRef.child(tempUser.id).child("engaging_board").setValue(tempUser.engaging_board);
                        break;
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mInforRef.addChildEventListener(mInforEventListener);
    }

    /**
     * 방에 참가할 때 데이터베이스 부분에 정보를 삽입
     * 참가한 인원을 증가 시키고, 유저의 참가한 채팅방을 추가시킨다.
     * 나중에 리스트값을 통합시켜서 관리해야겠다.
     */
    public void push_Engaging_Board(ArrayList<String> arr, BoardData bData) {
        // 유저 정보에 참가한 게시판 추가
        mInforRef.child(user.id).child("engaging_board").setValue(arr);
        // 게시판 갱신 -> 참가한 인원 1증가, 참가한 사람 추가
        mBoardRef.child(bData.chat_room_name).setValue(bData);
        chatRoom_Items.add(bData);
        // 채팅방 리스트를 추가시켜주고
        // 게시판 리스트는 수정해주어야 된다

    }

    public void exit() {
        //mBoardRef.removeEventListener(mChatEventListener);
        mBoardRef.removeEventListener(mBoardEventListener);
        mBoardRef.removeEventListener(mChatRoomEventListener);
        board_Items.clear();
        chatRoom_Items.clear();
        //dataBase_Instance = null;
    }

    public void print_ar() {
        System.out.println("===================================================================");
        System.out.println();
        for(BoardData b : board_Items) {
            System.out.print(b.topic.toString());
            System.out.print(", ");
        }
        System.out.println();
        System.out.println("===================================================================");
    }

    /**
     * 한명이 나감
     * 인원 1명 감소
     * 참가한 사람 1명 삭제
     * 개인정보에서 내가 참가한 방 삭제
     * 채팅방에 누구누구 나갔다고 알림
     */
    public void outBoard(BoardData bData) {
        bData.engaged_people -= 1;
        for(User temp : bData.en_people) {
            if(temp.id.equals(user.id)) {
                bData.en_people.remove(user);
                break;
            }
        }
        mBoardRef.child(bData.chat_room_name).setValue(bData);
        user.engaging_board.remove(bData.chat_room_name);
        mInforRef.child(user.id).child("engaging_board").setValue(user.engaging_board);
        removeInforEvent(bData);
        chatRoom_Items.remove(bData);
    }

    public void removeBoard(BoardData bData) {
        removeInforEvent(bData);
        mBoardRef.child(bData.chat_room_name).removeValue();
        mChatRef.child(bData.chat_room_name).removeValue();
        chatRoom_Items.remove(bData);
        //user.engaging_board.remove(bData.chat_room_name);
        //mInforRef.child(user.id).child("engaging_board").setValue(user.engaging_board);
    }
}
