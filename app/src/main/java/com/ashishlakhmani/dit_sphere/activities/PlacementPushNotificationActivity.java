package com.ashishlakhmani.dit_sphere.activities;

import android.Manifest;
import android.app.AlertDialog;
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
import com.ashishlakhmani.dit_sphere.classes.NotificationPlacement;
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

public class PlacementPushNotificationActivity extends AppCompatActivity {

    EditText name, details, ctc, post;
    ImageView image, image_date;
    TextView dateText, select_branch;
    LinearLayout layout;

    private static final int img_requestCode = 37245;
    private static final int MY_PERMISSIONS_REQUEST = 24642;

    private Uri path;
    private Bitmap img;
    private Calendar myCalendar = Calendar.getInstance();
    private Date date;

    String[] existingBranches = {"cse", "it", "civil", "petroleum", "ece", "electrical", "mechanical"};
    private ArrayList<String> selectedBranches = new ArrayList<>();
    boolean[] checkedBranches = new boolean[existingBranches.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placement_push_notification);

        initialize();

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTask();
            }
        });

        select_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBranchTask();
            }
        });

    }


    @Override
    public void onResume() {
        setTitle("Placement Push Notification");
        super.onResume();
    }

    private void initialize() {
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        details = findViewById(R.id.details);
        ctc = findViewById(R.id.ctc);
        post = findViewById(R.id.post);
        dateText = findViewById(R.id.date);
        select_branch = findViewById(R.id.select_branch);
        image_date = findViewById(R.id.image_date);
        layout = findViewById(R.id.layout);
    }


    private void selectBranchTask() {

        AlertDialog.Builder builder = new AlertDialog.Builder(PlacementPushNotificationActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Select Branches");

        builder.setMultiChoiceItems(existingBranches, checkedBranches, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedBranches[which] = isChecked;
                if (isChecked)
                    selectedBranches.add(existingBranches[which]);
                else
                    selectedBranches.remove(existingBranches[which]);
            }
        });

        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!selectedBranches.isEmpty())
                    select_branch.setText(selectedBranches.toString().toUpperCase());
                else
                    select_branch.setText("Select Branches");
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void dateTask() {

        DatePickerDialog dialog = new DatePickerDialog(PlacementPushNotificationActivity.this, new DatePickerDialog.OnDateSetListener() {
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

    public void onSend(View view) {
        if (!name.getText().toString().trim().isEmpty() && !details.getText().toString().trim().isEmpty() && !ctc.getText().toString().trim().isEmpty() && !post.getText().toString().trim().isEmpty() && !selectedBranches.isEmpty() && date != null) {

            final ProgressDialog progressDialog = new ProgressDialog(PlacementPushNotificationActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Placement Push Notification");
            progressDialog.setMessage("Please Wait.\nSending Notifications to Students.");
            progressDialog.show();

            final ParseObject object = new ParseObject("Placement");

            if (img != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                ParseFile file = new ParseFile("event.jpg", byteArray);
                object.put("image", file);
            }

            final String company_name_txt = name.getText().toString().trim();
            final String details_txt = details.getText().toString().trim();
            final String ctc_txt = ctc.getText().toString().trim();
            final String post_txt = post.getText().toString().trim();

            object.put("company_name", company_name_txt);
            object.put("details", details_txt);
            object.put("date", date);
            object.put("ctc", ctc_txt + " Lakhs/anum");
            object.put("post", post_txt);
            object.put("branch", selectedBranches);

            String myFormat = "dd MMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            final String date_txt = sdf.format(date);

            object.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        NotificationPlacement notificationPlacement = new NotificationPlacement(PlacementPushNotificationActivity.this, progressDialog);
                        notificationPlacement.execute(object.getObjectId(), company_name_txt, date_txt);
                        reInitialize();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(PlacementPushNotificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            Toast.makeText(this, "Please Fill all the fields.", Toast.LENGTH_SHORT).show();
        }
    }

    private void reInitialize() {
        name.setText("");
        details.setText("");
        ctc.setText("");
        post.setText("");
        dateText.setText("Company Visit Date");
        date = null;
        select_branch.setText("Select Branches");
        selectedBranches.clear();
        image.setImageResource(R.drawable.placeholder_album);
        img = null;


        for (int i = 0; i < existingBranches.length; i++) {
            checkedBranches[i] = false;
        }

    }

    //Permission check
    public boolean checkAndRequestPermissions() {
        int readStoragePermission = ContextCompat.checkSelfPermission(PlacementPushNotificationActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);


        int writeStoragePermission = ContextCompat.checkSelfPermission(PlacementPushNotificationActivity.this,
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
                    Toast.makeText(PlacementPushNotificationActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlacementPushNotificationActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void onImage(View view) {

        SharedPreferences sharedPreferences = PlacementPushNotificationActivity.this.getSharedPreferences("permission", MODE_PRIVATE);
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
            Toast.makeText(PlacementPushNotificationActivity.this, getMimeType(path), Toast.LENGTH_SHORT).show();
            if (path != null && (getMimeType(path).endsWith("jpeg") || getMimeType(path).endsWith("jpg") || getMimeType(path).endsWith("png"))) {
                try {
                    img = MediaStore.Images.Media.getBitmap(PlacementPushNotificationActivity.this.getContentResolver(), path);
                    image.setImageBitmap(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(PlacementPushNotificationActivity.this, "Please Select Proper Image Format.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = PlacementPushNotificationActivity.this.getContentResolver();
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
