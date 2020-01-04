package com.example.hackathonfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    Calendar c;
    public static int year;
    public static List<Event> events;
    public static List<Event> searchEvents;
    RecyclerView rv;
    RVAdapter adapter;
    ProgressDialog mProgress;
    SearchView searchView;
    TextView link;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        events = new ArrayList<>();
        mProgress = new ProgressDialog(this);

        db = new DBHelper(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j = new Intent(getBaseContext(), AddActivity.class);
                startActivity(j);
            }
        });

        if (savedInstanceState != null) {
            events = savedInstanceState.getParcelableArrayList("Events");
            System.out.println("Coming Back " + events.size());
            rv.setAdapter(adapter);
        }

        events = db.getAllEvents();
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter(events, this, db);

        rv.setAdapter(adapter);

        link = (TextView) findViewById(R.id.link);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchEvents = new ArrayList<>();
                searchEvents.clear();

                for (int i = 0; i <= events.size() - 1; i++) {
                    if ((StringUtils.containsIgnoreCase(events.get(i).city, query))
                            || StringUtils.containsIgnoreCase(events.get(i).title, query)) {
                        searchEvents.add(events.get(i));
                    }
                }
                Intent i = new Intent(FavoriteActivity.this, SearchActivity.class);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
            case android.R.id.home:
                super.onBackPressed();
                return true;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
