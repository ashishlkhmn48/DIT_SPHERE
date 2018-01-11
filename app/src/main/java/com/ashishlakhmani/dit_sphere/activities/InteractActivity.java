package com.ashishlakhmani.dit_sphere.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.ChatAdapter;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InteractActivity extends AppCompatActivity {

    private ConstraintLayout headingLayout;
    private RecyclerView chatListRecyclerView;
    private EditText msg;
    private TextView sorryText;
    private ImageView sorryImage;


    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver online_offline_broadcastReceiver;
    private ArrayList<MessageObject> messageObjectList = new ArrayList<>();
    private ChatAdapter adapter;

    public static LocalChatDatabase chatDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact);

        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        if (getSupportActionBar() != null) {
            setTitle(sp.getString("branch", "").toUpperCase() + " Department");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialize();
        loadMessages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.interact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.backup) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("Backup");
            progressDialog.setMessage("Loading all the Messages.");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Backup");
            builder.setMessage("All your Unsent Messages will not be Sent.\nDo you still want to Load all the Messages ?");

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    progressDialog.show();
                    final SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                    String branch = sharedPreferences.getString("branch", "");

                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(branch);
                    query.orderByAscending("createdAt");
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {
                                chatDatabase.deleteData();
                                for (ParseObject obj : objects) {
                                    String id = obj.getString("student_id");
                                    String message = obj.getString("message");
                                    Date date = obj.getDate("date");
                                    String sendStatus = "";
                                    if (id.equals(sharedPreferences.getString("id", ""))) {
                                        sendStatus = "sent";
                                    }

                                    MessageObject messageObject = new MessageObject(id, message, date.toString(), sendStatus);
                                    chatDatabase.addUserDetails(messageObject);
                                    progressDialog.dismiss();

                                    loadMessages();
                                }
                            } else {
                                Toast.makeText(InteractActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }


        if (id == R.id.delete) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete");
            builder.setMessage("All your Messages will be Deleted.\nDo you still want to Continue ?");

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    chatDatabase.deleteData();
                    loadMessages();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("interact_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isOpen", true);
        editor.apply();

        registerReceiver(broadcastReceiver, new IntentFilter("UPDATE_UI"));
        registerReceiver(online_offline_broadcastReceiver, new IntentFilter("UPDATE_UI_ONLINE_OFFLINE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = getSharedPreferences("interact_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isOpen", false);
        editor.apply();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(online_offline_broadcastReceiver);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences sp = getSharedPreferences("interact_activity", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putBoolean("isOpen", false);
        editor.apply();
    }

    private void initialize() {
        headingLayout = findViewById(R.id.heading_layout);
        msg = findViewById(R.id.messageEditText);
        sorryText = findViewById(R.id.sorryText);
        sorryImage = findViewById(R.id.sorryImage);

        chatDatabase = new LocalChatDatabase(this, null, null, 1);

        chatListRecyclerView = findViewById(R.id.msg_recycler_view);
        adapter = new ChatAdapter(this, messageObjectList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);
        chatListRecyclerView.setAdapter(adapter);

        new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                headingLayout.animate()
                        .translationY(0)
                        .alpha(0.0f)
                        .setDuration(700)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                headingLayout.setVisibility(View.GONE);
                            }
                        });
            }
        }.start();


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadMessages();
            }
        };


        online_offline_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadMessages();
            }
        };

    }


    public void onSendPress(View view) {

        if (!msg.getText().toString().trim().isEmpty()) {
            final SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);

            final String student_id = sp.getString("id", "");
            final String message = msg.getText().toString().trim();
            final Date currentDateTime = new Date();

            //Inserting To Local Database
            MessageObject messageObject = new MessageObject(student_id, message, currentDateTime.toString(), "wait");
            messageObjectList.add(messageObject);
            chatDatabase.addUserDetails(messageObject);
            adapter.notifyDataSetChanged();
            chatListRecyclerView.smoothScrollToPosition(messageObjectList.size() - 1);

            ParseObject parseObject = new ParseObject(sp.getString("branch", ""));
            parseObject.put("student_id", student_id);
            parseObject.put("message", message);
            parseObject.put("date", currentDateTime);
            parseObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {

                        //UI Changes
                        sorryText.setVisibility(View.INVISIBLE);
                        sorryImage.setVisibility(View.INVISIBLE);

                    } else {
                        sorryText.setVisibility(View.VISIBLE);
                        sorryImage.setVisibility(View.VISIBLE);
                        Toast.makeText(InteractActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            msg.setText("");
        }
    }

    private void loadMessages() {

        messageObjectList.clear();
        messageObjectList.addAll(chatDatabase.getMessageObjects());
        adapter.notifyDataSetChanged();
        if (messageObjectList.isEmpty()) {
            sorryText.setVisibility(View.VISIBLE);
            sorryImage.setVisibility(View.VISIBLE);
        } else {
            sorryText.setVisibility(View.INVISIBLE);
            sorryImage.setVisibility(View.INVISIBLE);
            chatListRecyclerView.smoothScrollToPosition(messageObjectList.size() - 1);
        }
    }
}
