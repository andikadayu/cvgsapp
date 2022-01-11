package com.cvgs.cvgsapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.advances.Constance;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailProjectActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvNama, tvTelp, tvEmail, tvAlamat, tvJudul, tvPaket, tvTool, tvTgl, tvHarga, tvJangka, tvTraning, tvGaransi;
    CardView cardProfile, cardProject, cardPenjelasan, cardLainnya;
    Button btnSkema;

    Constance constance = new Constance();
    String id_daftar;

    String nama,no_telp,email,alamat,judul,nama_paket,tool,penjelasan,tgl_daftar,jangka_waktu,training,garansi,harga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_project);

        btnBack = findViewById(R.id.btnBack);
        tvNama = findViewById(R.id.tvNama);
        tvTelp = findViewById(R.id.tvTelp);
        tvEmail = findViewById(R.id.tvEmail);
        tvAlamat = findViewById(R.id.tvAlamat);
        tvJudul = findViewById(R.id.tvJudul);
        tvPaket = findViewById(R.id.tvPaket);
        tvTool = findViewById(R.id.tvTool);
        tvTgl = findViewById(R.id.tvTgl);
        tvHarga = findViewById(R.id.tvHarga);
        tvJangka = findViewById(R.id.tvJangka);
        tvTraning = findViewById(R.id.tvTraning);
        tvGaransi = findViewById(R.id.tvGaransi);
        cardProfile = findViewById(R.id.cardProfile);
        cardProject = findViewById(R.id.cardProject);
        cardPenjelasan = findViewById(R.id.cardPenjelasan);
        cardLainnya = findViewById(R.id.cardLainnya);
        btnSkema = findViewById(R.id.btnSkema);

        AndroidNetworking.initialize(getApplicationContext());

        Intent currentIntent = getIntent();
        if(currentIntent.hasExtra("id_daftar")){
            id_daftar = currentIntent.getStringExtra("id_daftar");
            getAllData(this);
        }else{
            finish();
        }


        btnBack.setOnClickListener(view->finish());

        btnSkema.setOnClickListener(view -> {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Download Skema")
                    .setMessage("Do you want download skema")
                    .setNegativeButton("Cancel",(dialog,i)->dialog.cancel())
                    .setPositiveButton("Confirm",(dialog,i)->downloadSkema(this)).show();
        });

    }

    private void getAllData(Activity activity){
        AndroidNetworking.post(constance.server+"/api/progress/getDetailProject.php")
                .addBodyParameter("id_daftar",id_daftar)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            nama = response.getString("nama");
                            no_telp = response.getString("no_telp");
                            email = response.getString("email");
                            alamat = response.getString("alamat");
                            judul = response.getString("judul");
                            nama_paket = response.getString("nama_paket");
                            tool = response.getString("tool");
                            penjelasan = response.getString("penjelasan");
                            tgl_daftar = response.getString("tgl_daftar");
                            harga = response.getString("harga");

                            initializeData();

                            boolean has_other = response.getBoolean("has_other");
                            if(has_other){
                                initializeOther();
                                jangka_waktu = response.getString("jangka_waktu");
                                training = response.getString("training");
                                garansi = response.getString("garansi");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "ERROR RESPONSES", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(activity, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void initializeData(){
        tvNama.setText(nama);
        tvTelp.setText(no_telp);
        tvEmail.setText(email);
        tvAlamat.setText(alamat);
        tvJudul.setText(judul);
        tvPaket.setText(nama_paket);
        tvTool.setText(tool);
        tvTgl.setText(tgl_daftar);
        tvHarga.setText(harga);
        btnSkema.setText(penjelasan);
    }

    private void initializeOther(){
        cardLainnya.setVisibility(View.VISIBLE);
        tvJangka.setText(jangka_waktu);
        tvTraning.setText(training);
        tvGaransi.setText(garansi);
    }

    private void downloadSkema(Activity activity){
        ProgressDialog pgb = new ProgressDialog(activity);
        pgb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pgb.setTitle("Download Skema");
        pgb.setMax(100);

        pgb.setCancelable(false);
        pgb.show();
        AndroidNetworking.download(constance.server+"/api/register/assets/"+penjelasan, String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)),penjelasan)
                .setTag("DownloadSkema")
                .setPriority(Priority.MEDIUM)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {
                        double progress = (100.0 * bytesDownloaded) / totalBytes;

                        pgb.setProgress((int) progress);
                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        pgb.dismiss();
                        Toast.makeText(activity, "Download Complete", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(activity, "Can't Download Skema", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}