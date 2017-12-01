package ua.bellkross.client.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import ua.bellkross.client.model.ArrayListRooms;
import ua.bellkross.client.model.Room;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class DBHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "LINKED_TASKS_DB";

    public static String ROOM_TABLE_NAME = "ROOM";
    public static String ROOM_ID = "ROOM_ID";
    public static String ROOM_SERVER_ID = "ROOM_SERVER_ID";
    public static String ROOM_LIST_ID = "ROOM_LIST_ID";
    public static String ROOM_NAME = "NAME";
    public static String ROOM_PASSWORD = "PASSWORD";

    public static String TASK_TABLE_NAME = "TASK";
    public static String TASK_ID = "TASK_ID";
    public static String TASK_SERVER_ID = "TASK_SERVER_ID";
    public static String TASK_LIST_ID = "TASK_LIST_ID";
    public static String FK_ROOM_ID = "FK_ROOM_ID";
    public static String TASK_TEXT = "TASK_TEXT";
    public static String TASK_NAME_OF_CREATOR = "TASK_NAME_OF_CREATOR";
    public static String TASK_STATE = "TASK_STATE";
    public static String TASK_DEADLINE = "TASK_DEADLINE";
    public static String TASK_COMMENTS = "TASK_COMMENTS";
    public static DBHelper instance;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ROOM_TABLE_NAME + " (" +
                ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ROOM_SERVER_ID + " INTEGER NOT NULL," +
                ROOM_LIST_ID + " INTEGER NOT NULL," +
                ROOM_NAME + " TEXT NOT NULL," +
                ROOM_PASSWORD + " TEXT NOT NULL" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE_NAME + " (" +
                TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TASK_SERVER_ID + " INTEGER NOT NULL," +
                TASK_LIST_ID + " INTEGER NOT NULL," +
                TASK_TEXT + " TEXT NOT NULL," +
                TASK_NAME_OF_CREATOR + " TEXT NOT NULL," +
                TASK_STATE + " INTEGER NOT NULL," +
                TASK_DEADLINE + " DATETIME NOT NULL," +
                TASK_COMMENTS + " TEXT NULL," +
                FK_ROOM_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + FK_ROOM_ID + ") REFERENCES " + ROOM_TABLE_NAME + "(" + ROOM_ID + ")" +
                ");");

        Log.d(LOG_TAG,"database on create");
    }

    public int addTask(final Task task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_SERVER_ID,task.getServerDbID());
        contentValues.put(TASK_LIST_ID,task.getArrayListID());
        contentValues.put(TASK_TEXT,task.getText());
        contentValues.put(TASK_NAME_OF_CREATOR,task.getNameOfCreator());
        contentValues.put(TASK_STATE,task.getState());
        contentValues.put(TASK_DEADLINE,task.getDeadline().getTime());
        contentValues.put(TASK_COMMENTS,task.getComments());
        contentValues.put(FK_ROOM_ID,task.getRoomID());
        int pos = (int) db.insert(TASK_TABLE_NAME, null, contentValues);

        db.close();
        return pos;
    }

    public int addRoom(final Room room){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ROOM_SERVER_ID, room.getServerDbID());
        contentValues.put(ROOM_LIST_ID, room.getArrayListID());
        contentValues.put(ROOM_NAME, room.getName());
        contentValues.put(ROOM_PASSWORD, room.getPassword());
        int pos = (int) db.insert(ROOM_TABLE_NAME,null,contentValues);
        contentValues.clear();
        db.close();
        return pos;
    }

    public ArrayList<Room> refresh(ArrayList<Room> rooms){
        this.clear();
        int position = -1;
        int pos;
        SQLiteDatabase db = getWritableDatabase();
        for (Room room:
             rooms) {
            room.setArrayListID(++position);
            room.setDbID(addRoom(room));
            pos = -1;
            for (Task task:
                 room.getTasks()) {
                task.setArrayListID(++pos);
                task.setDbID(addTask(task));
            }
        }
        db.close();
        return rooms;
    }

    public void clear(){
        SQLiteDatabase db = getWritableDatabase();
        for (Room room : ArrayListRooms.getInstance()){
            deleteRoom(room.getDbID());
            for (Task task:
                 room.getTasks()) {
                deleteTask(task.getDbID());
            }
        }
        db.close();
    }

    public void deleteTask(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+TASK_TABLE_NAME+" WHERE "+TASK_ID+" = "+id+";");
        db.close();
    }

    public void deleteRoom(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM "+ROOM_TABLE_NAME+" WHERE "+ROOM_ID+" = "+id+";");
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static DBHelper getInstance(Context context) {
        return instance == null ? new DBHelper(context) : instance;
    }

    public static DBHelper getInstance() {
        return instance;
    }

}
