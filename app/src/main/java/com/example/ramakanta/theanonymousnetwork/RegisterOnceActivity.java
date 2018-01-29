package com.example.ramakanta.theanonymousnetwork;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class RegisterOnceActivity extends AppCompatActivity {

    private CircleImageView image_reg;
    private CircularProgressButton circularProgressButton;
    private Calendar myCalendar;
    private EditText dob_reg,name_reg,roll_reg,lives_reg,phone_reg,email_reg,joined_reg,bio_reg;
    private String s_dob_reg,s_name_reg,s_roll_reg,s_lives_reg,s_phone_reg,s_email_reg,s_joined_reg,s_bio_reg,s_gender_reg="Select Your Gender";
    private String downloadUrl,thumb_downloadUrl;
    private Spinner gender_reg;
    private ArrayAdapter<CharSequence> adapter;
    private DatePickerDialog.OnDateSetListener date_pick;
    private LinearLayout linearLayout;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private StorageReference storeProfileImage;
    private StorageReference storeProfileThumbImage;
    private Bitmap thumb_bitmap;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_once);
        //initialization
        circularProgressButton = findViewById(R.id.btn_reg);
        linearLayout = findViewById(R.id.register_layout);
        dob_reg = findViewById(R.id.dob_reg);
        roll_reg = findViewById(R.id.roll_reg);
        name_reg = findViewById(R.id.name_reg);
        lives_reg = findViewById(R.id.lives_reg);
        phone_reg = findViewById(R.id.phone_reg);
        email_reg = findViewById(R.id.email_reg);
        joined_reg = findViewById(R.id.joined_reg);
        bio_reg = findViewById(R.id.bio_reg);
        gender_reg = findViewById(R.id.gender_reg);
        image_reg=findViewById(R.id.profile_pic_reg);
        loadingBar=new ProgressDialog(this);
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.setCancelable(false);
        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        storeProfileImage= FirebaseStorage.getInstance().getReference().child("user_profile_image");
        storeProfileThumbImage=FirebaseStorage.getInstance().getReference().child("user_profile_thumb_image");
        //Spinner Item Insertion
        adapter = ArrayAdapter.createFromResource(this , R.array.gender_select,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender_reg.setAdapter(adapter);
        gender_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                s_gender_reg = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //Select the Date
        myCalendar= Calendar.getInstance();
        date_pick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        //select image
        image_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(RegisterOnceActivity.this);
            }
        });
        //Submit button click event
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractAllData();
                @SuppressLint("StaticFieldLeak")
                AsyncTask<String, String, String> demoDownload = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        if(validateAllFields()) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Map update_user_data=new HashMap();
                            update_user_data.put("u_image",downloadUrl);
                            update_user_data.put("u_thumb_image",thumb_downloadUrl);
                            update_user_data.put("u_name",s_name_reg);
                            update_user_data.put("u_roll",s_roll_reg);
                            update_user_data.put("u_lives",s_lives_reg);
                            update_user_data.put("u_joined",s_joined_reg);
                            update_user_data.put("u_phone",s_phone_reg);
                            update_user_data.put("u_email",s_email_reg);
                            update_user_data.put("u_dob",s_dob_reg);
                            update_user_data.put("u_gender",s_gender_reg);
                            update_user_data.put("u_bio",s_bio_reg);
                            userRef.updateChildren(update_user_data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Snackbar snackbar = Snackbar
                                            .make(linearLayout , "Successfully Registered !", Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                    Intent i=new Intent(RegisterOnceActivity.this,MainActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(String s)
                    {
                            circularProgressButton.revertAnimation();
                            circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
                    }
                };
                circularProgressButton.startAnimation();
                demoDownload.execute();
            }
        });
    }
    private void extractAllData() {
        s_name_reg=name_reg.getText().toString();
        s_lives_reg=lives_reg.getText().toString();
        s_dob_reg=dob_reg.getText().toString();
        s_roll_reg=roll_reg.getText().toString();
        s_phone_reg=phone_reg.getText().toString();
        s_email_reg=email_reg.getText().toString();
        s_joined_reg=joined_reg.getText().toString();
        s_bio_reg=bio_reg.getText().toString();
    }
    private boolean validateAllFields() {
        if(s_name_reg.equals("")||s_gender_reg.equals("Select Your Gender")||s_lives_reg.equals("")||s_dob_reg.equals("")||s_phone_reg.equals("")||s_email_reg.equals("")||s_joined_reg.equals("")||s_bio_reg.equals("")) {
            Snackbar snackbar = Snackbar
                    .make(linearLayout , "Fields Cannot Be Blank", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }else if(downloadUrl==null ||thumb_downloadUrl==null){
            Snackbar snackbar = Snackbar
                    .make(linearLayout , "You Must Select A Profile Picture", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
        return true;
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
    }
    public void otc(View v) {
        new DatePickerDialog(RegisterOnceActivity.this, date_pick, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dob_reg.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.register_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_reg) {

            new AlertDialog.Builder(this).setTitle("Log Out")
                    .setMessage("Do you really want to logout")
                    .setPositiveButton("Yes, Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mAuth.signOut();
                            Intent i = new Intent(RegisterOnceActivity.this,LoginActivity.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingBar.setMessage("Wait while We are updating your Profile Picture..");
                loadingBar.setTitle("Please Wait");
                loadingBar.show();
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
                            Toast.makeText(RegisterOnceActivity.this,"Saving Your Profile picture...",Toast.LENGTH_SHORT).show();
                            downloadUrl=task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask=thumbFilePath.putBytes(mbyte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                    Picasso.with(RegisterOnceActivity.this).load(thumb_downloadUrl).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.vector_add_photo)
                                            .into(image_reg, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                }
                                                @Override
                                                public void onError() {
                                                    Picasso.with(RegisterOnceActivity.this).load(thumb_downloadUrl).placeholder(R.drawable.vector_add_photo).into(image_reg);
                                                }
                                            });
                                }
                            });
                        }else{
                            Toast.makeText(RegisterOnceActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                        loadingBar.dismiss();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(RegisterOnceActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}