package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.DetailPembayaranModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailTransaksiAdapter extends RecyclerView.Adapter<DetailTransaksiAdapter.Holder> {
    Activity activity;
    ArrayList<DetailPembayaranModel> dataModel;
    String server;

    public DetailTransaksiAdapter(Activity activity, ArrayList<DetailPembayaranModel> dataModel, String server) {
        this.activity = activity;
        this.dataModel = dataModel;
        this.server = server;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pembayaran_adapter,parent,false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        DetailPembayaranModel dataModels = dataModel.get(position);

        Picasso.get().load(server+dataModels.getImage()).into(holder.imgBLoogo);

        holder.tvBJudul.setText(dataModels.getNominal());
        holder.tvBDetail.setText(dataModels.getTgl_transaksi());
        holder.tvBSisa.setText(dataModels.getStatus());

        holder.layoutOuter.setOnClickListener(view->{
            String[] options = {"Show Image","Close"};
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("List Options")
                    .setItems(options,((dialogInterface, i) -> {
                        if(i == 0) {
                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = inflater.inflate(R.layout.custom_show_transaction, null);
                            final ImageView imgTrans;
                            imgTrans = v.findViewById(R.id.imgTrans);
                            Picasso.get().load(server + dataModels.getImage()).into(imgTrans);

                            new MaterialAlertDialogBuilder(activity)
                                    .setView(v)
                                    .setTitle("Detail Transaction")
                                    .setNegativeButton("Cancel", ((dialogInterface1, i1) -> dialogInterface1.cancel())).show();
                        }else if(i == 1){
                            dialogInterface.cancel();
                        }
                    })).show();
        });


        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        DetailPembayaranModel model;
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
