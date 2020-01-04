package com.example.hackathonfinder;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

class Event implements Parcelable {

    String title;
    String url;
    String startDate;
    String endDate;
    String year;
    String city;
    String host;
    String length;
    String size;
    String travel;
    String prize;
    String highSchoolers;
    String cost;
    String facebookURL;
    String twitterURL;
    String googlePlusURL;
    String notes;
    Date date;
    Boolean isFavorited;

    Event() {}

    Event(String title, String url, String startDate, String endDate, String year, String city,
          String host, String length, String size, String travel, String prize, String highSchoolers,
          String cost, String facebookURL, String twitterURL, String googlePlusURL, String notes, Boolean isFavorited) {
        this.title = title;
        this.url = url;
        this.startDate = startDate;
        this.endDate = endDate;
        this.year = year;
        this.city = city;
        this.host = host;
        this.length = length;
        this.size = size;
        this.travel = travel;
        this.prize = prize;
        this.highSchoolers = highSchoolers;
        this.cost = cost;
        this.facebookURL = facebookURL;
        this.twitterURL = twitterURL;
        this.googlePlusURL = googlePlusURL;
        this.notes = notes;
        this.isFavorited = isFavorited;
    }

    protected Event(Parcel in) {
        title = in.readString();
        url = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        year = in.readString();
        city = in.readString();
        host = in.readString();
        length = in.readString();
        size = in.readString();
        travel = in.readString();
        prize = in.readString();
        highSchoolers = in.readString();
        cost = in.readString();
        facebookURL = in.readString();
        twitterURL = in.readString();
        googlePlusURL = in.readString();
        notes = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.url);
        parcel.writeString(this.startDate);
        parcel.writeString(this.endDate);
        parcel.writeString(this.year);
        parcel.writeString(this.city);
        parcel.writeString(this.host);
        parcel.writeString(this.length);
        parcel.writeString(this.size);
        parcel.writeString(this.travel);
        parcel.writeString(this.prize);
        parcel.writeString(this.highSchoolers);
        parcel.writeString(this.cost);
        parcel.writeString(this.facebookURL);
        parcel.writeString(this.twitterURL);
        parcel.writeString(this.googlePlusURL);
        parcel.writeString(this.notes);
    }

}
