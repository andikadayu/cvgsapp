package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cvgs.cvgsapp.DetailNotificationActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.NotificationModel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.Holder>{
    Activity activity;
    ArrayList<NotificationModel> dataModel;

    public NotificationAdapter(Activity activity, ArrayList<NotificationModel> dataModel) {
        this.activity = activity;
        this.dataModel = dataModel;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adapter,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        NotificationModel dataModels = dataModel.get(position);

        holder.tvDescription.setText(dataModels.getDescription());
        holder.tvTgl.setText(dataModels.getDate_notify());

        if(dataModels.is_read()){
            holder.layoutOuter.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
        }else{
            holder.layoutOuter.setBackgroundColor(Color.parseColor("#D6E6F2"));
        }

        holder.layoutOuter.setOnClickListener(view->{
            Intent sendData = new Intent(activity, DetailNotificationActivity.class);
            sendData.putExtra("id_notification",dataModels.getId_notification());
            sendData.putExtra("date_notify",dataModels.getDatetime_notify());
            sendData.putExtra("detail",dataModels.getDetails());
            activity.startActivity(sendData);
        });

        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        NotificationModel model;
        RelativeLayout layoutOuter;
        TextView tvDescription,tvTgl;


        public Holder(@NonNull @NotNull View v) {
            super(v);

            layoutOuter = v.findViewById(R.id.layoutOuter);
            tvDescription = v.findViewById(R.id.tvDescription);
            tvTgl = v.findViewById(R.id.tvTgl);

        }
    }

}
