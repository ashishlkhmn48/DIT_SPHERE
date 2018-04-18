package com.ashishlakhmani.dit_sphere.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.DeleteData;
import com.ashishlakhmani.dit_sphere.classes.InsertToDatabase;
import com.ashishlakhmani.dit_sphere.fragments.About;
import com.ashishlakhmani.dit_sphere.fragments.Help;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

public class FacultyActivity extends AppCompatActivity {

    private boolean isStpo = false;
    TextView name, contact, email, location, specialization , branch;
    FloatingActionButton mFloatingActionButton;

    ConstraintLayout layout;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);

        putFcmToServer();
        initialize();
        getFacultyDetails();
        FloatingActionButtonTask();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.logout:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Logout");
                builder.setMessage("Do you really want to Logout ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
                        DeleteData deleteData = new DeleteData(FacultyActivity.this);
                        deleteData.execute(sp.getString("id", ""));
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;

            case R.id.about:
                if (!isFragmentVisible("about")) {
                    About about = new About();
                    loadFragmentForDrawer(about, "about");
                }
                break;

            case R.id.gmail:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "ashishlkhmn48@gmail.com"));
                    //intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject");
                    //intent.putExtra(Intent.EXTRA_TEXT, "your_text");
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.help:
                if (!isFragmentVisible("help")) {
                    Help help = new Help();
                    loadFragmentForDrawer(help, "help");
                }
                break;

            case R.id.notification:
                Intent intent = new Intent(this,FacultyNotificationActivity.class);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void setToolbarTitle(String title) {
        setTitle("");
        TextView toolbar_text = findViewById(R.id.toolbar_text);
        toolbar_text.setText(title);
    }


    private void initialize() {
        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        location = findViewById(R.id.location);
        specialization = findViewById(R.id.specialization);
        branch = findViewById(R.id.branch);
        mFloatingActionButton = findViewById(R.id.floatingActionButton);

        layout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.progressBar);
    }

    private void putFcmToServer() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String fcm_token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

        String id = sp.getString("id", "");
        String password = sp.getString("password", "");
        String branch = sp.getString("branch", "").toLowerCase();

        InsertToDatabase insertToDatabase = new InsertToDatabase(this, id, password, branch);
        insertToDatabase.execute(id, fcm_token, new Date().toString());
    }

    public void getFacultyDetails() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Faculty");
        query.whereMatches("email_id", sp.getString("id", "").toLowerCase());

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                progressBar.setVisibility(View.INVISIBLE);
                if (e == null) {

                    isStpo = object.getBoolean("is_stpo");
                    if(isStpo){
                        mFloatingActionButton.setVisibility(View.VISIBLE);
                    }else {
                        mFloatingActionButton.setVisibility(View.INVISIBLE);
                    }

                    String loc = object.getString("building")+", "+object.getString("floor") +" Floor, "+
                            object.getString("cabin");

                    List<String> list = object.getList("specialization");

                    String branchTxt = "( "+object.getString("branch")+" )";

                    name.setText(object.getString("name"));
                    contact.setText(object.getString("contact_num"));
                    email.setText(object.getString("email_id"));
                    branch.setText(branchTxt);
                    location.setText(loc);
                    specialization.setText(list.toString());

                    layout.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(FacultyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Load fragment for navigation drawer.
    private void loadFragmentForDrawer(Fragment fragment, String TAG) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        fragmentTransaction.replace(R.id.faculty_home_layout, fragment, TAG);
        fragmentTransaction.addToBackStack(TAG);
        fragmentTransaction.commit(); // save the changes
    }

    private boolean isFragmentVisible(String TAG) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG);
        if (fragment != null && fragment.isVisible())
            return true;
        else {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.popBackStack();
            return false;
        }
    }

    public void onNotify(View view){
        Intent intent = new Intent(this,PushNotificationActivity.class);
        startActivity(intent);
    }

    public void onCommunity(View view){
        Intent intent = new Intent(this,InteractActivity.class);
        startActivity(intent);
    }

    private void FloatingActionButtonTask() {
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FacultyActivity.this,PlacementPushNotificationActivity.class);
                startActivity(intent);
            }
        });
    }
}
