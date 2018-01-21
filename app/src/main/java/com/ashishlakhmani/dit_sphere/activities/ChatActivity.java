package com.ashishlakhmani.dit_sphere.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashishlakhmani.dit_sphere.R;
import com.ashishlakhmani.dit_sphere.adapters.ChatAdapter;
import com.ashishlakhmani.dit_sphere.classes.LocalChatDatabase;
import com.ashishlakhmani.dit_sphere.classes.MessageObject;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ConstraintLayout headingLayout;
    private RecyclerView chatListRecyclerView;
    private EditText msg;
    private TextView sorryText;
    private ImageView sorryImage;


    private BroadcastReceiver broadcastReceiver;
    private ArrayList<MessageObject> messageObjectList = new ArrayList<>();
    private ChatAdapter adapter;

    private String object_id;
    private int num_connected_users;

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

        if (id == R.id.users_connected) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Connected Users");
            progressDialog.setMessage("Please Wait..\nFetching Connected Users.");
            progressDialog.show();

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Threads");
            query.getInBackground(object_id, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {

                        List<String> list = object.getList("connected_id");
                        final String[] connected_users = list.toArray(new String[list.size()]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("Connected Users");
                        builder.setMessage("\nTotal Users : " + connected_users.length);
                        builder.setIcon(R.drawable.interact_3);
                        View view = getLayoutInflater().inflate(R.layout.layout_connected_users, null);
                        ListView listView = view.findViewById(R.id.connected_users_list);
                        ListAdapter adapter = new ArrayAdapter<Object>(ChatActivity.this, android.R.layout.simple_list_item_1, connected_users);
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                String student_id = connected_users[position];
                                fetchStudentDetails(student_id);
                            }
                        });

                        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.setView(view);
                        alertDialog.show();

                    } else {
                        Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }


        if (id == R.id.delete) {

            final LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.delete);
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


    private void fetchStudentDetails(String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Profile");
        progressDialog.setMessage("Loading Profile..");
        progressDialog.show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Students");
        query.whereEqualTo("student_id", id);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {

                    final String id = object.getString("student_id");
                    String name = object.getString("name").toUpperCase();
                    String branch = object.getString("branch").toUpperCase();
                    String year = object.getString("year");

                    if (year.equals("1")) {
                        year += "st Year";
                    } else if (year.equals("2")) {
                        year += "nd Year";
                    } else if (year.equals("3")) {
                        year += "rd Year";
                    } else {
                        year += "th Year";
                    }

                    final View view = getLayoutInflater().inflate(R.layout.layout_profile_alert, null);
                    TextView id_tv, name_tv, branch_tv, year_tv;
                    final ImageView picture;

                    id_tv = view.findViewById(R.id.id);
                    name_tv = view.findViewById(R.id.name);
                    branch_tv = view.findViewById(R.id.branch);
                    year_tv = view.findViewById(R.id.year);
                    picture = view.findViewById(R.id.picture);

                    id_tv.setText(id);
                    name_tv.setText(name);
                    branch_tv.setText(branch);
                    year_tv.setText(year);

                    final ParseFile file = object.getParseFile("picture");
                    if (file != null) {
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    picture.setImageBitmap(img);

                                    progressDialog.dismiss();
                                    Dialog alertDialog = new Dialog(ChatActivity.this);
                                    alertDialog.setCancelable(true);
                                    alertDialog.setContentView(view);
                                    alertDialog.show();
                                    Window window = alertDialog.getWindow();
                                    window.setLayout(1000, 1100);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {

                        progressDialog.dismiss();
                        Dialog alertDialog = new Dialog(ChatActivity.this);
                        alertDialog.setCancelable(true);
                        alertDialog.setContentView(view);
                        alertDialog.show();
                        Window window = alertDialog.getWindow();
                        window.setLayout(1000, 1100);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
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

        object_id = getIntent().getStringExtra("object_id");

        headingLayout = findViewById(R.id.heading_layout);
        msg = findViewById(R.id.messageEditText);
        sorryText = findViewById(R.id.sorryText);
        sorryImage = findViewById(R.id.sorryImage);

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
                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Threads");
                query.getInBackground(object_id, new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            num_connected_users = object.getList("connected_id").size();
                        } else {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                loadMessages();
            }
        };

    }


    public void onSendPress(View view) {

        final LocalChatDatabase chatDatabase = new LocalChatDatabase(this, object_id);

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
