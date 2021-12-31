package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.ShowProgressActivity;
import com.cvgs.cvgsapp.model.DetailProgressModel;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DetailProgressAdapter extends RecyclerView.Adapter<DetailProgressAdapter.Holder> {

    Activity activity;
    ArrayList<DetailProgressModel> dataModel;
    String server;
    String logo;

    public DetailProgressAdapter(Activity activity, ArrayList<DetailProgressModel> dataModel, String server, String logo) {
        this.activity = activity;
        this.dataModel = dataModel;
        this.server = server;
        this.logo = logo;
    }

    @NonNull
    @NotNull
    @Override
    public Holder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pembayaran_adapter,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull Holder holder, int position) {
        DetailProgressModel dataModels = dataModel.get(position);

        Picasso.get().load(server+logo).into(holder.imgBLoogo);

        holder.tvBJudul.setText(dataModels.getIsi_progress());
        holder.tvBDetail.setText(dataModels.getTgl_progress());
        holder.tvBSisa.setText(dataModels.getProgress());

        holder.tvBJudul.setMaxLines(1);
        holder.tvBJudul.setEllipsize(TextUtils.TruncateAt.END);

        holder.layoutOuter.setOnClickListener(view->{
            // TODO Something
            Intent sendData = new Intent(activity, ShowProgressActivity.class);
            sendData.putExtra("id_progress",dataModels.getId_progress());
            sendData.putExtra("progress",dataModels.getProgress());
            sendData.putExtra("isi_progress",dataModels.getIsi_progress());
            sendData.putExtra("tgl_progress",dataModels.getTgl_progress());
            sendData.putExtra("screenshot",dataModels.getScreenshot());
            sendData.putExtra("video",dataModels.getVideo());
            activity.startActivity(sendData);
        });

        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        DetailProgressModel model;
        ImageView imgBLoogo;
        TextView tvBJudul, tvBDetail, tvBSisa;
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
