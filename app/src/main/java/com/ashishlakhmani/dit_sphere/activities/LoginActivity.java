package com.ashishlakhmani.dit_sphere.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.classes.InsertToDatabase;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private EditText id, password, email;
    private RadioButton radioStudent;
    private CheckBox show;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    onLogin(v);
                }
                return false;
            }
        });
    }


    //Method to initialize Widgets
    private void initialize() {
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.resetEmail);
        show = (CheckBox) findViewById(R.id.show);
        dialog = new ProgressDialog(this);
        radioStudent = findViewById(R.id.radio_student);
    }

    //Login button action.
    public void onLogin(View view) {
        if (isNetworkAvailable()) {
            dialog.setCancelable(false);
            dialog.setTitle("DIT - SPHERE");
            dialog.setMessage("Logging In..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
            login();
        } else {
            Toast.makeText(this, "Please Connect to Internet.", Toast.LENGTH_LONG).show();
        }
    }

    //Reset button action.
    public void onReset(View v) {

        if (checkEmail(this.email.getText().toString().trim())) {

            dialog.setCancelable(false);
            dialog.setTitle("DIT - SPHERE");
            dialog.setMessage("Sending Reset Link..");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();


            ParseUser.requestPasswordResetInBackground(email.getText().toString().trim(), new RequestPasswordResetCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Password Reset Link Sent.", Toast.LENGTH_SHORT).show();
                        email.setText("");
                    } else {
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {

            email.setError("Enter Valid Email ID");
            email.requestFocus();

        }
    }

    //Validate email
    private boolean checkEmail(String email) {
        int dotPos = email.lastIndexOf(".");
        int atPos = email.indexOf("@");

        if (atPos < 1 || dotPos < atPos + 2 || dotPos + 2 >= email.length()) {
            return false;
        }

        return true;
    }

    //CheckBox actions
    public void onShow(View view) {
        if (view.getId() == R.id.show) {
            if (show.isChecked()) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }

    //To hide keyboard on press anywhere
    public void onClick(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //Student & Faculty Login
    private void login() {
        if (checkID() && checkPassword()) {

            //Login to parse server
            ParseUser.logInInBackground(id.getText().toString().toLowerCase().trim(), password.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {

                        String class_name;
                        String id_name;
                        final String email_name;

                        if (radioStudent.isChecked()) {
                            class_name = "Students";
                            id_name = "student_id";
                            email_name = "email";
                        }else {
                            class_name = "Faculty";
                            id_name = "email_id";
                            email_name = id_name;
                        }
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(class_name);
                        query.whereEqualTo(id_name, id.getText().toString().toLowerCase().trim());
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null) {

                                    SharedPreferences sp = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
                                    final String fcm_token = sp.getString(getString(R.string.FCM_TOKEN), "");

                                    InsertToDatabase insertToDatabase = new InsertToDatabase(LoginActivity.this, dialog, id.getText().toString().trim(), password.getText().toString(), object.getString("branch").toLowerCase(),email_name);
                                    insertToDatabase.execute(id.getText().toString().trim(), fcm_token, new Date().toString());

                                } else {
                                    dialog.dismiss();
                                    if (e.getCode() == ParseException.CONNECTION_FAILED)
                                        Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                    else if (e.getCode() == ParseException.OBJECT_NOT_FOUND)
                                        Toast.makeText(LoginActivity.this, "No User Available", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        dialog.dismiss();
                        if (e.getCode() == ParseException.CONNECTION_FAILED)
                            Toast.makeText(LoginActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                        else if (e.getCode() == ParseException.OBJECT_NOT_FOUND)
                            Toast.makeText(LoginActivity.this, "No User Available", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {

            dialog.dismiss();

            if (!checkID()) {
                id.setError("Invalid ID");
                id.requestFocus();
            }

            if (!checkPassword()) {
                password.setError("Password Length too Short");
                password.requestFocus();
            }
        }
    }

    //validate Student ID
    private boolean checkID() {
        if(radioStudent.isChecked()){
            if (id.getText().toString().trim().length() == 10)
                return true;
            else
                return false;
        }else {
            return checkEmail(id.getText().toString().toLowerCase().trim());
        }

    }

    //validate Password
    private boolean checkPassword() {
        if (password.getText().toString().trim().length() >= 5)
            return true;
        else
            return false;
    }

    //To check if the network is available i.e Mobile n/w or wifi
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected())
                return true;
        }
        return false;
    }

}
