package com.example.know_your_gov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class PhotoDetail extends AppCompatActivity {

    private Intent intent;
    private Politician politician;
    private ImageView imageView;
    private final String DemURL = "https://democrats.org/";
    private final String RepURL = "https://www.gop.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        intent = getIntent();
        politician = (Politician) intent.getSerializableExtra("pol1");
        String loc = (String) intent.getSerializableExtra("loc1");
        imageView = findViewById(R.id.PHOTO3);

        if(politician.getPhotoUrl() != null) {
            loadRemoteImage(politician.getPhotoUrl());
        }

        if(politician.getParty().equals("Democratic Party")) {
            ((ImageView) findViewById(R.id.LOGO3)).setImageResource(R.drawable.dem_logo);
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.BLUE);
        }
        else if (politician.getParty().equals("Republican Party")) {
            ((ImageView) findViewById(R.id.LOGO3)).setImageResource(R.drawable.rep_logo);
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.RED);
        }
        else {
            ((ImageView) findViewById(R.id.LOGO3)).setImageResource(0);
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.BLACK);
        }

        ((TextView) findViewById(R.id.OFFICE3)).setText(politician.getOffice());
        ((TextView) findViewById(R.id.NAME3)).setText(politician.getName());
        ((TextView) findViewById(R.id.place4)).setText(loc);
    }

    private void loadRemoteImage(final String imageURL) {
        final long start = System.currentTimeMillis();

        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                    }
                });
    }

    public void logoClicked(View v) {
        if(politician.getParty().equals("Democratic Party")) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(DemURL));
            startActivity(i);
        }
        else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(RepURL));
            startActivity(i);
        }
    }
}