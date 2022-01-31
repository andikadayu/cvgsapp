package com.cvgs.cvgsapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.cvgs.cvgsapp.adapter.OpenAdapter;
import com.cvgs.cvgsapp.model.OpenModel;

import java.util.ArrayList;

public class OpenSourceActivity extends AppCompatActivity {

    ListView lvOpenSource;
    ArrayAdapter<OpenModel> adapter;
    ArrayList<OpenModel> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_source);

        lvOpenSource = findViewById(R.id.lvOpenSource);

        dataList = new ArrayList<OpenModel>();

        initialize();
    }

    private void initialize() {
        dataList.add(new OpenModel("Android Open Source Project", "Copyright 2009-2012 The Android Open Source Project", "Apache-2.0 License"));
        dataList.add(new OpenModel("Flaticon", "https://www.freepikcompany.com/legal", "designed by " + getString(R.string.company_name) + " from Flaticon"));
        dataList.add(new OpenModel("android-networking:1.0.2", "https://github.com/amitshekhariitbhu/Fast-Android-Networking", "Apache-2.0 License"));
        dataList.add(new OpenModel("circleimageview:3.1.0", "https://github.com/hdodenhof/CircleImageView", "Apache-2.0 License"));
        dataList.add(new OpenModel("picasso:2.71828", "https://github.com/square/picasso", "Apache-2.0 License"));
        dataList.add(new OpenModel("ImageSlideshow:0.0.6", "https://github.com/denzcoskun/ImageSlideshow", "Apache-2.0 License"));
        dataList.add(new OpenModel("ExoPlayer:2.16.1", "https://github.com/google/ExoPlayer", "Apache-2.0 License"));
        dataList.add(new OpenModel("PhotoView:2.3.0", "https://github.com/Baseflow/PhotoView", "Apache-2.0 License"));
        adapter = new OpenAdapter(OpenSourceActivity.this, dataList);

        lvOpenSource.setAdapter(adapter);
    }
}