package com.cvgs.cvgsapp.advances;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cvgs.cvgsapp.NotificationActivity;
import com.cvgs.cvgsapp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationReceiver extends Service {

    private static int count = 100;
    SessionManager sessionManager;
    Constance constance = new Constance();
    String role, id_detail;

    private void getNotif() {
        AndroidNetworking.post(constance.server + "/api/notification/getNotification.php")
                .addBodyParameter("id_detail", id_detail)
                .addBodyParameter("role", role)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray ja = response.getJSONArray("data");
                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    create_notif(jo.getString("description"));
                                }
                            }
                            getNotif();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }


    private void create_notif(@NonNull String description) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), constance.channel_notification_id);
        builder.setSmallIcon(R.mipmap.ic_cvgs);
        builder.setContentTitle("Information");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText(description);
        builder.setAutoCancel(true);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(constance.channel_notification_id) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(constance.channel_notification_id, "Notification Receiver", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Notification Update Receiver");
                notificationChannel.setShowBadge(true);
                notificationChannel.setImportance(NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        assert notificationManager != null;
        notificationManager.notify(count, builder.build());
        count++;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not Yet Implemented");
    }


    @Override
    public void onCreate() {
        sessionManager = new SessionManager(this);
        role = sessionManager.getUserDetail().get(SessionManager.ROLE);
        id_detail = sessionManager.getUserDetail().get(SessionManager.ID_DETAIL);
        AndroidNetworking.initialize(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getNotif();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
