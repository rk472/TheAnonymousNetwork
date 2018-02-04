package com.example.ramakanta.theanonymousnetwork;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String uId;
    private ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        ImageView mImage = findViewById(R.id.splash_logo);

        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(mAuth.getCurrentUser() != null) {
                        hasUserData();
                    }else {
                        Intent shareIntent = new Intent(SplashActivity.this , LoginActivity.class);
                        startActivity(shareIntent);
                        finish();
                    }
                }
            }
        };
        timer.start();
    }
    private void hasUserData(){
        uId = mAuth.getCurrentUser().getUid();
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uId)) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(SplashActivity.this, RegisterOnceActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addValueEventListener(mListener);
    }

    @Override
    protected void onStop() {
        if (mListener != null && mDatabase!=null) {
            mDatabase.removeEventListener(mListener);
        }
        super.onStop();
    }
    @Override
    public void onPause() {
        if (mListener != null && mDatabase!=null) {
            mDatabase.removeEventListener(mListener);
        }
        super.onPause();
    }
}
