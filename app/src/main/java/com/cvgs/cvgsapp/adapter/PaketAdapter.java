package com.cvgs.cvgsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cvgs.cvgsapp.HomeActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.RegisterFormActivity;
import com.cvgs.cvgsapp.RegisterLoggedActivity;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.PaketModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PaketAdapter extends ArrayAdapter<PaketModel> {

    Constance constance = new Constance();
    String nama_layanan;
    boolean is_logged_in;
    String role;

    public PaketAdapter(@NonNull Context context, ArrayList<PaketModel> dataModel,String nama_layanan,boolean is_logged_in,String role) {
        super(context, 0,dataModel);
        this.nama_layanan = nama_layanan;
        this.is_logged_in = is_logged_in;
        this.role = role;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_grid_explore,parent,false);
        PaketModel dataModels = (PaketModel) getItem(position);

        ImageView menuImage;
        TextView MenuTitle;
        LinearLayout MenuOuter;

        menuImage = view.findViewById(R.id.menuImage);
        MenuTitle = view.findViewById(R.id.MenuTitle);
        MenuOuter = view.findViewById(R.id.MenuOuter);

        // TODO Set Image
        Picasso.get().load(constance.server+"/api/paket/assets/"+dataModels.getLogo_paket()).into(menuImage);

        // TODO set Title
        MenuTitle.setText(dataModels.getNama_paket());

        // Todo set Color
        MenuOuter.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(dataModels.getColor_paket())));

        menuImage.setOnClickListener(view1->{
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Konfirmasi "+dataModels.getNama_paket())
                    .setMessage(dataModels.getIsi_paket())
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!is_logged_in){
                                Intent kirimData = new Intent(getContext(), RegisterFormActivity.class);
                                kirimData.putExtra("nama_layanan",nama_layanan);
                                kirimData.putExtra("nama_paket",dataModels.getNama_paket());
                                kirimData.putExtra("id_paket",dataModels.getId_paket());
                                kirimData.putExtra("img_paket",constance.server+"/api/paket/assets/"+dataModels.getLogo_paket());
                                getContext().startActivity(kirimData);
                            }else{
                                if(role.equalsIgnoreCase("pendaftar")){
                                    Intent kirimData = new Intent(getContext(), RegisterLoggedActivity.class);
                                    kirimData.putExtra("nama_layanan",nama_layanan);
                                    kirimData.putExtra("nama_paket",dataModels.getNama_paket());
                                    kirimData.putExtra("id_paket",dataModels.getId_paket());
                                    kirimData.putExtra("img_paket",constance.server+"/api/paket/assets/"+dataModels.getLogo_paket());
                                    getContext().startActivity(kirimData);
                                }else{
                                    getContext().startActivity(new Intent(getContext(), HomeActivity.class));
                                }
                            }
                        }
                    })
                    .show();
        });

        return view;
    }
}
