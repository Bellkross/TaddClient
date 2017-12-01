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
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ua.bellkross.client.model.Room;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class ClientTask extends AsyncTask<Void, Void, Void> {

    public static final String IP = "192.168.0.102";
    public static final int PORT = 5000;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Resender resend;
    private String login;

    public ClientTask(String login) {
        this.login = login;
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

    private class Resender extends AsyncTask<Void,Void,Void> {

        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                while (!stoped) {
                    final String str = in.readLine();
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
                Log.d(LOG_TAG, "name = " + name + " pass = " + password + " dbID = " + dbID);
                push("7");
                break;
            case 7:
                Log.d(LOG_TAG, "rooms = " + data.substring(1));
                String data2 = "";
                try {
                    data2 = in.readLine();
                } catch (IOException e) {
                    Log.d(LOG_TAG, e.toString());
                }
                Log.d(LOG_TAG, "tasks = " + data2);

                ArrayList<Room> roomsAL = new ArrayList<>();
                ArrayList<Task> tasksAL = new ArrayList<>();

                try {
                    JSONArray tasks = new JSONArray(data2);
                    int serverDbID;
                    int roomID;
                    String text, nameOfCreator;
                    int state;
                    Date deadline=null;
                    String comments;
                    for (int i = 0; i < tasks.length(); ++i) {
                        JSONObject task = (JSONObject) tasks.get(i);
                        Log.d(LOG_TAG, task + "");
                        serverDbID = task.getInt("id");
                        roomID = task.getInt("fk");
                        text = task.getString("text");
                        nameOfCreator = task.getString("nameOC");
                        state = task.getInt("state");
                        try {
                            String dateStr = task.getString("deadline");
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date dlDate = sdf.parse(dateStr);
                            deadline = dlDate;
                        } catch (ParseException e) {
                            Log.d(LOG_TAG, e.toString());
                            e.printStackTrace();
                        }
                        comments = task.getString("comments");
                        Task newTask = new Task(serverDbID,roomID,text,nameOfCreator,state,deadline,comments);
                        Log.d(LOG_TAG, "task = " + newTask.toString());
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
                        Log.d(LOG_TAG, room + "");
                        Room newRoom = new Room(nameR, passwordR, serverDbIDr);
                        for (Task task : tasksAL) {
                            if(task.getRoomID()==newRoom.getServerDbID()){
                                newRoom.getTasks().add(task);
                            }
                        }
                        Log.d(LOG_TAG, "new room = " + newRoom.toString());
                        roomsAL.add(newRoom);
                    }

                    GridViewAdapter.getInstance().refresh(roomsAL);
                } catch (JSONException e) {
                    Log.d(LOG_TAG,e.toString());
                }
                break;
        }

    }



    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
