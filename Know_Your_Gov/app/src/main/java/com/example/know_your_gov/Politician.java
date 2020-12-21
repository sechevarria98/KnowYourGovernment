package com.example.know_your_gov;

import java.io.Serializable;

public class Politician implements Serializable {

    private String name;
    private String office;
    private String party;
    private String address;
    private String photoUrl;
    private String phone;
    private String website;
    private String email;
    private String facebook;
    private String twitter;
    private String youtube;

    public Politician(String name, String office, String party, String address, String photoUrl, String phone, String website, String email, String facebook, String twitter, String youtube) {
        this.name = name;
        this.office = office;
        this.party = party;
        this.address = address;
        this.photoUrl = photoUrl;
        this.phone = phone;
        this.website = website;
        this.email = email;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
    }

    public String getName() { return name; }

    public String getOffice() { return office; }

    public String getParty() { return party; }

    public String getAddress() { return address; }

    public String getPhotoUrl() { return  photoUrl; }

    public String getPhone() { return phone; }

    public String getWebsite() { return website; }

    public String getEmail() { return email; }

    public String getFacebook() { return  facebook; }

    public String getTwitter() { return twitter; }

    public String getYoutube() { return youtube; }
}
