package com.example.hackathonfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "EventsDB";
    private static final String TABLE_NAME = "EVENTS";
    private static final String COL_ID = "id";
    private static final String COL_TITLE = "title";
    private static final String COL_URL = "url";
    private static final String COL_STARTDATE = "startDate";
    private static final String COL_ENDDATE = "endDate";
    private static final String COL_YEAR = "year";
    private static final String COL_CITY = "city";
    private static final String COL_HOST = "host";
    private static final String COL_LENGTH = "length";
    private static final String COL_SIZE = "size";
    private static final String COL_TRAVEL = "travel";
    private static final String COL_PRIZE = "prize";
    private static final String COL_HIGHSCHOOLERS = "highSchoolers";
    private static final String COL_COST = "cost";
    private static final String COL_FACEBOOKURL = "facebookURL";
    private static final String COL_TWITTERURL = "twitterURL";
    private static final String COL_GOOGLEPLUSURL = "googlePlusURL";
    private static final String COL_NOTES = "notes";
    private static final String COL_ISFAVORITED = "isFavorited";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NOTES_TABLE = "CREATE TABLE " + TABLE_NAME
                + "(" + COL_ID + " INTEGER PRIMARY KEY, "
                + COL_TITLE + " TEXT, " + COL_URL + " TEXT, "
                + COL_STARTDATE + " TEXT, " + COL_ENDDATE + " TEXT, "
                + COL_YEAR + " TEXT, " + COL_CITY + " TEXT, "
                + COL_HOST + " TEXT, " + COL_LENGTH + " TEXT, "
                + COL_SIZE + " TEXT, " + COL_TRAVEL + " TEXT, "
                + COL_PRIZE + " TEXT, " + COL_HIGHSCHOOLERS + " TEXT, "
                + COL_COST + " TEXT, " + COL_FACEBOOKURL + " TEXT, "
                + COL_TWITTERURL + " TEXT, " + COL_GOOGLEPLUSURL + " TEXT, "
                + COL_NOTES + " TEXT, " + COL_ISFAVORITED + " INTEGER " + ")";
        db.execSQL(CREATE_NOTES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    void addEvent(Event event) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_TITLE, event.title);
        values.put(COL_URL, event.url);
        values.put(COL_STARTDATE, event.startDate);
        values.put(COL_ENDDATE, event.endDate);
        values.put(COL_YEAR, event.year);
        values.put(COL_CITY, event.city);
        values.put(COL_HOST, event.host);
        values.put(COL_LENGTH, event.length);
        values.put(COL_SIZE, event.size);
        values.put(COL_TRAVEL, event.travel);
        values.put(COL_PRIZE, event.prize);
        values.put(COL_HIGHSCHOOLERS, event.highSchoolers);
        values.put(COL_COST, event.cost);
        values.put(COL_FACEBOOKURL, event.facebookURL);
        values.put(COL_TWITTERURL, event.twitterURL);
        values.put(COL_GOOGLEPLUSURL, event.googlePlusURL);
        values.put(COL_NOTES, event.notes);
        values.put(COL_ISFAVORITED, event.isFavorited ? 1 : 0);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    Event getEvent(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Event event = new Event();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_TITLE + "= ?;", new String[]{title});
        if (c.moveToFirst() && c.isNull(c.getColumnIndex(COL_TITLE))) {
            db.close();
            return null;
        }
        if (c.getCount() != 0) {
            event = getEventFromCursor(c);
            db.close();
            return event;
        } else {
            db.close();
            return null;
        }

    }

    boolean removeEvent(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_TITLE + "='" + title + "'", null) > 0;
    }


    ArrayList<Event> getAllEvents() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Event> events = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME;

        //Assume no duplicate titles
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        while (!(c.isAfterLast())) {
            Event event = getEventFromCursor(c);
            events.add(event);
            c.moveToNext();
        }

        db.close();
        return events;
    }

    Event getEventFromCursor(Cursor c) {
        Event event = new Event();
        event.title = c.getString(c.getColumnIndex(COL_TITLE));
        event.url = c.getString(c.getColumnIndex(COL_URL));
        event.startDate = c.getString(c.getColumnIndex(COL_STARTDATE));
        event.endDate = c.getString(c.getColumnIndex(COL_ENDDATE));
        event.year = c.getString(c.getColumnIndex(COL_YEAR));
        event.city = c.getString(c.getColumnIndex(COL_CITY));
        event.host = c.getString(c.getColumnIndex(COL_HOST));
        event.length = c.getString(c.getColumnIndex(COL_LENGTH));
        event.size = c.getString(c.getColumnIndex(COL_SIZE));
        event.travel = c.getString(c.getColumnIndex(COL_TRAVEL));
        event.prize = c.getString(c.getColumnIndex(COL_PRIZE));
        event.highSchoolers = c.getString(c.getColumnIndex(COL_HIGHSCHOOLERS));
        event.cost = c.getString(c.getColumnIndex(COL_COST));
        event.facebookURL = c.getString(c.getColumnIndex(COL_FACEBOOKURL));
        event.twitterURL = c.getString(c.getColumnIndex(COL_TWITTERURL));
        event.googlePlusURL = c.getString(c.getColumnIndex(COL_GOOGLEPLUSURL));
        event.notes = c.getString(c.getColumnIndex(COL_NOTES));
        event.isFavorited = c.getInt(c.getColumnIndex(COL_ISFAVORITED)) != 0;
        return event;
    }
}