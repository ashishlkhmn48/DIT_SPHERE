package com.ashishlakhmani.dit_sphere.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.DeleteData;
import com.ashishlakhmani.dit_sphere.classes.InsertToDatabase;
import com.ashishlakhmani.dit_sphere.fragments.About;
import com.ashishlakhmani.dit_sphere.fragments.Calculator;
import com.ashishlakhmani.dit_sphere.fragments.Club;
import com.ashishlakhmani.dit_sphere.fragments.CommonImageFragment;
import com.ashishlakhmani.dit_sphere.fragments.Downloads;
import com.ashishlakhmani.dit_sphere.fragments.Help;
import com.ashishlakhmani.dit_sphere.fragments.News;
import com.ashishlakhmani.dit_sphere.fragments.Profile;
import com.ashishlakhmani.dit_sphere.fragments.Result;
import com.ashishlakhmani.dit_sphere.fragments.Syllabus;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public BottomNavigationView bottomNavigationView;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    public View header;
    private ProgressDialog progressDialog;

    private static final int MY_PERMISSIONS_REQUEST = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sp = getSharedPreferences("interact_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isOpen", false);
        editor.apply();

        putFcmToServer();
        initialize();
        toolBarTask();
        bottomNavigationViewTask();
        navigationDrawerTask();

    }

    public void setToolbarTitle(String title) {
        setTitle("");
        TextView toolbar_text = findViewById(R.id.toolbar_text);
        toolbar_text.setText(title);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        SharedPreferences sharedPreferences = getSharedPreferences("permission", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("granted", true);
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    editor.putBoolean("granted", false);
                }

                editor.apply();
                break;
        }
    }

    //Permission check
    public boolean checkAndRequestPermissions() {
        int readStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);


        int writeStoragePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        if (readStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
            return false;
        }

        return true;
    }

    //To initialize widgets.
    private void initialize() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        navigationView = findViewById(R.id.nav_view);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(this);

    }


    private void putFcmToServer() {
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String fcm_token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");

        String id = sp.getString("id", "");
        String password = sp.getString("password", "");
        String branch = sp.getString("branch", "");

        InsertToDatabase insertToDatabase = new InsertToDatabase(this, id, password, branch);
        insertToDatabase.execute(id, fcm_token, new Date().toString());

        ParseQuery<ParseObject> query = new ParseQuery<>("Students");
        query.whereEqualTo("student_id", sp.getString("id", ""));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                    String fcm_token = sp.getString(getString(R.string.FCM_TOKEN), "");

                    object.put("fcm_token", fcm_token);
                    object.saveInBackground();
                } else {
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    //To assign bottom navigation view task.
    private void bottomNavigationViewTask() {
        ViewCompat.setElevation(bottomNavigationView, 100);
        News news = new News();
        loadFragment(news, "layout_news");
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.news:
                        News news = new News();
                        loadFragment(news, "layout_news");
                        return true;

                    case R.id.result:
                        Result result = new Result();
                        loadFragment(result, "result");
                        return true;

                    case R.id.calculator:
                        Calculator calculator = new Calculator();
                        loadFragment(calculator, "calculator");
                        return true;
                }
                return false;
            }
        });


        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
            }
        });
    }

    private void toolBarTask() {
        setSupportActionBar(toolbar);
    }

    private void navigationDrawerTask() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationHeaderTask();
    }


    public void navigationHeaderTask() {
        header = navigationView.getHeaderView(0);
        ConstraintLayout layout = header.findViewById(R.id.header_layout);
        final ImageView imageView = header.findViewById(R.id.image);
        final ProgressBar progressBar = header.findViewById(R.id.progressBar);
        final TextView name = header.findViewById(R.id.name);
        final TextView id = header.findViewById(R.id.id);

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        String student_id = sp.getString("id", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
        query.whereEqualTo("student_id", student_id);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    id.setText(object.getString("student_id"));
                    name.setText(object.getString("name"));

                    ParseFile file = (ParseFile) object.get("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null && data != null) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    imageView.setImageBitmap(img);
                                } else {
                                    Toast.makeText(HomeActivity.this, "Some error while loading Image.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        imageView.setImageResource(R.drawable.user_default);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile profile = new Profile();
                if (!isFragmentVisible("profile")) {
                    loadFragmentForDrawer(profile, "profile");
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                        DeleteData deleteData = new DeleteData(HomeActivity.this);
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

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.calendar:
                if (!isFragmentVisible("calendar")) {
                    CommonImageFragment calendar = new CommonImageFragment();
                    bundle.putString("id", "calendar");
                    bundle.putString("title", "Academic Calendar");
                    calendar.setArguments(bundle);
                    loadFragmentForDrawer(calendar, "calendar");
                }
                break;

            case R.id.fees:
                if (!isFragmentVisible("fees")) {
                    CommonImageFragment fees = new CommonImageFragment();
                    bundle.putString("id", "fees");
                    bundle.putString("title", "Fees Structure");
                    fees.setArguments(bundle);
                    loadFragmentForDrawer(fees, "fees");
                }
                break;

            case R.id.syllabus:
                if (!isFragmentVisible("syllabus")) {
                    Syllabus syllabus = new Syllabus();
                    loadFragmentForDrawer(syllabus, "syllabus");
                }
                break;

            case R.id.interact:
                Intent intent = new Intent(HomeActivity.this, InteractActivity.class);
                startActivity(intent);
                break;

            case R.id.schedule:
                if (!isFragmentVisible("schedule")) {
                    CommonImageFragment schedule = new CommonImageFragment();
                    bundle.putString("id", "schedule");
                    bundle.putString("title", "Exam Schedule");
                    schedule.setArguments(bundle);
                    loadFragmentForDrawer(schedule, "schedule");
                }
                break;

            case R.id.seating:
                if (!isFragmentVisible("seating")) {
                    CommonImageFragment seating = new CommonImageFragment();
                    bundle.putString("id", "seating");
                    bundle.putString("title", "Seating Plan");
                    seating.setArguments(bundle);
                    loadFragmentForDrawer(seating, "seating");
                }
                break;

            case R.id.download:
                if (!isFragmentVisible("downloads")) {
                    Downloads downloads = new Downloads();
                    loadFragmentForDrawer(downloads, "downloads");
                }
                break;

            case R.id.upcoming:
                Intent i = new Intent(this, UpcomingEventsActivity.class);
                startActivity(i);
                break;

            case R.id.clubs:
                if(!isFragmentVisible("club")) {
                    Club club = new Club();
                    loadFragmentForDrawer(club,"club");
                }
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Load fragment for bottom navigation view
    public void loadFragment(Fragment fragment, String TAG) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.content, fragment, TAG);
        fragmentTransaction.commit(); // save the changes
    }

    //Load fragment for navigation drawer.
    private void loadFragmentForDrawer(Fragment fragment, String TAG) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        fragmentTransaction.replace(R.id.home_layout, fragment, TAG);
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


}
