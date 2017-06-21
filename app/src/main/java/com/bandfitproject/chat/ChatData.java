package com.bandfitproject.chat;

public class ChatData {
    public String firebaseKey; // Firebase Realtime Database 에 등록된 Key 값
    public String userName; // 사용자 이름
    public String userPhotoUrl; // 사용자 사진 URL
    public String userEmail; // 사용자 이메일주소
    public String message; // 작성한 메시지
    public long time; // 작성한 시간

    public ChatData() {}

    public ChatData(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public String getUserName() {return  userName;}
    public void setUserName(String userName) {this.userName = userName;}

    public String getMessage() {return  message;}
    public void setMessage(String message) {this.message = message;}
}