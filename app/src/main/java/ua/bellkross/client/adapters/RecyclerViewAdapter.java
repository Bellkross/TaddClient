package ua.bellkross.client.adapters;


import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ua.bellkross.client.R;
import ua.bellkross.client.RoomsActivity;
import ua.bellkross.client.TasksActivity;
import ua.bellkross.client.database.DBHelper;
import ua.bellkross.client.model.Room;
import ua.bellkross.client.model.Task;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private ArrayList<Task> tasks;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;
    private LayoutInflater layoutInflater;

    public RecyclerViewAdapter(Context context, ArrayList<Task> tasks,
                               View.OnClickListener onClickListener,
                               View.OnLongClickListener onLongClickListener) {
        this.tasks = tasks;
        this.onClickListener = onClickListener;
        this.layoutInflater = LayoutInflater.from(context);
        this.onLongClickListener = onLongClickListener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.task_item, parent, false);
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.getTvText().setText(task.getText());
        if(task.getState()==1)
        holder.getTvText().setPaintFlags(holder.getTvText().getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.getTvNameOfCreator().setText(task.getNameOfCreator());

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addTask(Task task){

    }

}
