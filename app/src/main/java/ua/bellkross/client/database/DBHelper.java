package ua.bellkross.client.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
                "FOREIGN KEY (" + FK_ROOM_ID + ") REFERENCES " + ROOM_TABLE_NAME + "(" + FK_ROOM_ID + ")" +
                ");");

        Log.d(LOG_TAG,"database on created");
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
