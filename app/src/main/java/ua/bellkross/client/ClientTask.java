package ua.bellkross.client;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ua.bellkross.client.adapters.ArrayListRooms;
import ua.bellkross.client.adapters.GridViewAdapter;
import ua.bellkross.client.database.DBHelper;
import ua.bellkross.client.model.Room;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class ClientTask extends AsyncTask<Void, Void, Void> {

    //
    public static final String IP = "165.227.52.31";
    public static final int PORT = 80;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Resender resend;
    private String login;
    private boolean refreshWithoutNotify = false;

    private static ClientTask instance;

    public ClientTask(String login) {
        this.login = login;
        instance = this;
    }

    public static ClientTask getInstance(String login) {
        return instance == null ? new ClientTask(login) : instance;
    }

    public static ClientTask getInstance() {
        return instance;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            InetAddress ipAddress = InetAddress.getByName(IP);

            this.socket = new Socket(ipAddress, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            resend = new Resender();
            resend.execute();
            out = new PrintWriter(socket.getOutputStream(), true);
            push(login);
            Thread.sleep(100);
            push("7");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void push(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.printf(message);
            }
        }).start();
        Log.d(LOG_TAG, "message \"" + message + "\" pushed");
    }

    public void close() {
        try {
            resend.setStop();
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Thread's haven't closed");
        }
    }

    private class Resender extends AsyncTask<Void, Void, Void> {

        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                while (!stoped) {
                    final String str = in.readLine();
                    Log.d(LOG_TAG, "read = " + str);
                    int command = Integer.parseInt(str.substring(0, 1));
                    Log.d(LOG_TAG, "command = " + command);
                    readCommand(str, command);
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Ошибка при получении сообщения.");
                e.printStackTrace();
            }
            return null;
        }
    }

    private void readCommand(String data, int command) {
        int c1;
        int c2;
        int c3;
        int dot;

        switch (command) {
            case 1:
                c1 = data.indexOf(',', 0);
                c2 = data.indexOf(',', c1 + 1);
                c3 = data.indexOf(',', c2 + 1);
                dot = data.indexOf('.', c3 + 1);

                String name = data.substring(c1 + 1, c2);
                String password = data.substring(c2 + 1, c3);
                int dbID = Integer.parseInt(data.substring(c3 + 1, dot));
            case 2:
            case 3:
            case 4:
                push("7");
                break;
            case 7:
                String data2 = data.substring(data.indexOf('&')+1);
                Log.d(LOG_TAG,"Data2 = " + data2);
                ArrayList<Room> roomsAL = new ArrayList<>();
                ArrayList<Task> tasksAL = new ArrayList<>();

                try {
                    JSONArray tasks = new JSONArray(data2);
                    int serverDbID;
                    int roomID;
                    String text, nameOfCreator;
                    int state;
                    String comments;
                    for (int i = 0; i < tasks.length(); ++i) {
                        JSONObject task = (JSONObject) tasks.get(i);
                        serverDbID = task.getInt("id");
                        roomID = task.getInt("fk");
                        text = task.getString("text");
                        nameOfCreator = task.getString("nameOC");
                        state = task.getInt("state");
                        comments = task.getString("comments");
                        Task newTask = new Task(serverDbID, roomID, text, nameOfCreator, state, comments);
                        tasksAL.add(newTask);
                    }

                    JSONArray rooms = new JSONArray(data.substring(1));
                    int serverDbIDr;
                    String nameR;
                    String passwordR;
                    for (int i = 0; i < rooms.length(); ++i) {
                        JSONObject room = (JSONObject) rooms.get(i);
                        serverDbIDr = room.getInt("id");
                        nameR = room.getString("name");
                        passwordR = room.getString("pass");
                        Room newRoom = new Room(nameR, passwordR, serverDbIDr);
                        for (Task task : tasksAL) {
                            if (task.getRoomID() == newRoom.getServerDbID()) {
                                newRoom.getTasks().add(task);
                            }
                        }
                        roomsAL.add(newRoom);
                    }

                    if(refreshWithoutNotify) {
                        refreshWithoutNotify(roomsAL);
                        refreshWithoutNotify = false;
                    }else{
                        GridViewAdapter.getInstance().refresh(roomsAL);
                    }
                } catch (JSONException e) {
                    Log.d(LOG_TAG, e.toString());
                }
                break;
        }

    }

    private void refreshWithoutNotify(ArrayList<Room> rooms) {
        ArrayListRooms.getInstance().clear();
        ArrayListRooms.getInstance().addAll(DBHelper.getInstance().refresh(rooms));
        Log.d(LOG_TAG,"method rwoutn alr" + ArrayListRooms.getInstance().get(0).getTasks());
    }

    public void setRefreshWithoutNotify(boolean refreshWithoutNotify) {
        this.refreshWithoutNotify = refreshWithoutNotify;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
