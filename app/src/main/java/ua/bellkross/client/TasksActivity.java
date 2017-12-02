package ua.bellkross.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import ua.bellkross.client.adapters.ArrayListRooms;
import ua.bellkross.client.adapters.RecyclerViewAdapter;
import ua.bellkross.client.model.Room;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class TasksActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Room room;
    private ArrayList<Task> tasks;
    private static RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private EditText etInputTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        int roomID = getIntent().getIntExtra("RoomID",-1);
        room = ArrayListRooms.getInstance().get(roomID);
        tasks = room.getTasks();
        Log.d(LOG_TAG, "l = " + tasks.size());

        toolbar = findViewById(R.id.toolbar_tasks);
        toolbar.setTitle(room.getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        adapter = new RecyclerViewAdapter(this, tasks,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(v);
                        Log.d(LOG_TAG, recyclerViewPosition+"");
                    }
                },
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(v);
                        Log.d(LOG_TAG, recyclerViewPosition+"");
                        return true;
                    }
                });

        recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        etInputTask = findViewById(R.id.etInputTask);

        etInputTask.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                String task = etInputTask.getText().toString();
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && !task.equalsIgnoreCase("")) {
                    addTask();
                    etInputTask.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void addTask(){
        ClientTask.getInstance().setRefreshWithoutNotify(true);
        String task = etInputTask.getText().toString();
        ClientTask.getInstance().push("2,"+room.getServerDbID()+','+task+','+ClientTask.getInstance().getLogin()+
                ','+'0'+','+"no comments.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Task task;
        //adapter.addTask();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAndRemoveTask();
        }
        return super.onOptionsItemSelected(item);
    }
}
