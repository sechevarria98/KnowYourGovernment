package com.example.know_your_gov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Details extends AppCompatActivity {

    private Intent intent;
    private Politician politician;
    private ImageView imageView;
    private static final String TAG = "Details";
    private final String DemURL = "https://democrats.org/";
    private final String RepURL = "https://www.gop.com/";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        intent = getIntent();
        politician = (Politician) intent.getSerializableExtra("pol");
        String loc = (String) intent.getSerializableExtra("loc");
        ((TextView) findViewById(R.id.place2)).setText(loc);
        ((TextView) findViewById(R.id.NAME)).setText(politician.getName());
        ((TextView) findViewById(R.id.OFFICE)).setText(politician.getOffice());
        ((TextView) findViewById(R.id.PARTY)).setText( "(" + politician.getParty() + ")");

        imageView = findViewById(R.id.PHOTO);

        if(politician.getPhotoUrl() != null)
            loadRemoteImage(politician.getPhotoUrl());

        if(politician.getWebsite() != null)
            ((TextView) findViewById(R.id.WEBSITESPACE)).setText(politician.getWebsite());
        else
            ((TextView) findViewById(R.id.WEBSITESPACE)).setText("Not Provided.");

        if(politician.getEmail() != null)
            ((TextView) findViewById(R.id.EMAILSPACE)).setText(politician.getEmail());
        else
            ((TextView) findViewById(R.id.EMAILSPACE)).setText("Not Provided.");

        if(politician.getPhone() != null)
            ((TextView) findViewById(R.id.PHONESPACE)).setText(politician.getPhone());
        else
            ((TextView) findViewById(R.id.PHONESPACE)).setText("Not Provided.");

        if(politician.getAddress() != null)
            ((TextView) findViewById(R.id.ADDRESSSPACE)).setText(politician.getAddress());
        else
            ((TextView) findViewById(R.id.ADDRESSSPACE)).setText("Not Provided.");

        if(politician.getParty().equals("Democratic Party")) {
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.BLUE);
            ((ImageView) findViewById(R.id.LOGO)).setImageResource(R.drawable.dem_logo);
        }
        else if(politician.getParty().equals("Republican Party")) {
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.RED);
            ((ImageView) findViewById(R.id.LOGO)).setImageResource(R.drawable.rep_logo);
        }
        else {
            ((ConstraintLayout) findViewById(R.id.layout)).setBackgroundColor(Color.BLACK);
            ((ImageView) findViewById(R.id.LOGO)).setVisibility(View.INVISIBLE);
        }

        if (politician.getFacebook() == null) {
            ((ImageView) findViewById(R.id.FACEBOOK)).setVisibility(View.INVISIBLE);
        }
        else if (politician.getTwitter() == null) {
            ((ImageView) findViewById(R.id.TWITTER)).setVisibility(View.INVISIBLE);
        }
        else if (politician.getYoutube() == null) {
            ((ImageView) findViewById(R.id.YOUTUBE)).setVisibility(View.INVISIBLE);
        }

        Linkify.addLinks(((TextView) findViewById(R.id.WEBSITESPACE)), Linkify.ALL);
        Linkify.addLinks(((TextView) findViewById(R.id.ADDRESSSPACE)), Linkify.ALL);
        Linkify.addLinks(((TextView) findViewById(R.id.PHONESPACE)), Linkify.ALL);
        Linkify.addLinks(((TextView) findViewById(R.id.EMAILSPACE)), Linkify.ALL);
    }

    private boolean doNetCheck() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void loadRemoteImage(final String imageURL) {
        final long start = System.currentTimeMillis();

        Picasso.get().load(imageURL)
                .error(R.drawable.brokenimage)
                .placeholder(R.drawable.placeholder)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                photoClicked(view);
                            }
                        });
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
        else if (politician.getParty().equals("Republican Party")) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(RepURL));
            startActivity(i);
        }
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = politician.getTwitter();
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + politician.getFacebook();
        String urlToUse;

        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                urlToUse = "fb://page/" + politician.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void youtubeClicked(View v) {
        String name = politician.getYoutube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void photoClicked(View v) {
        if (doNetCheck()) {
            if (politician.getPhotoUrl() == null) {
                Toast.makeText(this, "Just Why?", Toast.LENGTH_SHORT).show();
            } else {
                String location = ((TextView) findViewById(R.id.place2)).getText().toString();
                Intent intent = new Intent(this, PhotoDetail.class);
                intent.putExtra("pol1", politician);
                intent.putExtra("loc1", location);
                startActivity(intent);
            }
        }
        else
            noInternet();
    }
    public void noInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without and internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}