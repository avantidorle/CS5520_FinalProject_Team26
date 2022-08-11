package edu.neu.madcourse.cs5520_finalproject_team26;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class NotificationService extends Service implements LocationListener {

    final static String ACTION = "NotifyServiceAction";
    final static int RQS_STOP_SERVICE = 1;


    private Geocoder geocoder;
    private double latitudeValue;
    private double longitudeValue;
    private List<Address> addresses;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private String address;
    private double distanceTravelled = 0.0f;


    NotifyServiceReceiver notifyServiceReceiver;

    @Override
    public void onCreate() {
        notifyServiceReceiver = new NotifyServiceReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getLocationChanges();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this, "My Notification");
        builder.setContentTitle("Answer this");
        builder.setSmallIcon(R.drawable.coin);
        builder.setContentText("There are new questions in this location");
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationService.this);
        managerCompat.notify(1, builder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    private void getLocationChanges() {
    }

    @Override
        public void onDestroy() {
            this.unregisterReceiver(notifyServiceReceiver);
            super.onDestroy();
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        Toast.makeText(getApplicationContext(), location.getLatitude() +
                " " + location.getLongitude(), Toast.LENGTH_SHORT).show();

    }
    public class NotifyServiceReceiver extends BroadcastReceiver {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                int rqs = arg1.getIntExtra("RQS", 0);
                if (rqs == RQS_STOP_SERVICE){
                    stopSelf();
                }
            }
        }
    }