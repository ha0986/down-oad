package com.hanif.gdele;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Telephony;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class start extends AppCompatActivity {

    Button facebooks, whatsapps, instagrams, twitters, vimeos, vks, mores, rate, twitchs;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mores = findViewById(R.id.more);
        rate = findViewById(R.id.rate);
        facebooks = findViewById(R.id.facebook);
        whatsapps = findViewById(R.id.whatsapp);
        instagrams = findViewById(R.id.instagram);
        twitchs = findViewById(R.id.twitch);
        twitters = findViewById(R.id.twitter);
        vimeos = findViewById(R.id.vimeo);
        vks = findViewById(R.id.vk);





        mores.setOnClickListener(view -> startActivity(new Intent(start.this, more.class)));
        facebooks.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        whatsapps.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        instagrams.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        twitters.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        twitchs.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        vimeos.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        vks.setOnClickListener(view -> startActivity(new Intent(start.this, MainActivity.class)));
        rate.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.gdele"))));

    }

    @Override
    public void onBackPressed() {

    }


    public void isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel("myCh", "My Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "myCh")
                .setSmallIcon(R.drawable.baseline_download_24)
                .setContentTitle("textTitle")
                .setContentText("textContent")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
        notificationManager.notify(1, builder.build());
    }










}


