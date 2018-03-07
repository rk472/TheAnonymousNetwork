package com.example.ramakanta.theanonymousnetwork;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private CircleImageView navImage;
    private TextView navName,navEmail;
    private DatabaseReference mDatabase;
    private String uId;
    private ProgressDialog mProgress;
    private Bitmap thumb_bitmap;
    private StorageReference storeProfileImage,storeProfileThumbImage;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);
        navigationView.setCheckedItem(R.id.nav_profile);
        View header=navigationView.getHeaderView(0);
        navImage=header.findViewById(R.id.nav_image);
        navName=header.findViewById(R.id.nav_name);
        navEmail=header.findViewById(R.id.nav_email);
        fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ProfileFragment());
        fragmentTransaction.commit();
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        storeProfileImage= FirebaseStorage.getInstance().getReference().child("user_profile_image");
        storeProfileThumbImage=FirebaseStorage.getInstance().getReference().child("user_profile_thumb_image");

        mProgress=new ProgressDialog(this);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                navName.setText(dataSnapshot.child("u_name").getValue().toString());
                navEmail.setText(dataSnapshot.child("u_email").getValue().toString());
                final String thumb_image=dataSnapshot.child("u_thumb_image").getValue().toString();
                Picasso.with(MainActivity.this).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(navImage, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(MainActivity.this).load(thumb_image).placeholder(R.drawable.logo_def).into(navImage);
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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
                            Intent i = new Intent(MainActivity.this,LoginActivity.class);
                            startActivity(i);
                            finish();
                        }
                    })
                    .setNegativeButton("No , Don't" , null)
            .show();
            return true;
        }else if(id==R.id.action_settings){
            Intent i=new Intent(this,SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment f = null;
        if(id==R.id.nav_settings){
            Intent i=new Intent(this,SettingsActivity.class);
            startActivity(i);
        }else {
            switch (id) {
                case R.id.nav_home:
                    f = new HomeFragment();
                    break;
                case R.id.nav_profile:
                    f = new ProfileFragment();
                    break;
                case R.id.nav_message:
                    f = new ChatFragment();
                    break;
                case R.id.nav_storage:
                    f = new StorageFragment();
                    break;
                case R.id.nav_attendance:
                    f = new AttendanceFragment();
                    break;
                case R.id.nav_time_table:
                    f = new TimeTableFragment();
                    break;

            }
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, f);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress.setMessage("Wait while We are updating your Profile Picture..");
                mProgress.setTitle("Please Wait");
                mProgress.show();
                Uri resultUri = result.getUri();
                String uid=mAuth.getCurrentUser().getUid();
                File thumb_filePath=new File(resultUri.getPath());
                try{
                    thumb_bitmap=new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxHeight(200)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePath);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] mbyte=byteArrayOutputStream.toByteArray();
                StorageReference filePath=storeProfileImage.child(uid+".jpg");
                final StorageReference thumbFilePath=storeProfileThumbImage.child(uid+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Saving Your Profile picture...",Toast.LENGTH_SHORT).show();
                            final String downloadUrl=task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask=thumbFilePath.putBytes(mbyte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    final String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                    mDatabase.child("u_image").setValue(downloadUrl);
                                    mDatabase.child("u_thumb_image").setValue(thumb_downloadUrl);
                                    Toast.makeText(MainActivity.this, "Profile Picture Updated Successfully !", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }else{
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        mProgress.dismiss();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(MainActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
