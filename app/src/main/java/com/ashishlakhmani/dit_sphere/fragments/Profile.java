package com.ashishlakhmani.dit_sphere.fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.activities.HomeActivity;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class Profile extends Fragment {

    private TextView id, name, gender;
    private EditText dob, address, email, contact;
    private Button save;
    private ImageButton edit, remove;
    private ImageView profilePic, calendar_button;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private ProgressDialog dialog;

    private Bitmap img;
    private Calendar myCalendar;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private static final int img_requestCode = 13;


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ((HomeActivity) getActivity()).setToolbarTitle("Profile");


        initialize(view);
        loadProfileData();
        buttonTask();
        frameLayoutButtonTask();

        return view;
    }


    //Initialize widgets
    private void initialize(View view) {

        id = view.findViewById(R.id.id);
        name = view.findViewById(R.id.name);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dob);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.gmail);
        contact = view.findViewById(R.id.contact);
        edit = view.findViewById(R.id.edit);
        remove = view.findViewById(R.id.remove);
        save = view.findViewById(R.id.save);
        profilePic = view.findViewById(R.id.profile_pic);
        calendar_button = view.findViewById(R.id.calendar_button);
        progressBar = view.findViewById(R.id.profile_progressbar);
        layout = view.findViewById(R.id.profile_layout);
        dialog = new ProgressDialog(getContext());
        myCalendar = Calendar.getInstance();

    }

    //To load profile data from server.
    private void loadProfileData() {

        SharedPreferences sp = getContext().getSharedPreferences("login", MODE_PRIVATE);
        String student_id = sp.getString("id", "");
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
        query.whereEqualTo("student_id", student_id);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    id.setText(object.getString("student_id"));
                    name.setText(object.getString("name"));
                    gender.setText(object.getString("gender"));
                    dob.setText(object.getString("dob"));
                    address.setText(object.getString("address"));
                    email.setText(object.getString("email"));
                    contact.setText(object.getString("contact"));

                    ParseFile file = (ParseFile) object.get("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    layout.setVisibility(View.VISIBLE);
                                    img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profilePic.setImageBitmap(img);
                                } else {
                                    Toast.makeText(getContext(), "Some error while loading Image.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    layout.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        layout.setVisibility(View.VISIBLE);
                        profilePic.setImageResource(R.drawable.user_default);
                    }
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    layout.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //To assign tasks to buttons
    private void buttonTask() {

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (homeActivity.checkAndRequestPermissions()) {
                    getImage();
                }
            }
        });


        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePic.setImageResource(R.drawable.user_default);
                img = null;
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }


    //To assign tasks to editTexts
    private void frameLayoutButtonTask() {

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd-MM-yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                dob.setText(sdf.format(myCalendar.getTime()));
            }

        };


        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
                if (inputMethodManager != null && getActivity().getCurrentFocus() != null) {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }


    //to get image from storage
    private void getImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, img_requestCode);
    }


    //To save back the changes to the server.
    private void saveTask() {

        if (checkDob() && checkAddress() && checkEmail() && checkContact()) {

            dialog.setCancelable(false);
            dialog.setTitle("DIT - SPHERE");
            dialog.setMessage("Saving Details..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();

            SharedPreferences sp = getContext().getSharedPreferences("login", MODE_PRIVATE);
            String student_id = sp.getString("id", "");
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
            query.whereEqualTo("student_id", student_id);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {

                        object.put("dob", dob.getText().toString());
                        object.put("address", address.getText().toString());
                        object.put("email", email.getText().toString());
                        object.put("contact", contact.getText().toString());

                        if (img != null) {
                            //To put ParseFile object..Here we are putting an image.
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] arr = stream.toByteArray();
                            ParseFile file = new ParseFile(object.getString("student_id"), arr);
                            object.put("picture", file);
                        } else {
                            object.remove("picture");
                        }

                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                dialog.dismiss();
                                android.app.AlertDialog alertDialog;
                                alertDialog = new android.app.AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("Status");
                                alertDialog.setCancelable(true);
                                alertDialog.setMessage("Details Saved Successfully.");
                                alertDialog.show();


                                //Navigation Header Tasks
                                ((HomeActivity) getActivity()).navigationHeaderTask();

                                dob.setError(null);
                                address.setError(null);
                                email.setError(null);
                                contact.setError(null);
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean checkDob() {
        String str = dob.getText().toString().trim();
        if (!str.isEmpty() && str.indexOf("-") == 2 && str.lastIndexOf("-") == 5 && str.length() == 10)
            return true;
        else {
            dob.setError("Invalid D.O.B!!");
            dob.requestFocus();
            return false;
        }
    }

    //validate Email ID
    private boolean checkEmail() {

        String str = email.getText().toString().trim();
        int atposition = str.indexOf("@");
        int dotposition = str.lastIndexOf(".");
        if (!email.getText().toString().trim().isEmpty() && (atposition < 1 || dotposition < atposition + 2 || dotposition + 2 >= str.length())) {
            contact.setError("Invalid Email ID..!!");
            contact.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean checkAddress() {
        if (!address.getText().toString().trim().isEmpty())
            return true;
        else {
            address.setError("Invalid Address!!");
            address.requestFocus();
            return false;
        }
    }

    private boolean checkContact() {
        if (contact.getText().toString().trim().length() == 10 &&
                (contact.getText().toString().trim().startsWith("7") ||
                        contact.getText().toString().trim().startsWith("8") ||
                        contact.getText().toString().trim().startsWith("9")) && isNumber())
            return true;
        else {
            contact.setError("Invalid Phone Number..!!");
            contact.requestFocus();
            return false;
        }
    }

    public boolean isNumber() {
        for (int i = 0; i < contact.getText().toString().trim().length(); i++) {
            if (!(contact.getText().toString().trim().charAt(i) >= 48 && contact.getText().toString().trim().charAt(i) <= 57))
                return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == img_requestCode && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            //String path = getRealPathFromURI(uri);
            if (uri != null) {

                String type = getMimeType(uri);
                //Toast.makeText(getContext(), type, Toast.LENGTH_LONG).show();

                if (type.endsWith("jpeg") || type.endsWith("jpg") || type.endsWith("png")) {
                    try {
                        img = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        profilePic.setImageBitmap(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Please select an Image.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getContext().getContentResolver();
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