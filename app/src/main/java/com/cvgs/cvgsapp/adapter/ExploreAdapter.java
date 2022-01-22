package com.cvgs.cvgsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.cvgs.cvgsapp.PilihPaketActivity;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.ExploreModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ExploreAdapter extends ArrayAdapter<ExploreModel> {

    ImageView menuImage;
    TextView MenuTitle;
    LinearLayout MenuOuter;
    Constance constance = new Constance();


    public ExploreAdapter(@NonNull Context context, @NonNull ArrayList<ExploreModel> dataModel) {
        super(context, 0, dataModel);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_grid_explore,parent,false);
        ExploreModel dataModels = (ExploreModel) getItem(position);

        menuImage = view.findViewById(R.id.menuImage);
        MenuTitle = view.findViewById(R.id.MenuTitle);
        MenuOuter = view.findViewById(R.id.MenuOuter);

        // TODO Set Image
        Picasso.get().load(constance.server+"/api/explore/assets/"+dataModels.getImage_explore()).into(menuImage);

        // TODO set Title
        MenuTitle.setText(dataModels.getDetail_explore());

        // Todo set Color
        MenuOuter.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(dataModels.getColor_explore())));

        MenuOuter.setOnClickListener(view2->{
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Konfirmasi "+dataModels.getDetail_explore()+"?")
                    .setMessage(dataModels.getIsi_explore())
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent kirimData = new Intent(getContext(), PilihPaketActivity.class);
                            kirimData.putExtra("id_layanan",dataModels.getId_explore());
                            kirimData.putExtra("nama_layanan",dataModels.getDetail_explore());
                            getContext().startActivity(kirimData);
                        }
                    })
                    .show();
        });

        return view;
    }
}
