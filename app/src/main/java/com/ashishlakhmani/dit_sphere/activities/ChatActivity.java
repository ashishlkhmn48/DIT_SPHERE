package com.ashishlakhmani.dit_sphere.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
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

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.ChatAdapter;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    private ConstraintLayout headingLayout;
    private RecyclerView chatListRecyclerView;
    private EditText msg;
    private TextView sorryText;
    private ImageView sorryImage;


    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver online_offline_broadcastReceiver;
    private ArrayList<MessageObject> messageObjectList = new ArrayList<>();
    private ChatAdapter adapter;

    private String object_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initialize();
        loadMessages();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.interact_overflow_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {

            final LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);

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

        setTitle(getIntent().getStringExtra("heading"));

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

        chatListRecyclerView = findViewById(R.id.msg_recycler_view);
        adapter = new ChatAdapter(this, messageObjectList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        chatListRecyclerView.setLayoutManager(linearLayoutManager);
        chatListRecyclerView.setAdapter(adapter);

        object_id = getIntent().getStringExtra("object_id");

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

        LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);

        if (!msg.getText().toString().trim().isEmpty()) {
            final SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
            final String student_id = sp.getString("id", "");
            final String message = msg.getText().toString().trim();
            final Date currentDateTime = new Date();

            //Inserting To Local Database
            MessageObject messageObject = new MessageObject(object_id, student_id, message, currentDateTime.toString(), "wait");
            messageObjectList.add(messageObject);
            chatDatabase.addUserDetails(messageObject);
            adapter.notifyItemInserted(messageObjectList.size() - 1);
            chatListRecyclerView.smoothScrollToPosition(messageObjectList.size() - 1);

            sorryText.setVisibility(View.INVISIBLE);
            sorryImage.setVisibility(View.INVISIBLE);
            msg.setText("");
        }
    }

    private void loadMessages() {

        LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);
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

    public String getHeading() {
        return getIntent().getStringExtra("heading");
    }

}
