package com.hanira.gdele;


import android.Manifest;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;


public class MainActivity extends AppCompatActivity {
    String links;
    boolean downloading;
    private boolean updating = false;
    public Button quee, fetch, paste, mores;
    EditText input;
    ProgressBar progressbar;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String processId = "MyDlProcess",title;


    private final Function3<Float, Long, String, Unit> callback = (progress, o2, line) -> {
        runOnUiThread(() -> {
                    if(progress<0){
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
        mores= findViewById(R.id.more);
        progressbar = findViewById(R.id.progressBar2);


        progressbar.setVisibility(View.GONE);


        fetch.setOnClickListener(view -> {
            links = String.valueOf(input.getText());
            if(links.contains("http://") || links.contains("https://")){
                startDownload();
                progressbar.setVisibility(view.VISIBLE);
            }


        });


        paste.setOnClickListener(view -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip()) {
                android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
                android.content.ClipData data = clipboard.getPrimaryClip();
                if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                    input.setText(data.getItemAt(0).getText());
            }else{
                Toast.makeText(this, "Please copy a link first", Toast.LENGTH_SHORT).show();
            }
        });

        mores.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, more.class)));


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }



    private void startDownload() {
        if (downloading) {
            Toast.makeText(this, "cannot start download. a download is already in progress", Toast.LENGTH_LONG).show();
            return;
        }



        String url = input.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            input.setError(getString(R.string.url_error));
            return;
        }

        YoutubeDLRequest request = new YoutubeDLRequest(url);
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
                }, e -> {
                    Toast.makeText(this, "download failed", Toast.LENGTH_LONG).show();
                    downloading = false;
                });
        compositeDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @NonNull
    private File getDownloadLocation() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File youtubeDLDir = new File(downloadsDir, "youtubedl-android");
        if (!youtubeDLDir.exists()) youtubeDLDir.mkdir();
        return youtubeDLDir;
    }

    private void showStart() {

    }

    public boolean isStoragePermissionGranted() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            return false;
        }
    }























}