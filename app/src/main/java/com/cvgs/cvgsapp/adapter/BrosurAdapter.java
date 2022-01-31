package com.cvgs.cvgsapp.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.SuperBrosur;
import com.cvgs.cvgsapp.UploadBrosurActivity;
import com.cvgs.cvgsapp.advances.Constance;
import com.cvgs.cvgsapp.model.BrosurModel;
import com.cvgs.cvgsapp.show_detail_brosur;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;

import java.util.ArrayList;

public class BrosurAdapter extends ArrayAdapter<BrosurModel> {
    ArrayList<BrosurModel> dataModel;
    ImageView imgBrosur;
    Constance constance = new Constance();
    String from;

    public BrosurAdapter(@NonNull Context context, ArrayList<BrosurModel> dataModel, String from) {
        super(context, 0, dataModel);
        this.from = from;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_view_brosur, parent, false);
        BrosurModel dataModels = (BrosurModel) getItem(position);

        imgBrosur = view.findViewById(R.id.imgCusBrosur);

        Picasso.get().load(constance.server + "/api/brosur/assets/brosur-" + dataModels.getId_brosur() + ".png").into(imgBrosur);

        imgBrosur.setOnClickListener(view1 -> {
            Intent intent = new Intent(getContext(), show_detail_brosur.class);
            intent.putExtra("id_brosur", dataModels.getId_brosur());
            getContext().startActivity(intent);
        });

        if (from.equals("super")) {
            imgBrosur.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    final CharSequence[] options = {"Update Brosur", "Delete Brosur", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Option Brosur");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (options[i].equals("Cancel")) {
                                dialogInterface.cancel();
                            } else if (options[i].equals("Update Brosur")) {
                                Intent sendData = new Intent(getContext(), UploadBrosurActivity.class);
                                sendData.putExtra("id_brosur", dataModels.getId_brosur());
                                getContext().startActivity(sendData);
                            } else {
                                new MaterialAlertDialogBuilder(getContext())
                                        .setTitle("Delete Brosur?")
                                        .setMessage("Are you sure to delete this?")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogs, int i) {
                                                dialogs.cancel();
                                            }
                                        })
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteBrosur(dataModels.getId_brosur());
                                            }
                                        }).show();

                                dialogInterface.cancel();
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }

        return view;
    }

    private void deleteBrosur(String id) {
        ProgressDialog pdg = new ProgressDialog(getContext());
        pdg.setCancelable(false);
        pdg.setMessage("Loading...");
        pdg.show();
        AndroidNetworking.post(constance.server + "/api/brosur/deleteBrosur.php")
                .addBodyParameter("id_brosur", id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            boolean status = response.getBoolean("status");
                            pdg.dismiss();
                            if (status) {
                                Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                                getContext().startActivity(new Intent(getContext(), SuperBrosur.class));
                            } else {
                                Toast.makeText(getContext(), "Delete Unsuccessful", Toast.LENGTH_SHORT).show();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            pdg.dismiss();
                            Toast.makeText(getContext(), "Error Response", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        pdg.dismiss();
                        Toast.makeText(getContext(), "Error Connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
