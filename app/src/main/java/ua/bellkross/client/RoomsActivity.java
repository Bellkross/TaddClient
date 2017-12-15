package ua.bellkross.client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import ua.bellkross.client.adapters.ArrayListRooms;
import ua.bellkross.client.adapters.GridViewAdapter;
import ua.bellkross.client.database.DBHelper;

public class RoomsActivity extends AppCompatActivity {

    public static String LOG_TAG = "debug";

    private Toolbar toolbar;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private static GridView gridView;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        DBHelper.getInstance(this.getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gridView = findViewById(R.id.gvRooms);


        gridView.setAdapter(GridViewAdapter.getInstance(this));

        runLayoutAnimation(gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d(LOG_TAG, "" + position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(RoomsActivity.this);
                ConstraintLayout constraintLayout = (ConstraintLayout)
                        getLayoutInflater().inflate(R.layout.password_dialog, null);
                final EditText etInpPass = constraintLayout.findViewById(R.id.etInpPass);
                dialog.setView(constraintLayout);
                dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ipassword = etInpPass.getText().toString();
                        String password = ArrayListRooms.getInstance().get(position).getPassword();
                        if (ipassword.equals(password)) {
                            Intent intent = new Intent(RoomsActivity.this, TasksActivity.class);
                            intent.putExtra("RoomID", position);
                            startActivity(intent);
                        } else {
                            String toastText = "wrong password";
                            Toast.makeText(RoomsActivity.this, toastText, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.show();
            }
        });
        ClientTask.getInstance(toolbar.getTitle().toString());
        ClientTask.getInstance().execute();

    }


    private void runLayoutAnimation(final GridView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    public void addRoom(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        ConstraintLayout constraintLayout = (ConstraintLayout)
                getLayoutInflater().inflate(R.layout.add_room_dialog, null);
        final EditText etName = constraintLayout.findViewById(R.id.tvName);
        final EditText etPassword = constraintLayout.findViewById(R.id.etPassword);
        dialog.setView(constraintLayout);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString()
                        .replaceAll(",", "").replaceAll(" ", ""),
                        password = etPassword.getText().toString()
                                .replaceAll(",", "").replaceAll(" ", "");
                if (!name.isEmpty() && !password.isEmpty()) {
                    ClientTask.getInstance().push("1" + ',' + name + ',' + password + '.');
                } else {
                    String toastText = "Room wasn't created input pass & name !";
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        item.setIcon(Drawable.createFromPath("@drawable/set"));
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
                        toolbar.setTitle(name);
                        ClientTask.getInstance().setLogin(name);
                        ClientTask.getInstance().push(name);
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        String name = "Unnamed";
                        ClientTask.getInstance().push(name);
                        ClientTask.getInstance().setLogin(name);
                    }
                });
                ClientTask.getInstance().push("0");
                dialog.show();
                break;
            case R.id.action_refresh_rooms:
                ClientTask.getInstance().push("7");
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

    public static GridView getGridView() {
        return gridView;
    }


}

