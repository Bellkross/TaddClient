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
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            resend = new Resender();
            resend.start();
            push(login);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void push(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                out.printf(message);
            }
        }).start();
        Log.d(LOG_TAG, "message pushed");
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
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
