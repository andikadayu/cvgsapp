package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cvgs.cvgsapp.AdminProgressDetailActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.ProgressModel;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.Holder> {
    Activity activity;
    ArrayList<ProgressModel> dataModel;
    String server;

    public ProgressAdapter(Activity activity, ArrayList<ProgressModel> dataModel, String server) {
        this.activity = activity;
        this.dataModel = dataModel;
        this.server = server;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pembayaran_adapter, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        ProgressModel dataModels = dataModel.get(position);
        Picasso.get().load(server + dataModels.getLogo()).into(holder.imgBLoogo);


        String juduls = dataModels.getJudul();
        holder.tvBJudul.setText(juduls);
        holder.tvBDetail.setText(dataModels.getDetail());
        holder.tvBSisa.setText(dataModels.getProgress());

        holder.layoutOuter.setOnClickListener(view -> {
            Intent sendData = new Intent(activity, AdminProgressDetailActivity.class);
            sendData.putExtra("id_daftar", dataModels.getId_daftar());
            sendData.putExtra("judul", juduls);
            sendData.putExtra("detail", dataModels.getDetail());
            sendData.putExtra("logo", dataModels.getLogo());
            sendData.putExtra("progress", dataModels.getProgress());

            activity.startActivity(sendData);
        });

        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        ImageView imgBLoogo;
        TextView tvBJudul, tvBDetail, tvBSisa;
        ProgressModel model;
        RelativeLayout layoutOuter;

        public Holder(@NonNull @NotNull View v) {
            super(v);
            imgBLoogo = v.findViewById(R.id.imgBLoogo);
            tvBJudul = v.findViewById(R.id.tvBJudul);
            tvBDetail = v.findViewById(R.id.tvBDetail);
            tvBSisa = v.findViewById(R.id.tvBSisa);
            layoutOuter = v.findViewById(R.id.layoutOuter);
        }
    }
}
