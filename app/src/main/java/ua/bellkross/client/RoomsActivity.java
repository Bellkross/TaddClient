package ua.bellkross.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import ua.bellkross.client.database.DBHelper;

public class RoomsActivity extends AppCompatActivity {

    public static String LOG_TAG = "debug";

    private Toolbar toolbar;
    private static String name = "Unnamed";
    private GridView gridView;
    private ClientTask clientTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        DBHelper.getInstance(this.getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        GridViewAdapter.getInstance(this);
        gridView = findViewById(R.id.gvRooms);
        gridView.setAdapter(GridViewAdapter.getInstance());

        clientTask = new ClientTask(toolbar.getTitle().toString());
        clientTask.execute();
    }

    public void addRoom(View view){
        clientTask.push("message");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_name:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                final EditText etName = new EditText(this);
                etName.setHint("Input ur name");
                dialog.setView(etName);
                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etName.getText().toString();
                        RoomsActivity.name = name;
                        toolbar.setTitle(name);
                        clientTask.setLogin(name);
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rooms, menu);
        return true;
    }

}
