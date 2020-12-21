package com.example.know_your_gov;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DataLoaderRunnable implements Runnable {
    private static final String TAG = "DataLoaderRunnable";
    private MainActivity mainActivity;
    private String address;

    private static final String DATA_URL = "https://www.googleapis.com/civicinfo/v2/representatives?key=";
    private static final String API_KEY = null;

    DataLoaderRunnable(MainActivity mainActivity, String address) {
        this.mainActivity = mainActivity;
        this.address = address;
    }

    @Override
    public void run() {
        final String URL = DATA_URL + API_KEY + "&address=" + address;

        Uri dataUri = Uri.parse(URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            java.net.URL url = new URL(urlToUse);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                handleResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            handleResults(null);
            return;
        }
        handleResults(sb.toString());
    }

    private void handleResults(final String jsonString) {
        final ArrayList<Politician> p = parseJSON(jsonString);
        final String loc = parseNormalized(jsonString);
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(p != null)
                    mainActivity.returnData(p);
            }
        });
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (loc != null)
                mainActivity.displayLoc(loc);
            }
        });
    }

    private String parseNormalized(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);

            //Get Normalized Input object
            String city = "";
            String state = "";
            String zip = "";
            JSONObject normal = jObjMain.getJSONObject("normalizedInput");
            if(normal.has("city"))
                city = normal.getString("city");
            if(normal.has("state"))
                state = normal.getString("state");
            if(normal.has("zip"))
                zip = normal.getString("zip");

            String location = null;
            if (city.equals(""))
                location = state + " " + zip;
            else
                location = city + ", " + state + " " + zip;

            //Log.d(TAG, "parseNormalized: " + location);
            return location;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Politician> parseJSON(String s) {
        ArrayList<Politician> pList = new ArrayList<>();

        try {
            JSONObject jObjMain = new JSONObject(s);

            JSONArray offices = jObjMain.getJSONArray("offices");
            JSONArray officials = jObjMain.getJSONArray("officials");

            for(int i = 0; i < offices.length(); i++) {
                JSONObject position = offices.getJSONObject(i);
                String office = position.getString("name");

                JSONArray indices = position.getJSONArray("officialIndices");
                for(int j = 0; j < indices.length(); j++) {
                    JSONObject personInfo = officials.getJSONObject(indices.getInt(j));
                    String name = personInfo.getString("name");
                    String party = personInfo.getString("party");

                    String photoUrl = null;
                    if(personInfo.has("photoUrl"))
                         photoUrl = personInfo.getString("photoUrl");

                    String phone = null;
                    if(personInfo.has("phones")) {
                        JSONArray phones = personInfo.getJSONArray("phones");
                        phone = phones.getString(0);
                    }

                    String website = null;
                    if(personInfo.has("urls")) {
                        JSONArray urls = personInfo.getJSONArray("urls");
                        website = urls.getString(0);
                    }

                    String email = null;
                    if(personInfo.has("emails")) {
                        JSONArray emails = personInfo.getJSONArray("emails");
                        email = emails.getString(0);
                    }

                    String facebook = null;
                    String twitter = null;
                    String youtube = null;
                    if(personInfo.has("channels")) {
                        JSONArray channel = personInfo.getJSONArray("channels");
                        for(int k = 0; k < channel.length(); k++) {
                            JSONObject specificChannel = channel.getJSONObject(k);
                            if(specificChannel.has("type")) {
                                if(specificChannel.getString("type").equals("Facebook"))
                                    facebook = specificChannel.getString("id");
                                if(specificChannel.getString("type").equals("Twitter"))
                                    twitter = specificChannel.getString("id");
                                if(specificChannel.getString("type").equals("YouTube"))
                                    youtube = specificChannel.getString("id");
                            }
                        }
                    }

                    String loc = null;
                    if(personInfo.has("address")) {
                        JSONArray addresses = personInfo.getJSONArray("address");
                        JSONObject addressSpecific = addresses.getJSONObject(0);
                        if (addressSpecific.has("line1"))
                            loc = addressSpecific.getString("line1");
                        if (addressSpecific.has("line2"))
                            loc = loc + " " + addressSpecific.getString("line2");
                        if (addressSpecific.has("line3"))
                            loc = loc + " " + addressSpecific.getString("line3");
                        if (addressSpecific.has("city"))
                            loc = loc + " " + addressSpecific.getString("city");
                        if (addressSpecific.has("state"))
                            loc = loc + " " + addressSpecific.getString("state");
                        if (addressSpecific.has("zip"))
                            loc = loc + " " + addressSpecific.getString("zip");
                    }

                    Politician p = new Politician(name, office, party, loc, photoUrl, phone, website, email, facebook, twitter, youtube);
                    pList.add(p);
                }
            }
            return pList;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
