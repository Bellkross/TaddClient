package ua.bellkross.client.adapters;


import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ua.bellkross.client.R;

import static ua.bellkross.client.RoomsActivity.LOG_TAG;

public class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView tvText;
    private TextView tvNameOfCreator;

    public MyViewHolder(View itemView) {
        super(itemView);

        tvText = itemView.findViewById(R.id.tvText);
        tvNameOfCreator = itemView.findViewById(R.id.tvName);

    }

    public TextView getTvText() {
        return tvText;
    }

    public void setTvText(TextView tvText) {
        this.tvText = tvText;
    }

    public TextView getTvNameOfCreator() {
        return tvNameOfCreator;
    }

    public void setTvNameOfCreator(TextView tvNameOfCreator) {
        this.tvNameOfCreator = tvNameOfCreator;
    }

}
