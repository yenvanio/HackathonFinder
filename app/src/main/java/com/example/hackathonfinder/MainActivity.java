package com.example.hackathonfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    Calendar c;
    public static int year;
    public static List<Event> events;
    public static List<Event> searchEvents;
    RecyclerView rv;
    RVAdapter adapter;
    JSONObject[] holder;
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

        events = new ArrayList<>();
        holder = new JSONObject[12];
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
            rv.setAdapter(adapter);
        }

        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RVAdapter(events, this, db);

        requestQueue = Volley.newRequestQueue(this);
        rv.setAdapter(adapter);

        LoadCardTask task = new LoadCardTask();
        task.execute();

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
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
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
                Intent i = new Intent(this, FavoriteActivity.class);
                startActivity(i);
                break;

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

    private class LoadCardTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);

            for (int i = 9; i <= 12; i++) {
                String url = getUrl(Integer.toString(i), Integer.toString(year));
                final int j = i;

                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, future, future);
                requestQueue.add(jsonObjectRequest);
                try {
                    JSONObject response = future.get();
                    holder[j - 1] = response;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                for (int i = 9; i <= 12; i++) {
                    JSONObject response = holder[i - 1];

                    final String month = getMonth(i);
                    if (response.getJSONArray(month) != null) {
                        JSONArray jsonArray = response.getJSONArray(month);

                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject event = jsonArray.getJSONObject(j);
                            String title = event.getString("title");
                            Event event_from_db = db.getEvent(title);
                            if (event_from_db != null) {
                                events.add(event_from_db);
                            } else {
                                String url = event.getString("url");
                                String startDate = event.getString("startDate");
                                String endDate = event.getString("endDate");
                                String year = event.getString("year");
                                String city = event.getString("city");
                                String host = event.getString("host");
                                String length = event.getString("length");
                                String size = event.getString("size");
                                String travel = event.getString("travel");
                                String prize = event.getString("prize");
                                String highSchoolers = event.getString("highSchoolers");
                                String cost = event.getString("cost");
                                String facebookURL = "", twitterURL = "", googlePlusURL = "";
                                if (event.has("facebookURL")) {
                                    facebookURL = event.getString("facebookURL");
                                }
                                if (event.has("twitterURL")) {
                                    twitterURL = event.getString("twitterURL");
                                }
                                if (event.has("googlePlusURL")) {
                                    googlePlusURL = event.getString("googlePlusURL");
                                }
                                String notes = event.getString("notes");
                                events.add(new Event(title, url, startDate, endDate, year, city, host,
                                        length, size, travel, prize, highSchoolers, cost, facebookURL, twitterURL,
                                        googlePlusURL, notes, false));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            mProgress.hide();
        }

    }

    public String getUrl(String month, String year) {

        if (month.length() == 1) {
            month = "0" + month;
        }
        String url = "http://www.hackalist.org/api/1.0/" + year + "/" + month + ".json";
        System.out.println(url);
        return url;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month - 1];
    }

}
