package com.example.ramakanta.theanonymousnetwork;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView nameProf,livesProf,joinedProf,rollProf,bioProf,genderProf,dobProf,mailProf,phoneProf,navName,navEmail;
    private CircleImageView logoProf,navImage;
    private DatabaseReference mDatabase;
    private String uId;
    private ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Loading your Profile");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

        //Assign all fields
        nameProf = findViewById(R.id.name_profile);
        rollProf = findViewById(R.id.roll_profile);
        livesProf = findViewById(R.id.lives_in_profile);
        joinedProf = findViewById(R.id.joined_imca_profile);
        bioProf = findViewById(R.id.bio_profile);
        genderProf = findViewById(R.id.gender_profile);
        dobProf = findViewById(R.id.dob_profile);
        mailProf = findViewById(R.id.mail_profile);
        phoneProf = findViewById(R.id.phone_profile);
        logoProf = findViewById(R.id.logo_profile);
        NavigationView navView=findViewById(R.id.nav_view);
        View header=navView.getHeaderView(0);
        navImage=header.findViewById(R.id.nav_image);
        navName=header.findViewById(R.id.nav_name);
        navEmail=header.findViewById(R.id.nav_email);

        //database retrieval
        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("u_name").getValue().toString();
                String lives=dataSnapshot.child("u_lives").getValue().toString();
                String joined=dataSnapshot.child("u_joined").getValue().toString();
                String phone=dataSnapshot.child("u_phone").getValue().toString();
                String mail=dataSnapshot.child("u_email").getValue().toString();
                String dob=dataSnapshot.child("u_dob").getValue().toString();
                String gender=dataSnapshot.child("u_gender").getValue().toString();
                String bio=dataSnapshot.child("u_bio").getValue().toString();
                final String thumb_image=dataSnapshot.child("u_thumb_image").getValue().toString();
                String roll=dataSnapshot.child("u_roll").getValue().toString();

                nameProf.setText(name);
                livesProf.setText(lives);
                joinedProf.setText(joined);
                phoneProf.setText(phone);
                mailProf.setText(mail);
                dobProf.setText(dob);
                genderProf.setText(gender);
                bioProf.setText(bio);
                rollProf.setText(roll);
                navName.setText(name);
                navEmail.setText(mail);

                Picasso.with(ProfileActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(logoProf, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(thumb_image).placeholder(R.drawable.logo_def).into(logoProf);
                            }
                        });
                mProgress.dismiss();
                Picasso.with(ProfileActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(navImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {
                                Picasso.with(ProfileActivity.this).load(thumb_image).placeholder(R.drawable.logo_def).into(navImage);
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //drawer initialization
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {

            new AlertDialog.Builder(this).setTitle("Log Out")
                    .setMessage("Do you really want to logout")
                    .setPositiveButton("Yes, Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Intent i = new Intent(ProfileActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();

                        }
                    })
                    .setNegativeButton("No , Don't" , null)
            .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
