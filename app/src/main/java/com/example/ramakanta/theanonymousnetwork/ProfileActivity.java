package com.example.ramakanta.theanonymousnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView nameProf, livesProf, joinedProf, rollProf, bioProf, genderProf, dobProf, mailProf, phoneProf;
    private CircleImageView logoProf;
    private DatabaseReference mDatabase;
    private String uId;
    private ProgressDialog mProgress;
    private ImageButton call, mail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Loading your Profile");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        uId = getIntent().getExtras().getString("uid");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        //Assign all fields
        nameProf = findViewById(R.id.name_profile_other);
        rollProf = findViewById(R.id.roll_profile_other);
        livesProf = findViewById(R.id.lives_in_profile_other);
        joinedProf = findViewById(R.id.joined_imca_profile_other);
        bioProf = findViewById(R.id.bio_profile_other);
        genderProf = findViewById(R.id.gender_profile_other);
        dobProf = findViewById(R.id.dob_profile_other);
        mailProf = findViewById(R.id.mail_profile_other);
        phoneProf = findViewById(R.id.phone_profile_other);
        logoProf = findViewById(R.id.logo_profile_other);
        call = findViewById(R.id.call_other);
        mail = findViewById(R.id.mail_other);

        mDatabase.keepSynced(true);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("u_name").getValue().toString();
                String lives = dataSnapshot.child("u_lives").getValue().toString();
                String joined = dataSnapshot.child("u_joined").getValue().toString();
                String phone = dataSnapshot.child("u_phone").getValue().toString();
                String mail = dataSnapshot.child("u_email").getValue().toString();
                String dob = dataSnapshot.child("u_dob").getValue().toString();
                String gender = dataSnapshot.child("u_gender").getValue().toString();
                String bio = dataSnapshot.child("u_bio").getValue().toString();
                final String thumb_image = dataSnapshot.child("u_thumb_image").getValue().toString();
                String roll = dataSnapshot.child("u_roll").getValue().toString();

                nameProf.setText(name);
                livesProf.setText(lives);
                joinedProf.setText(joined);
                phoneProf.setText(phone);
                mailProf.setText(mail);
                dobProf.setText(dob);
                genderProf.setText(gender);
                bioProf.setText(bio);
                rollProf.setText(roll);


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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String no = phoneProf.getText().toString();
                String uri = "tel:" + no;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                if (ActivityCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            1);
                    return;
                }
                startActivity(intent);

            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              String mail=mailProf.getText().toString();
                Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/html");
                i.putExtra(Intent.EXTRA_EMAIL,mail);
                startActivity(Intent.createChooser(i, "Send Email"));
            }
        });
    }
}
