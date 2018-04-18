package com.ashishlakhmani.dit_sphere.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.NotificationFaculty;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PushNotificationFacultyActivity extends AppCompatActivity {

    EditText heading, message;
    ImageView image, image_date;
    TextView dateText;
    LinearLayout layout;

    private static final int img_requestCode = 37232;
    private static final int MY_PERMISSIONS_REQUEST = 23242;

    private Uri path;
    private Bitmap img;
    private Calendar myCalendar = Calendar.getInstance();
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_notification_faculty);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialize();

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTask();
            }
        });
    }

    private void dateTask() {

        DatePickerDialog dialog = new DatePickerDialog(PushNotificationFacultyActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                dateText.setText(sdf.format(myCalendar.getTime()));
                date = myCalendar.getTime();
            }
        }, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.setCancelable(false);
        dialog.setTitle("Set Date");
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }


    @Override
    public void onResume() {
        setTitle("Send Notification");
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void initialize() {
        image = findViewById(R.id.image);
        heading = findViewById(R.id.heading);
        message = findViewById(R.id.message);
        dateText = findViewById(R.id.date);
        image_date = findViewById(R.id.image_date);
        layout = findViewById(R.id.layout);
    }

    public void onSend(View view) {
        if (!heading.getText().toString().trim().isEmpty() && !message.getText().toString().trim().isEmpty() && date != null) {

            final ProgressDialog progressDialog = new ProgressDialog(PushNotificationFacultyActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Club Notification");
            progressDialog.setMessage("Please Wait.\nSending Notifications to Club Members.");
            progressDialog.show();

            ParseObject object = new ParseObject("FacultyNotification");

            if (img != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("event.jpg", byteArray);
                object.put("image", file);
            }

            final String heading_txt = heading.getText().toString().trim();
            final String message_txt = message.getText().toString().trim();

            object.put("heading", heading_txt);
            object.put("message", message_txt);
            object.put("date", date);

            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        NotificationFaculty notificationFaculty = new NotificationFaculty(PushNotificationFacultyActivity.this, progressDialog);
                        notificationFaculty.execute(heading_txt, message_txt);
                        reInitialize();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PushNotificationFacultyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else {

            Toast.makeText(this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();

        }


    }

    private void reInitialize() {
        heading.setText("");
        message.setText("");
        dateText.setText("Select Date of Event");
        date = null;
        image.setImageResource(R.drawable.placeholder_album);
        img = null;
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImage();
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void onImage(View view) {

        SharedPreferences sharedPreferences = getSharedPreferences("permission", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (sharedPreferences.getBoolean("granted", true)) {
            selectImage();
        } else {
            if (checkAndRequestPermissions()) {
                editor.putBoolean("granted", true);
                editor.apply();
            }
        }
    }

    private void selectImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, img_requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_requestCode && resultCode == RESULT_OK && data != null) {
            path = data.getData();
            Toast.makeText(this, getMimeType(path), Toast.LENGTH_SHORT).show();
            if (path != null && (getMimeType(path).endsWith("jpeg") || getMimeType(path).endsWith("jpg") || getMimeType(path).endsWith("png"))) {
                try {
                    img = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    image.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Please Select Proper Image Format.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


}
