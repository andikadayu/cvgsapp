package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.BrosurModel;
import com.cvgs.cvgsapp.show_detail_brosur;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeInfoAdapter extends RecyclerView.Adapter<HomeInfoAdapter.ViewHolder>{
    private ArrayList<BrosurModel> dataModel;
    private Activity activity;
    Constance constance = new Constance();

    public HomeInfoAdapter(ArrayList<BrosurModel> dataModel, Activity activity) {
        this.dataModel = dataModel;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HomeInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_view_brosur,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrosurModel model = dataModel.get(position);
        Picasso.get().load(constance.server+"/api/brosur/assets/brosur-"+model.getId_brosur()+".png").into(holder.imgBrosur);
        holder.imgBrosur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, show_detail_brosur.class);
                intent.putExtra("id_brosur",model.getId_brosur());
                activity.startActivity(intent);
            }
        });

        holder.model = model;
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBrosur;
        BrosurModel model;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBrosur = itemView.findViewById(R.id.imgCusBrosur);
        }
    }
}
