package com.hanira.gdele;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

public class more extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);



        Button gdelete = findViewById(R.id.instafollow);
        Button abc = findViewById(R.id.abc);
        Button puzzle = findViewById(R.id.puzzle);
        Button word = findViewById(R.id.word);
        Button tikfollow = findViewById(R.id.tikfollow);
        Button tiklikes = findViewById(R.id.tiklikes);
        Button ludo = findViewById(R.id.ludo);
        Button likee = findViewById(R.id.likee);
        Button downloads= findViewById(R.id.download);


        gdelete.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.gdele"))));
        abc.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanira.love"))));
        word.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.easyPuzzle"))));
        puzzle.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.puzzle"))));
        tiklikes.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.tiklikes"))));
        tikfollow.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.tikfollow"))));
        ludo.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.lodu"))));
        likee.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.likeefollow"))));
        downloads.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hanif.gdele"))));


    }






}