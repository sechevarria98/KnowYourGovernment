package com.example.know_your_gov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class aboutactivity extends AppCompatActivity {

    private TextView link;
    private static final String URL = "https://developers.google.com/civic-information/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutactivity);

        link = findViewById(R.id.Link);
    }

    public void stringClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(URL));
        startActivity(intent);
    }
}