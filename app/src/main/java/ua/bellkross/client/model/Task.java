package ua.bellkross.client.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {

    private int dbID;
    private int serverDbID;
    private int arrayListID;
    private int roomID;
    private String text;
    private String nameOfCreator;
    private int state;
    private String comments;

    public Task(int serverDbID, int roomID, String text, String nameOfCreator, int state) {
        this.serverDbID = serverDbID;
        this.roomID = roomID;
        this.text = text;
        this.nameOfCreator = nameOfCreator;
        this.state = state;
    }

    public Task(int serverDbID, int roomID, String text, String nameOfCreator, int state, String comments) {
        this.serverDbID = serverDbID;
        this.roomID = roomID;
        this.text = text;
        this.nameOfCreator = nameOfCreator;
        this.state = state;
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Task{" +
                "dbID=" + dbID +
                ", serverDbID=" + serverDbID +
                ", arrayListID=" + arrayListID +
                ", roomID=" + roomID +
                ", text='" + text + '\'' +
                ", nameOfCreator='" + nameOfCreator + '\'' +
                ", state=" + state +
                ", comments='" + comments + '\'' +
                '}';
    }

    public int getDbID() {
        return dbID;
    }

    public void setDbID(int dbID) {
        this.dbID = dbID;
    }

    public int getServerDbID() {
        return serverDbID;
    }

    public void setServerDbID(int serverDbID) {
        this.serverDbID = serverDbID;
    }

    public int getArrayListID() {
        return arrayListID;
    }

    public void setArrayListID(int arrayListID) {
        this.arrayListID = arrayListID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNameOfCreator() {
        return nameOfCreator;
    }

    public void setNameOfCreator(String nameOfCreator) {
        this.nameOfCreator = nameOfCreator;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
