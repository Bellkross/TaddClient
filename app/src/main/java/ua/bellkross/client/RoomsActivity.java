package ua.bellkross.client;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import ua.bellkross.client.database.DBHelper;

public class RoomsActivity extends AppCompatActivity {

    public static String LOG_TAG = "debug";

    private Toolbar toolbar;
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

    public void addRoom(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        ConstraintLayout constraintLayout = (ConstraintLayout)
                getLayoutInflater().inflate(R.layout.add_room_dialog, null);
        final EditText etName = constraintLayout.findViewById(R.id.etName);
        final EditText etPassword = constraintLayout.findViewById(R.id.etPassword);
        dialog.setView(constraintLayout);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = etName.getText().toString()
                        .replaceAll(",","").replaceAll(" ", ""),
                        password = etPassword.getText().toString()
                                .replaceAll(",","").replaceAll(" ", "");
                if(!name.isEmpty() && !password.isEmpty()) {
                    clientTask.push("1" + ',' + name + ',' + password + '.');
                }else {
                    String toastText = "Room wasn't created input pass & name !";
                    Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        //GVA refresh
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
                        toolbar.setTitle(name);
                        clientTask.setLogin(name);
                        clientTask.push(name);
                    }
                });
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        String name = "Unnamed";
                        clientTask.push(name);
                        clientTask.setLogin(name);
                    }
                });
                clientTask.push("8");
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
