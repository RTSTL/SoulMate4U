package com.rtstl.soulmate4u;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatActivity extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2, reference3;
    String myID = "", opponentID = "";
    Preferences pref;
    ImageView iv_back;
    CircleImageView profile_image;
    TextView tv_name, tv_online_offline;
    String opponentName = "", opponentPic = "";
    DatabaseReference zonesRef, zone1Ref, zone2Ref, zone2OnlineRef, zone1NameRef, zone1PicRef, zone1OnlineRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_chat);

        pref = new Preferences(ChatActivity.this);
        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout) findViewById(R.id.layout2);
        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText) findViewById(R.id.messageArea);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        iv_back = findViewById(R.id.iv_back);
        profile_image = findViewById(R.id.profile_image);
        tv_name = findViewById(R.id.tv_name);
        tv_online_offline = findViewById(R.id.tv_online_offline);

        myID = pref.getStringPreference(ChatActivity.this, "user_id");
        opponentID = GlobalVariable.currentOpponentChatID;

        Firebase.setAndroidContext(this);
        FirebaseApp.initializeApp(ChatActivity.this);

        reference1 = new Firebase("https://soulmate-190205.firebaseio.com/messages/" + myID + "_" + opponentID);
        reference2 = new Firebase("https://soulmate-190205.firebaseio.com/messages/" + opponentID + "_" + myID);

        zonesRef = FirebaseDatabase.getInstance().getReference("users");
        zone1Ref = zonesRef.child(opponentID);
        zone2Ref = zonesRef.child(myID);
        zone1NameRef = zone1Ref.child("name");
        zone1PicRef = zone1Ref.child("pic");
        zone1OnlineRef = zone1Ref.child("isonline");
        zone2OnlineRef = zone2Ref.child("isonline");

        System.out.println("zone1NameRef : " + zone1NameRef);

        zone1NameRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                System.out.println("name value  : " + dataSnapshot.getValue(String.class));
                opponentName = dataSnapshot.getValue(String.class);
                tv_name.setText(opponentName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        zone1PicRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                System.out.println("pic value  : " + dataSnapshot.getValue(String.class));
                opponentPic = dataSnapshot.getValue(String.class);
                Picasso.with(ChatActivity.this).load(opponentPic).into(profile_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        zone1OnlineRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                System.out.println("pic value  : " + dataSnapshot.getValue(String.class));
//                opponentPic = dataSnapshot.getValue(String.class);
                tv_online_offline.setText(dataSnapshot.getValue(String.class));
            }
            //9038510329
            //9874821055

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if (!messageText.equals("")) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", myID);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String myUserID = map.get("user").toString();

                if (myUserID.equals(myID)) {
                    addMessageBox("You\n" + message, 1);
                } else {
                    addMessageBox(opponentName + "\n" + message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addMessageBox(String message, int type) {
        TextView textView = new TextView(ChatActivity.this);
        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if (type == 1) {
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        } else {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        //scrollView.fullScroll(View.FOCUS_DOWN);
        sendScroll();
    }

    @Override
    protected void onResume() {
        super.onResume();
        zone2OnlineRef.setValue("online");
        startService(new Intent(this, BackgroundGpsService.class)); //start service
    }

    @Override
    protected void onPause() {
        super.onPause();
        zone2OnlineRef.setValue("offline");
        stopService(new Intent(this, BackgroundGpsService.class)); //stop service

    }

    private void sendScroll() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        }).start();
    }
}