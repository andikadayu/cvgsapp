package com.cvgs.cvgsapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cvgs.cvgsapp.PendaftarDetailActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.PendaftarModel;

import java.util.ArrayList;

public class PendaftarAdapter extends RecyclerView.Adapter<PendaftarAdapter.Holder> {
    private Activity activity;
    private ArrayList<PendaftarModel> dataModel;

    public PendaftarAdapter(Activity activity, ArrayList<PendaftarModel> dataModel) {
        this.activity = activity;
        this.dataModel = dataModel;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pendaftar_adapter, parent, false);
        return new Holder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        PendaftarModel dataModels = dataModel.get(position);

        holder.tvPNama.setText(dataModels.getNama());
        holder.tvPContact.setText(dataModels.getNo_telp() + " / " + dataModels.getEmail());
        holder.tvPAlamat.setText(dataModels.getAlamat());

        holder.forClick.setOnClickListener(view -> {
            Intent sendData = new Intent(activity, PendaftarDetailActivity.class);
            sendData.putExtra("id_detail", dataModels.getId_detail());
            sendData.putExtra("nama", dataModels.getNama());
            sendData.putExtra("detail", dataModels.getNo_telp() + " / " + dataModels.getEmail());
            sendData.putExtra("alamat", dataModels.getAlamat());
            sendData.putExtra("email", dataModels.getEmail());
            activity.startActivity(sendData);
        });

        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }


    public class Holder extends RecyclerView.ViewHolder {
        PendaftarModel model;
        TextView tvPNama, tvPContact, tvPAlamat;
        RelativeLayout forClick;

        public Holder(@NonNull View v) {
            super(v);
            tvPNama = v.findViewById(R.id.tvPNama);
            tvPContact = v.findViewById(R.id.tvPContact);
            tvPAlamat = v.findViewById(R.id.tvPAlamat);
            forClick = v.findViewById(R.id.forClick);
        }
    }
}
