package ua.bellkross.client;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ua.bellkross.client.adapters.ArrayListRooms;
import ua.bellkross.client.adapters.RecyclerViewAdapter;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class TasksActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private EditText etInputTask;
    private int roomID;
    private Boolean isFabOpen = false;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        roomID = getIntent().getIntExtra("RoomID", -1);
        Log.d(LOG_TAG, "l = " + ArrayListRooms.getInstance().get(roomID).getTasks().size());

        toolbar = findViewById(R.id.toolbar_tasks);
        toolbar.setTitle(ArrayListRooms.getInstance().get(roomID).getName());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        adapter = new RecyclerViewAdapter(this, ArrayListRooms.getInstance().get(roomID).getTasks(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(v);
                        Task task = ArrayListRooms.getInstance().get(roomID).
                                getTasks().get(recyclerViewPosition);
                        Log.d(LOG_TAG, "before state = " + task.getState());
                        if (task.getState() == 1) {
                            task.setState(0);
                        } else if (task.getState() == 0) {
                            task.setState(1);
                        }
                        Log.d(LOG_TAG, "after state = " + task.getState());
                        String taskText = task.getText();
                        ClientTask.getInstance().push("3," + task.getServerDbID() + ".");
                        ClientTask.getInstance().setRefreshWithoutNotify(true);
                        adapter.refresh(roomID);
                    }
                },
                new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        int recyclerViewPosition = recyclerView.getChildAdapterPosition(v);
                        Task task = ArrayListRooms.getInstance().get(roomID).
                                getTasks().get(recyclerViewPosition);
                        deleteTask(task.getServerDbID());
                        Log.d(LOG_TAG, recyclerViewPosition + ":sop vr");
                        ClientTask.getInstance().setRefreshWithoutNotify(true);

                        adapter.refresh(roomID);
                        return true;
                    }
                });

        recyclerView = findViewById(R.id.recycler_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        runLayoutAnimation(recyclerView);

        etInputTask = findViewById(R.id.etInputTask);

        etInputTask.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                String task = etInputTask.getText().toString();
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && !task.equalsIgnoreCase("")) {
                    if (!task.contains(",")) {
                        addTask();
                        etInputTask.setText("");
                    } else {
                        etInputTask.setText("");
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public static RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void addTask() {
        String task = etInputTask.getText().toString();
        ClientTask.getInstance().setRefreshWithoutNotify(true);
        ClientTask.getInstance().push("2," +
                ArrayListRooms.getInstance().get(roomID).getServerDbID()
                + ',' + task + ',' + ClientTask.getInstance().getLogin() + '.');
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter.refresh(roomID);
    }

    public void deleteTask(int taskID) {


        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("Great job!");
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();



        ClientTask.getInstance().push("4," + taskID + ".");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishAndRemoveTask();
        } else {
            adapter.refresh(roomID);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tasks, menu);
        return super.onCreateOptionsMenu(menu);
    }



    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}
