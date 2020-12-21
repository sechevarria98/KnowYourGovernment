package com.example.know_your_gov;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivityTAG";

    private List<Politician> politicianList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PoliticianAdapter politicianAdapter;

    private static int MY_LOCATION_REQUEST_CODE_ID = 111;
    private LocationManager locationManager;
    private Criteria criteria;

    private DataLoaderRunnable dataLoaderRunnable;

    private TextView places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        politicianAdapter = new PoliticianAdapter(politicianList, this);
        recyclerView.setAdapter(politicianAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        places = findViewById(R.id.place);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (savedInstanceState == null) {
            if (doNetCheck()) {
                onCreateLocations();
            } else
                noInternet();
        }
        else {
            if(doNetCheck()) {
                String tmp = savedInstanceState.getString("LOCATION");
                if (tmp != null) {
                    places.setText(savedInstanceState.getString("LOCATION"));
                    dataLoaderRunnable = new DataLoaderRunnable(this, tmp);
                    new Thread(dataLoaderRunnable).start();
                }
                else
                    onCreateLocations();
            }
            else
                noInternet();
        }
    }

    ///////////////////////   INTERNET   ///////////////////////
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
    ///////////////////////   END   ///////////////////////

    ///////////////////////   DIALOG   ///////////////////////
    private void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Enter City, State, or a Zip Code:");
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(editText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String place = editText.getText().toString();
                politicianList.clear();
                if(doNetCheck())
                    enteredLocation(place);
                else
                    noInternet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void noInternet() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Network Connection");
        builder.setMessage("Data cannot be accessed/loaded without and internet connection");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    ///////////////////////   END   ///////////////////////

    ///////////////////////   MENU   ///////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_about:
                Intent intent = new Intent(this, aboutactivity.class);
                startActivity(intent);
                break;
            case R.id.m_entry:
                dialog();
                break;
            default:
                String err = "Unknown Menu Item: " + item.getTitle();
                Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    ///////////////////////   END   ///////////////////////

    ///////////////////////   LOCATION   ///////////////////////
    public void onCreateLocations() {
        criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    MY_LOCATION_REQUEST_CODE_ID);
        } else
            setLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_LOCATION_REQUEST_CODE_ID) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PERMISSION_GRANTED) {
                setLocation();
                return;
            }
        }
        ((TextView) findViewById(R.id.place)).setText(R.string.no_perm);
    }
    @SuppressLint("MissingPermission")
    private void setLocation() {
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location currentLocation = null;
        if (bestProvider != null) {
            currentLocation = locationManager.getLastKnownLocation(bestProvider);
        }
        if (currentLocation != null) {
            doLatLon(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
    }
    private void doLatLon(Double lat, Double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation(lat, lon, 10);
            StringBuilder sb = new StringBuilder();
            for(Address ad : addresses) {
                String a = String.format(" %s %s %s ",
                        (ad.getLocality() == null ? "" : ad.getLocality()),
                        (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
                        (ad.getPostalCode() == null ? "" : ad.getPostalCode()));
                if (!a.trim().isEmpty())
                    sb.append(a.trim());
                break;
            }
            dataLoaderRunnable = new DataLoaderRunnable(this, sb.toString());
            new Thread(dataLoaderRunnable).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void enteredLocation(String loc) {
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try{
//            List<Address> addresses;
//            addresses = geocoder.getFromLocationName(loc, 10);
//            StringBuilder sb  = new StringBuilder();
//            for (Address ad : addresses) {
//                String a = String.format(" %s %s %s ",
//                        (ad.getLocality() == null ? "" : ad.getLocality()),
//                        (ad.getAdminArea() == null ? "" : ad.getAdminArea()),
//                        (ad.getPostalCode() == null ? "" : ad.getPostalCode()));
//                if (!a.trim().isEmpty())
//                    sb.append(a.trim());
//                break;
//            }
//            dataLoaderRunnable = new DataLoaderRunnable(this, sb.toString());
//            new Thread(dataLoaderRunnable).start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        dataLoaderRunnable = new DataLoaderRunnable(this, loc);
        new Thread(dataLoaderRunnable).start();
    }
    ///////////////////////   END   ///////////////////////

    ///////////////////////   CLICK   ///////////////////////
    @Override
    public void onClick(View view) {
        if(doNetCheck()) {
            final int pos = recyclerView.getChildLayoutPosition(view);
            Politician p = politicianList.get(pos);
            String location = ((TextView) findViewById(R.id.place)).getText().toString();
            Intent intent = new Intent(this, Details.class);
            intent.putExtra("pol", p);
            intent.putExtra("loc", location);
            startActivity(intent);
        }
        else {
            noInternet();
        }
    }
    ///////////////////////   END   ///////////////////////

    ///////////////////////   RUNNABLE   ///////////////////////
    public void returnData(ArrayList<Politician> plist) {
        politicianList.clear();
        for(int i = 0; i < plist.size(); i++) {
            Politician p = plist.get(i);
            politicianList.add(p);
        }
        politicianAdapter.notifyDataSetChanged();
    }

    public void displayLoc(String loc) {
        if(loc != null)
            ((TextView) findViewById(R.id.place)).setText(loc);
        else
            ((TextView) findViewById(R.id.place)).setText(R.string.not_valid);
    }
    ///////////////////////   END   ///////////////////////

    ///////////////////////   INSTANCE STATE   ///////////////////////
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(!politicianList.isEmpty())
            outState.putString("LOCATION", places.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
    ///////////////////////   END   ///////////////////////
}