package com.hanira.gdele;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;


public class MainActivity extends AppCompatActivity {
    String links, inputedLink;
    boolean downloading;
    private boolean updating = false;
    public Button quee, fetch, paste, mores;
    EditText input;
    ProgressBar progressbar;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String processId = "MyDlProcess", title;


    private final Function3<Float, Long, String, Unit> callback = (progress, o2, line) -> {
        runOnUiThread(() -> {
                    if (progress < 0) {
                        progressbar.setVisibility(View.GONE);
                    }

                }
        );
        return Unit.INSTANCE;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AdView mAdView;
        input = findViewById(R.id.url);
        fetch = findViewById(R.id.fetch);
        quee = findViewById(R.id.quee);
        paste = findViewById(R.id.paste);
        mores = findViewById(R.id.more);
        progressbar = findViewById(R.id.progressBar2);


        progressbar.setVisibility(View.GONE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            downloaNotification();
        }
        fetch.setOnClickListener(view -> {
            inputedLink = String.valueOf(input.getText());
            if (inputedLink.contains("http://") || inputedLink.contains("https://")) {
                links = input.getText().toString();
                if (inputedLink.contains("youtu")) {
                    showAlart();
                } else {
                    startDownload();
                    progressbar.setVisibility(view.VISIBLE);
                }

            }


        });



        paste.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                ClipDescription description = clipboard.getPrimaryClipDescription();
                ClipData data = clipboard.getPrimaryClip();
                if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    input.setText(data.getItemAt(0).getText());
            } else {
                Toast.makeText(this, "Please copy a link first", Toast.LENGTH_SHORT).show();
            }
        });

        mores.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, more.class)));


        quee.setOnClickListener(v -> getInput());

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


















    private void startDownload() {
        if (downloading) {
            Toast.makeText(this, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }


        if (TextUtils.isEmpty(links)) {
            input.setError(getString(R.string.url_error));
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(links);
        File youtubeDLDir = getDownloadLocation();
        File config = new File(youtubeDLDir, "config.txt");

        if (config.exists()) {
            request.addOption("--config-location", config.getAbsolutePath());
        } else {
            title = "/%(title)s.%(ext)s";
            request.addOption("--no-mtime");
            request.addOption("--downloader", "libaria2c.so");
            request.addOption("--external-downloader-args", "aria2c:\"--summary-interval=1\"");
            request.addOption("-f", "bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best");
            request.addOption("-o", youtubeDLDir.getAbsolutePath() + "/%(title)s.%(ext)s");

        }

        showStart();

        downloading = true;
        Disposable disposable = Observable.fromCallable(() -> YoutubeDL.getInstance().execute(request, processId, callback))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(youtubeDLResponse -> {
                    Toast.makeText(this, "download successful", Toast.LENGTH_LONG).show();
                    downloading = false;
                    checkQueue();
                }, e -> {
                    Toast.makeText(this, "download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }




    private void checkQueue(){
        if (linkList.size()>0){
            links = linkList.get(0);
            linkList.remove(0);
            startDownload();
        }
    }




    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }










    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, String.valueOf(R.string.app_name));
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }










    private void showStart() {

    }
















    ArrayList<String> linkList = new ArrayList<>();

    private void getInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Link to download ");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialogue, null);
        final EditText input = viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            inputedLink = input.getText().toString();
            if (inputedLink.contains("youtu")) {
                showAlart();
            } else {
                linkList.add(links);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }
















    private void showAlart() {
        new AlertDialog.Builder(this)
                .setTitle("Unsuppurted link")
                .setMessage("You can't download content from youtube")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }















    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void downloaNotification() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("myCh", "My channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "notifyDownload")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            Notification notification = builder.build();
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
                return;
            }
            notificationManagerCompat.notify(1, notification);
        }


    }


}


