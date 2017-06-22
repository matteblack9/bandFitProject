package com.bandfitproject.data;

import java.io.Serializable;
import java.util.ArrayList;

public class BoardData implements Serializable{
    public String firebaseKey = "";
    public String topic;
    public String type;
    public String place;
    public int engaged_people;
    public int need_people;
    public String date;
    public String chat_room_name;
    public String desc;
    public String admin;

    public ArrayList<String> chat_room;
    public ArrayList<User> en_people;


    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getEngaged_people() {
        return engaged_people;
    }

    public void setEngaged_people(int engaged_people) {
        this.engaged_people = engaged_people;
    }

    public int getNeed_people() {
        return need_people;
    }

    public void setNeed_people(int need_people) {
        this.need_people = need_people;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChat_room_name() {
        return chat_room_name;
    }

    public void setChat_room_name(String chat_room_name) {
        this.chat_room_name = chat_room_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ArrayList<String> getChat_room() {
        return chat_room;
    }

    public void setChat_room(ArrayList<String> chat_room) {
        this.chat_room = chat_room;
    }

    public BoardData() {

    }

    /*public BoardData(String topic, String type, int engaged_people,
                      int need_people, String date, String chat_room_name,
                      String desc, ArrayList<String> chat_room, ArrayList<String> engaging_people) {
        this.topic = topic;
        this.type = type;
        this.engaged_people = engaged_people;
        this.need_people = need_people;
        this.date = date;
        this.chat_room_name = chat_room_name;
        this.desc = desc;
        this.chat_room = chat_room;
        this.engaging_people = engaging_people ;
    }*/


    public BoardData(String topic, String type, int engaged_people,
                     int need_people, String date, String chat_room_name, String place,
                     String desc, ArrayList<String> chat_room, ArrayList<User> en_people) {
        this.topic = topic;
        this.type = type;
        this.place = place;
        this.engaged_people = engaged_people;
        this.need_people = need_people;
        this.date = date;
        this.chat_room_name = chat_room_name;
        this.desc = desc;
        this.chat_room = chat_room;
        this.en_people = en_people ;
    }

    public BoardData(String topic, String type, int engaged_people,
                     int need_people, String date) {
        this.topic = topic;
        this.type = type;
        this.engaged_people = engaged_people;
        this.need_people = need_people;
        this.date = date;
    }

    public void print() {
        System.out.println("제목: " + this.topic) ;
        System.out.println("종목: " + this.type) ;
        System.out.println("참여한 사람: " + this.engaged_people) ;
        System.out.println("필요한 사람: " + this.need_people) ;
        System.out.println("날짜: " + this.date) ;
        System.out.println("채팅방 이름: " + this.chat_room_name) ;
    }



}
