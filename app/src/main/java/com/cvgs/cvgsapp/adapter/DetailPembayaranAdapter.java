package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.PembayaranActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.DetailPembayaranModel;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailPembayaranAdapter extends RecyclerView.Adapter<DetailPembayaranAdapter.Holder>{
    Activity activity;
    ArrayList<DetailPembayaranModel> dataModel;
    String server;

    public DetailPembayaranAdapter(Activity activity, ArrayList<DetailPembayaranModel> dataModel,String server) {
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
            String[] options = {"Show Image","Confirm Transaction","Close"};
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("List Options")
                    .setItems(options,((dialogInterface, i) -> {
                        if(i == 0){
                            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = inflater.inflate(R.layout.custom_show_transaction,null);
                            final PhotoView imgTrans;
                            imgTrans = v.findViewById(R.id.imgTrans);
                            Picasso.get().load(server+dataModels.getImage()).into(imgTrans);

                            new MaterialAlertDialogBuilder(activity)
                                    .setView(v)
                                    .setTitle("Detail Transaction")
                                    .setNegativeButton("Cancel",((dialogInterface1, i1) -> dialogInterface1.cancel())).show();
                        }else if(i == 1){
                            new MaterialAlertDialogBuilder(activity)
                                    .setTitle("Confirm Transaction")
                                    .setMessage("Are you sure to confirm this transaction?")
                                    .setNegativeButton("Cancel",((dialogInterface1, i1) -> dialogInterface1.cancel()))
                                    .setPositiveButton("Confirm",((dialogInterface1, i1) -> {
                                        holder.confirm_transaction(dataModels.getId_transaksi());
                                    })).show();
                        }else if(i == 2){
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

    public class Holder extends RecyclerView.ViewHolder{
        DetailPembayaranModel model;
        ImageView imgBLoogo;
        TextView tvBJudul,tvBDetail,tvBSisa;
        RelativeLayout layoutOuter;

        public Holder(@NonNull View v) {
            super(v);

            imgBLoogo = v.findViewById(R.id.imgBLoogo);
            tvBJudul = v.findViewById(R.id.tvBJudul);
            tvBDetail = v.findViewById(R.id.tvBDetail);
            tvBSisa = v.findViewById(R.id.tvBSisa);
            layoutOuter = v.findViewById(R.id.layoutOuter);
        }

        public void confirm_transaction(String id_transaksi){
            AndroidNetworking.post(server+"/api/pembayaran/confirmTransaction.php")
                    .addBodyParameter("id_transaksi",id_transaksi)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");
                                if(status){
                                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                                    activity.startActivity(new Intent(activity, PembayaranActivity.class));
                                    activity.finish();
                                }else{
                                    Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show();
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                                Toast.makeText(activity, "ERROR RESPONSE", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
