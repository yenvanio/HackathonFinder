package com.example.hackathonfinder;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.TimeZone;

import me.grantland.widget.AutofitHelper;

public class AddActivity extends AppCompatActivity {

    Button add;
    TextView dateText;
    FloatingActionButton date;
    EditText time,link,host,cost,title, place,prizes,travel,highschool,twitter,googleplus,facebook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add a Hackathon");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


         initialize();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

                DatePickerDialog dialog = new DatePickerDialog(AddActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        dateText.setText(dayOfMonth+"/"+(monthOfYear+1)+"/"+year);
                    }
                },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    attemptAdd();
            }
        });
    }

    public void attemptAdd()
    {
        String titleText = title.getText().toString();
        String placeText = place.getText().toString();
        String timeText = time.getText().toString();
        String linkText = link.getText().toString();
        String hsText = highschool.getText().toString();
        String prizeText = prizes.getText().toString();
        String travelText = travel.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(hsText) && !hsText.equalsIgnoreCase("yes")&& !hsText.equalsIgnoreCase("no")
                && !hsText.equalsIgnoreCase("unknown"))
        {
            highschool.setError(getString(R.string.error_invalid_input));
            cancel=true;
            focusView = title;
        }

        if (!TextUtils.isEmpty(prizeText) && !prizeText.equalsIgnoreCase("yes")&& !prizeText.equalsIgnoreCase("no")
                && !prizeText.equalsIgnoreCase("unknown"))
        {
            prizes.setError(getString(R.string.error_invalid_input));
            cancel=true;
            focusView = title;
        }

        if (!TextUtils.isEmpty(travelText) && !travelText.equalsIgnoreCase("yes")&& !travelText.equalsIgnoreCase("no")
                && !travelText.equalsIgnoreCase("unknown"))
        {
            travel.setError(getString(R.string.error_invalid_input));
            cancel=true;
            focusView = title;
        }

        if (TextUtils.isEmpty(titleText))
        {
            title.setError(getString(R.string.error_empty_field));
            cancel=true;
            focusView = title;
        }


        if (TextUtils.isEmpty(titleText))
        {
            title.setError(getString(R.string.error_empty_field));
            cancel=true;
            focusView = title;
        }

        if (TextUtils.isEmpty(placeText))
        {
            place.setError(getString(R.string.error_empty_field));
            cancel=true;
            focusView = place;
        }

        if (TextUtils.isEmpty(timeText))
        {
            time.setError(getString(R.string.error_empty_field));
            cancel=true;
            focusView = time;
        }

        if (TextUtils.isEmpty(linkText))
        {
            link.setError(getString(R.string.error_empty_field));
            cancel=true;
            focusView = link;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{"yoganathan.shiv@gmail.com"});
            i.putExtra(Intent.EXTRA_SUBJECT, "Hackathon Addition");
            i.putExtra(Intent.EXTRA_TEXT, "  {\n" +
                    "  \"title\": \"" + title.getText() + "\",\n" +
                    "  \"url\": \"" + link.getText() + "\",\n" +
                    "  \"startDate\": \"" + dateText.getText() + "\",\n" +
                    "  \"endDate\": \"" + "auto_fill" + "\",\n" +
                    "  \"year\": \"" + dateText.getText() + "\",\n" +
                    "  \"city\": \"" + place.getText() + "\",\n" +
                    "  \"host\": \"" + host.getText() + "\",\n" +
                    "  \"length\": \"" + time.getText() + "\",\n" +
                    "  \"size\": \"" + "unknown" + "\",\n" +
                    "  \"travel\": \"" + travel.getText() + "\",\n" +
                    "  \"prize\": \"" + prizes.getText() + "\",\n" +
                    "  \"highSchoolers\": \"" + highschool.getText() + "\",\n" +
                    "  \"cost\": \"" + cost.getText() + "\",\n" +
                    "  \"facebookURL\": \"" + facebook.getText() + "\",\n" +
                    "  \"twitterURL\": \"" + twitter.getText() + "\",\n" +
                    "  \"googlePlusURL\": \"" + googleplus.getText() + "\",\n" +
                    "  \"notes\": \"" + "" + "\"\n" +
                    "},");
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(AddActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initialize()
    {
        title = (EditText) findViewById(R.id.title);
        place = (EditText) findViewById(R.id.place);
        time = (EditText) findViewById(R.id.time);
        link = (EditText) findViewById(R.id.link);

        date = (FloatingActionButton) findViewById(R.id.date);
        add  = (Button) findViewById(R.id.addButton);

        host = (EditText) findViewById(R.id.host);
        cost = (EditText) findViewById(R.id.cost);

        prizes = (EditText) findViewById(R.id.prizes);
        travel = (EditText) findViewById(R.id.travel);
        highschool = (EditText) findViewById(R.id.highschool);

        twitter = (EditText) findViewById(R.id.twitter);
        googleplus = (EditText) findViewById(R.id.google);
        facebook = (EditText) findViewById(R.id.facebook);
        dateText = (TextView) findViewById(R.id.dateText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}

