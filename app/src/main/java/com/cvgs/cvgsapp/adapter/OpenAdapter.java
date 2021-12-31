package com.cvgs.cvgsapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.OpenModel;

import java.util.ArrayList;

public class OpenAdapter extends ArrayAdapter<OpenModel> {

    ArrayList<OpenModel> dataModel;
    TextView openName,openLink,openLicense;

    public OpenAdapter(@NonNull Context context, ArrayList<OpenModel> dataModel) {
        super(context, 0, dataModel);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.detail_open_source,parent,false);
        OpenModel dataModels = (OpenModel) getItem(position);

        openName = view.findViewById(R.id.openName);
        openLink = view.findViewById(R.id.openLink);
        openLicense = view.findViewById(R.id.openLicense);

        openName.setText(dataModels.getNamePackage());
        openLink.setText(dataModels.getLinkPackage());
        openLicense.setText("This Package Licensed under "+dataModels.getLicensePackage());

        return view;
    }
}
