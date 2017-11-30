package ua.bellkross.client;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

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
            Log.d(LOG_TAG, "Connected to socket...");
            InetAddress ipAddress = InetAddress.getByName(IP);

            this.socket = new Socket(ipAddress, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            resend = new Resender();
            resend.start();
            Log.d(LOG_TAG, "resender started");
            out = new PrintWriter(socket.getOutputStream(), true);
            push(login);
        } catch (IOException e) {
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

    @Override
    protected void onProgressUpdate(Void... voids) {
    }

    @Override
    protected void onPostExecute(Void unused) {

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

    private class Resender extends Thread {

        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        public void run() {
            try {
                while (!stoped) {
                    final String str = in.readLine();
                    Log.d(LOG_TAG, str);
                    int command = Integer.parseInt(str.substring(0,1));
                    int comma1 = str.indexOf(',',0);
                    int comma2 = str.indexOf(',',comma1+1);
                    int comma3 = str.indexOf(',',comma2+1);
                    int dot = str.indexOf('.',comma3+1);
                    Log.d(LOG_TAG, "command = " + command + ", c1 = " + comma1 +
                            " c2 = " + comma2 + " c3 = " + comma3 + " dot = " + dot);
                    readCommand(str, command,comma1,comma2,comma3,dot);
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }

    private void readCommand(String data, int command, int c1, int c2, int c3, int dot){
        switch (command){
            case 1:
                String name = data.substring(c1+1,c2);
                String password = data.substring(c2+1,c3);
                String dbID = data.substring(c3+1,dot);
                Log.d(LOG_TAG, "name = " + name + " pass = " + password + " dbID = " + dbID);
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
