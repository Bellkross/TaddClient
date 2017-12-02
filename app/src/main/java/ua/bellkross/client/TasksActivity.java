package ua.bellkross.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import ua.bellkross.client.model.ArrayListRooms;

public class TasksActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        int roomID = getIntent().getIntExtra("RoomID",-1);
        toolbar = findViewById(R.id.toolbar_tasks);
        toolbar.setTitle(ArrayListRooms.getInstance().get(roomID).getName());
        setSupportActionBar(toolbar);
    }
}
