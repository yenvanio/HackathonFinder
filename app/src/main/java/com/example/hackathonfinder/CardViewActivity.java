package com.example.hackathonfinder;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.EventDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class CardViewActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    List<Event> events;
    TextView title, date, place, time, link, host, prize, cost, travel, highschool,twitter,google,facebook;
    ImageView calendar, costIcon;

    private GoogleAccountCredential mCredential;
    int position;
    ProgressDialog mProgress;

    private static final int REQUEST_ACCOUNT_PICKER = 252;
    private static final int REQUEST_AUTHORIZATION = 253;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 254;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 255;
    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        events = getIntent().getParcelableArrayListExtra("Events");
        position = getIntent().getExtras().getInt("Position");

        calendar = (ImageView) findViewById(R.id.addCalendar);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getResultsFromApi();
            }
        });

        mProgress = new ProgressDialog(this);
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        title = (TextView) findViewById(R.id.title);
        date = (TextView) findViewById(R.id.date);
        place = (TextView) findViewById(R.id.place);
        time = (TextView) findViewById(R.id.time);
        link = (TextView) findViewById(R.id.link);
        host = (TextView) findViewById(R.id.host);
        prize = (TextView) findViewById(R.id.prizes);
        cost = (TextView) findViewById(R.id.cost);
        costIcon=(ImageView)findViewById(R.id.costIcon);
        costIcon.setColorFilter(Color.GRAY);
        travel = (TextView) findViewById(R.id.travel);
        highschool = (TextView) findViewById(R.id.highschool);
        twitter = (TextView) findViewById(R.id.twitter);
        google = (TextView) findViewById(R.id.google);
        facebook = (TextView) findViewById(R.id.facebook);
        loadText(events, position);
    }

    private void loadText(List<Event> events, int i) {
        place.setText(events.get(i).city);
        title.setText(events.get(i).title);
        date.setText(events.get(i).startDate + " - " + (events.get(i).endDate));
        place.setText(events.get(i).city);
        time.setText(events.get(i).length + " hours");
        link.setText(events.get(i).url);
        host.setText("Hosted By: " + events.get(i).host);


        if(events.get(i).prize.equalsIgnoreCase("yes")){prize.setText("There will be prizes");}
        else{prize.setText("Unknown");}

        cost.setText("The cost is " + events.get(i).cost);

        if(events.get(i).travel.equalsIgnoreCase("yes")){travel.setText("Travel expenses will be reimbursed");}
        else{travel.setText("Travel expenses will not be reimbursed");}

        if(events.get(i).highSchoolers.equalsIgnoreCase("yes")){highschool.setText("Highschoolers allowed");}
        else{highschool.setText("No Highschoolers");}

        twitter.setText(events.get(i).twitterURL);
        google.setText(events.get(i).googlePlusURL);
        facebook.setText(events.get(i).facebookURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        } else if (!EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_CALENDAR)){
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to write to your calendar.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.WRITE_CALENDAR);
        } else if(!EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALENDAR)){
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to read from your calendar.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.READ_CALENDAR);
        } else {
            new createEvent(mCredential).execute();
        }

    }

    private class createEvent extends AsyncTask<Void, Void, Void>{

        private com.google.api.services.calendar.Calendar service = null;
        private Exception mLastError;

        public createEvent (GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            service = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("HackathonFinder")
                    .build();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            com.google.api.services.calendar.model.Event event = new com.google.api.services.calendar.model.Event()
                    .setSummary(events.get(position).title)
                    .setLocation(events.get(position).city)
                    .setDescription(events.get(position).notes);


            int year = Calendar.getInstance().get(Calendar.YEAR);

            String startDate =date(events.get(position).startDate + ", " + year);
            String  endDate = date(events.get(position).endDate + ", " + year);

            DateTime startDateTime = new DateTime(startDate);
            DateTime endDateTime = new DateTime(endDate);

            EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
            EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);

            event.setStart(startEventDateTime);
            event.setEnd(endEventDateTime);


            com.google.api.services.calendar.model.Event.Source url =  new com.google.api.services.calendar.model.Event.Source()
                    .setUrl(events.get(position).url);
            event.setSource(url);


            String calendarId = "primary";
            try {
                event = service.events().insert(calendarId, event).execute();
                System.out.printf("Event created: %s\n", event.getHtmlLink());
            } catch (Exception e) {
                mLastError = e;
                e.printStackTrace();
                cancel(true);
            }
            return null;
        }
        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Task canceled", Toast.LENGTH_SHORT).show();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) { // not entered
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) { // not entered
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            REQUEST_AUTHORIZATION);
                } else {

                }
            } else {

            }
        }

        @Override
        protected void onPreExecute()
        {
            mProgress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mProgress.hide();
            Toast.makeText(getApplicationContext(), "Event created !", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aVoid);
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }

        }
        else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);
        }
    }


    public static String date(String mytime) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(mytime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd");
        String finalDate = timeFormat.format(myDate);
        return finalDate;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_SHORT).show();

                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


}
