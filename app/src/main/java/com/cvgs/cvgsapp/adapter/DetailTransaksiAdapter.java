package com.cvgs.cvgsapp.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.cvgs.cvgsapp.R;
import com.cvgs.cvgsapp.model.DetailPembayaranModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

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
            String[] options = {"Show Image","Print Receipt","Close"};
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
                            new MaterialAlertDialogBuilder(activity)
                                    .setTitle("Download Receipt")
                                    .setMessage("Are you sure to download this receipt?")
                                    .setNegativeButton("Camcel",((dialogInterfacel,is)->dialogInterfacel.cancel()))
                                    .setPositiveButton("Confirm",((dialogInterface1, i1) -> holder.download_receipt(dataModels.getId_transaksi()))).show();
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

        public void download_receipt(String id_transaksi){
            ProgressDialog pgb = new ProgressDialog(activity);
            pgb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pgb.setTitle("Download Receipt");
            pgb.setMax(100);

            pgb.setCancelable(false);
            pgb.show();
            UUID number = UUID.randomUUID();
            String randomId = number.toString().replace("-", "");
            AndroidNetworking.download(server+"/api/report/kwitansi.php",String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)),id_transaksi+"_"+randomId+".pdf")
                    .addQueryParameter("id_transaksi",id_transaksi)
                    .setTag("DownloadReceipt")
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
                            activity.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        }

                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            Toast.makeText(activity, anError.getMessage(), Toast.LENGTH_SHORT).show();
                            pgb.dismiss();
                        }
                    });
        }
    }
}
