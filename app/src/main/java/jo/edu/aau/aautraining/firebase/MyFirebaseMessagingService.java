package jo.edu.aau.aautraining.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import jo.edu.aau.aautraining.LoginActivity;
import jo.edu.aau.aautraining.R;
import jo.edu.aau.aautraining.shared.AppConstants;
import jo.edu.aau.aautraining.shared.MySharedPreference;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String CHANNEL_ID;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            Log.i("FirebaseNotification", remoteMessage.getData().toString());
            CHANNEL_ID = getString(R.string.default_notification_channel_id);
            NotificationCompat.Builder mBuilder;
            Context context = getApplicationContext();
            mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            String msg = remoteMessage.getData().get("msg");
            mBuilder.setSmallIcon(R.drawable.ic_stat_ic_notification)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setOnlyAlertOnce(true)
                    .setContentText(msg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);

            }
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            Random random = new Random();
            notificationManager.notify(random.nextInt(10000), notification);

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        MySharedPreference mySharedPreference = new MySharedPreference(getApplicationContext());
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                AppConstants.UPDATE_FIREBASE_TOKEN,
                null, null
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("firebase_token", token);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String token = mySharedPreference.getToken();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
