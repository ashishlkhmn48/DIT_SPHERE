package com.ashishlakhmani.dit_sphere.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.FacultyData;
import com.squareup.picasso.Picasso;

public class FacultyDetailsActivity extends AppCompatActivity {

    TextView name, contact, email, location, specialization, branch;
    ImageView imageView;
    Toolbar toolbar;
    ImageView email_image, call_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_details);


        initialize();
        toolbarTask("Faculty Details");
        setFacultyDetails();

        email_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email.getText().toString()));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(FacultyDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        call_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+contact.getText().toString()));
                startActivity(intent);
            }
        });
    }

    

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialize() {
        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        email = findViewById(R.id.email);
        location = findViewById(R.id.location);
        specialization = findViewById(R.id.specialization);
        branch = findViewById(R.id.branch);
        imageView = findViewById(R.id.image);
        toolbar = findViewById(R.id.toolbar);
        email_image = findViewById(R.id.email_image);
        call_image = findViewById(R.id.call_image);
    }

    private void toolbarTask(String title) {
        setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setFacultyDetails() {
        FacultyData facultyData = (FacultyData) getIntent().getExtras().getSerializable("details");
        name.setText(facultyData.getName().toUpperCase());
        contact.setText(facultyData.getContactNum());
        email.setText(facultyData.getEmailId());
        branch.setText(facultyData.getBranch().toUpperCase());
        location.setText(facultyData.getLocation().toUpperCase());
        specialization.setText(facultyData.getSpecialization().toString());

        if (facultyData.getImageUrl() != null) {
            Picasso.with(this)
                    .load(facultyData.getImageUrl())
                    .placeholder(R.drawable.placeholder_album)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.user_default);
        }
    }

}
