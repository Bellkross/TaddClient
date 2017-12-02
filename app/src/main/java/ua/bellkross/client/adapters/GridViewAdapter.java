package ua.bellkross.client.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.bellkross.client.R;
import ua.bellkross.client.RoomsActivity;
import ua.bellkross.client.database.DBHelper;
import ua.bellkross.client.model.Room;

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private static GridViewAdapter instance;

    private GridViewAdapter(Context context) {
        mContext = context;
    }

    public static GridViewAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new GridViewAdapter(context);
        }
        return instance;
    }

    public static GridViewAdapter getInstance() {
        return instance;
    }

    @Override
    public int getCount() {
        return ArrayListRooms.getInstance().size();
    }

    @Override
    public Object getItem(int position) {
        return ArrayListRooms.getInstance().get(position);
    }

    @Override
    public long getItemId(int position) {
        return ArrayListRooms.getInstance().get(position).getServerDbID();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridview;
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gridview = inflater.inflate(R.layout.room_item, null);
        TextView tvID, tvName, tvNumber, tvCount;
        tvID = gridview.findViewById(R.id.tvID);
        tvName = gridview.findViewById(R.id.tvName);
        tvNumber = gridview.findViewById(R.id.tvNumber);
        tvCount = gridview.findViewById(R.id.tvCount);
        Room room = ArrayListRooms.getInstance().get(position);
        tvID.setText("" + room.getServerDbID());
        tvName.setText(room.getName());
        tvNumber.setText("" + room.getArrayListID());
        tvCount.setText("" + room.getTasks().size());
        return gridview;
    }

    public void add(Room room) {
        room.setDbID(DBHelper.getInstance().addRoom(room));
//        Room room = new Room("Room" + ArrayListRooms.getInstance().size(), "111");
//        room.setDbID(0);
//        room.setServerDbID(0);
//        room.setArrayListID(ArrayListRooms.getInstance().size());
//        ArrayListRooms.getInstance().add(room);
        notifyDataSetChanged();
    }

    public void refresh(ArrayList<Room> rooms) {
        ArrayListRooms.getInstance().clear();
        ArrayListRooms.getInstance().addAll(DBHelper.getInstance().refresh(rooms));
        RoomsActivity.getGridView().post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

}
