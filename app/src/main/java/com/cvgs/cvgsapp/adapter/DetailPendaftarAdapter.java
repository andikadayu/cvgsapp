package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.DialogInterface;
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
import com.cvgs.cvgsapp.DetailProjectActivity;
import com.cvgs.cvgsapp.PendaftarActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.DetailPendaftarModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailPendaftarAdapter extends RecyclerView.Adapter<DetailPendaftarAdapter.Holder>{

    Activity activity;
    ArrayList<DetailPendaftarModel> dataModel;
    String server;

    public DetailPendaftarAdapter(Activity activity, ArrayList<DetailPendaftarModel> dataModel,String server) {
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
        DetailPendaftarModel dataModels = dataModel.get(position);

        Picasso.get().load(server+dataModels.getLogo()).into(holder.imgBLoogo);

        holder.tvBJudul.setText(dataModels.getJudul());
        holder.tvBDetail.setText(dataModels.getTool());
        holder.tvBSisa.setText(dataModels.getStatus());

        String[] options= {"Detail Project","Confirm Project","Close"};

        holder.layoutOuter.setOnClickListener(view->{
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("List Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(i == 2){
                                dialogInterface.dismiss();
                            }else if(i == 1){
                                new MaterialAlertDialogBuilder(activity)
                                        .setTitle("Confirm Project")
                                        .setMessage("Are you sure confirm this project?")
                                        .setNegativeButton("Cancel",(dialog, position)->{
                                            dialog.dismiss();
                                        })
                                        .setPositiveButton("Confirm",(dialog,position)->{
                                            holder.setConfirmProject(dataModels.getId_daftar());
                                        }).show();
                            }else if(i == 0){
                                Intent sendData = new Intent(activity, DetailProjectActivity.class);
                                sendData.putExtra("id_daftar",dataModels.getId_daftar());
                                activity.startActivity(sendData);
                            }
                        }
                    }).show();
        });

        holder.model = dataModels;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public class Holder extends RecyclerView.ViewHolder{
        DetailPendaftarModel model;
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

        public void setConfirmProject(String id_daftar){
            AndroidNetworking.post(server+"/api/pendaftar/confirmProject.php")
                    .addBodyParameter("id_daftar",id_daftar)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try{
                                boolean status = response.getBoolean("status");
                                if(status){
                                    Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();
                                    activity.startActivity(new Intent(activity, PendaftarActivity.class));
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
