package com.example.ramakanta.theanonymousnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AddPostActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase,mUser;
    private ProgressDialog mProgress;
    private Bitmap thumb_bitmap;
    private EditText mCaption;
    private String uId;
    private ImageButton mImageButton;
    private RelativeLayout mContainer;
    private byte[] mbyte;
    private ImageView mImage;
    private CircleImageView mProfilePic;
    private String thumb_downloadUrl ;
    private StorageReference storeProfileThumbImage,thumbFilePath;
    private Map data = new HashMap();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mProgress=new ProgressDialog(this);
        mImage = findViewById(R.id.image_uploaded);
        mImageButton = findViewById(R.id.cancel_image_btn);
        mContainer = findViewById(R.id.image_container);
        mCaption = findViewById(R.id.post_content);
        mAuth = FirebaseAuth.getInstance();
        mProfilePic = findViewById(R.id.add_post_profile);
        uId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("posts");
        mUser = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        mUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String url = dataSnapshot.child("u_thumb_image").getValue().toString();
                Picasso.with(AddPostActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(mProfilePic, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {
                                Picasso.with(AddPostActivity.this).load(url).placeholder(R.drawable.logo_def).into(mProfilePic);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        storeProfileThumbImage=FirebaseStorage.getInstance().getReference().child("post_images");
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContainer.getVisibility()==View.VISIBLE)
                {
                    mContainer.setVisibility(View.INVISIBLE);
                }else{
                    mContainer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void dismis_dialog(View view) {
        finish();
    }

    public void select_image(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(AddPostActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress.setMessage("Please wait getting the Image ...");
                mProgress.setTitle("Please Wait");
                mProgress.show();
                Uri resultUri = result.getUri();
                File thumb_filePath=new File(resultUri.getPath());
                try{
                    thumb_bitmap=new Compressor(this)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePath);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,70,byteArrayOutputStream);
                mbyte=byteArrayOutputStream.toByteArray();
                mProgress.dismiss();
                mContainer.setVisibility(View.VISIBLE);
                mImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddPostActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void create_a_post(View view) {
        final String caption = mCaption.getText().toString();
        if(TextUtils.isEmpty(caption)){
            Toast.makeText(this, "Caption can't be blank...", Toast.LENGTH_SHORT).show();
        }else {
            mProgress.setMessage("Creating Post...");
            mProgress.setTitle("Please Wait");
            mProgress.show();
            final String pId = mDatabase.push().getKey();
            data.put("p_caption", caption);
            data.put("p_user", uId);
            data.put("p_like", 0);
            data.put("p_order", -1 * new Date().getTime());
            data.put("p_time", ServerValue.TIMESTAMP);
            if (mContainer.getVisibility() == View.VISIBLE) {
                thumbFilePath = storeProfileThumbImage.child(pId + ".jpg");
                UploadTask uploadTask = thumbFilePath.putBytes(mbyte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                        thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                        data.put("p_image", thumb_downloadUrl);
                        mDatabase.child(pId).updateChildren(data, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                DatabaseReference likesRef=FirebaseDatabase.getInstance().getReference().child("likes").child(pId).child("demo");
                                likesRef.setValue(0);
                                Toast.makeText(AddPostActivity.this, "Post Created Successfully...", Toast.LENGTH_SHORT).show();
                                mProgress.dismiss();
                                finish();
                            }
                        });
                    }
                });
            }else{
                data.put("p_image", "no_image");
                mDatabase.child(pId).updateChildren(data, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Toast.makeText(AddPostActivity.this, "Post Created Successfully...", Toast.LENGTH_SHORT).show();
                        DatabaseReference likesRef=FirebaseDatabase.getInstance().getReference().child("likes").child(pId).child("demo");
                        likesRef.setValue(0);
                        mProgress.dismiss();
                        finish();
                    }
                });
            }
        }
    }
}

