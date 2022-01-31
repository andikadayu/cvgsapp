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
import com.cvgs.cvgsapp.PembayaranDetailActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.TransaksiDetailActivity;
import com.cvgs.cvgsapp.model.PembayaranModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PembayaranAdapter extends RecyclerView.Adapter<PembayaranAdapter.Holder> {
    String server;
    String role;
    private Activity activity;
    private ArrayList<PembayaranModel> dataModel;

    public PembayaranAdapter(Activity activity, ArrayList<PembayaranModel> dataModel, String server, String role) {
        this.activity = activity;
        this.dataModel = dataModel;
        this.server = server;
        this.role = role;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pembayaran_adapter, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PembayaranModel dataModels = dataModel.get(position);

        Picasso.get().load(server + dataModels.getLogo()).into(holder.imgBLoogo);

        holder.tvBJudul.setText(dataModels.getJudul());
        holder.tvBDetail.setText(dataModels.getDetail());
        holder.tvBSisa.setText(dataModels.getSisa());


        holder.layoutOuter.setOnClickListener(view -> {
            if (role.equalsIgnoreCase("pendaftar")) {
                Intent sendData = new Intent(activity, TransaksiDetailActivity.class);
                sendData.putExtra("id_daftar", dataModels.getId_daftar());
                sendData.putExtra("judul", dataModels.getJudul());
                sendData.putExtra("detail", dataModels.getDetail());
                sendData.putExtra("logo", dataModels.getLogo());
                sendData.putExtra("sisa", dataModels.getSisa());
                activity.startActivity(sendData);
            } else {
                Intent sendData = new Intent(activity, PembayaranDetailActivity.class);
                sendData.putExtra("id_daftar", dataModels.getId_daftar());
                sendData.putExtra("judul", dataModels.getJudul());
                sendData.putExtra("detail", dataModels.getDetail());
                sendData.putExtra("logo", dataModels.getLogo());
                sendData.putExtra("sisa", dataModels.getSisa());
                activity.startActivity(sendData);
            }


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
        PembayaranModel model;
        RelativeLayout layoutOuter;

        public Holder(@NonNull View v) {
            super(v);
            imgBLoogo = v.findViewById(R.id.imgBLoogo);
            tvBJudul = v.findViewById(R.id.tvBJudul);
            tvBDetail = v.findViewById(R.id.tvBDetail);
            tvBSisa = v.findViewById(R.id.tvBSisa);
            layoutOuter = v.findViewById(R.id.layoutOuter);
        }
    }
}
