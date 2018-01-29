package com.example.ramakanta.theanonymousnetwork;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private FirebaseAuth mAuth;
    private TextView nameProf,livesProf,joinedProf,rollProf,bioProf,genderProf,dobProf,mailProf,phoneProf;
    private CircleImageView logoProf;
    private DatabaseReference mDatabase;
    private String uId;
    private ProgressDialog mProgress;
    private ImageButton uploadImage;
    private AppCompatActivity main;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Profile");
        View root=inflater.inflate(R.layout.fragment_profile, container, false);
        mProgress = new ProgressDialog(main);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Loading your Profile");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        //Assign all fields
        nameProf = root.findViewById(R.id.name_profile);
        rollProf = root.findViewById(R.id.roll_profile);
        livesProf = root.findViewById(R.id.lives_in_profile);
        joinedProf = root.findViewById(R.id.joined_imca_profile);
        bioProf = root.findViewById(R.id.bio_profile);
        genderProf = root.findViewById(R.id.gender_profile);
        dobProf = root.findViewById(R.id.dob_profile);
        mailProf = root.findViewById(R.id.mail_profile);
        phoneProf = root.findViewById(R.id.phone_profile);
        logoProf = root.findViewById(R.id.logo_profile);

        uploadImage=root.findViewById(R.id.upload_prof);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(main);
            }
        });
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


                Picasso.with(main).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(logoProf, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {
                                Picasso.with(main).load(thumb_image).placeholder(R.drawable.logo_def).into(logoProf);
                            }
                        });
                mProgress.dismiss();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        FloatingActionButton fab =  main.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //drawer initialization


        return root;
    }



}
