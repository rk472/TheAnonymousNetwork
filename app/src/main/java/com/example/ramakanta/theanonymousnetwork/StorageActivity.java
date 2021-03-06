package com.example.ramakanta.theanonymousnetwork;

import android.*;
import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Random;

import id.zelory.compressor.Compressor;

import static android.os.Environment.getExternalStorageDirectory;

public class StorageActivity extends AppCompatActivity {
    private DatabaseReference docRef;
    private FirebaseAuth mAuth;
    private  String uid,key;
    private ImageView docImage;
    private TextView docName;
    private Button uploadButton,downloadButton;
    private StorageReference docStore,downloadStore;
    private ProgressDialog mProgress;
    String downloadUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        key = getIntent().getExtras().getString("pos");
        //Toast.makeText(this, key, Toast.LENGTH_SHORT).show();
        docRef= FirebaseDatabase.getInstance().getReference().child("docs").child(uid).child(key);
        docStore= FirebaseStorage.getInstance().getReference().child("docs").child(uid);
        docImage=findViewById(R.id.doc_image);
        docName=findViewById(R.id.doc_name);
        uploadButton=findViewById(R.id.upload_doc);
        downloadButton=findViewById(R.id.download_doc);
        mProgress=new ProgressDialog(this);
        docRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                final String url=dataSnapshot.child("url").getValue().toString();
                downloadUrl=dataSnapshot.child("full_url").getValue().toString();
                docName.setText(name);
                Picasso.with(StorageActivity.this).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.no_image)
                        .into(docImage, new Callback() {
                            @Override
                            public void onSuccess() {

                            }
                            @Override
                            public void onError() {
                                Picasso.with(StorageActivity.this).load(url).placeholder(R.mipmap.no_image).into(docImage);
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(StorageActivity.this);
            }
        });
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(StorageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(StorageActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);
                    return;
                }
                final ProgressDialog dialog=new ProgressDialog(StorageActivity.this);
                downloadStore=FirebaseStorage.getInstance().getReferenceFromUrl(downloadUrl);
                File root = getExternalStorageDirectory();
                File dir=new File(root.getAbsolutePath(),"/TAN");
                dir.mkdirs();
                final File file = new File(dir,key+".jpg");
                downloadStore.getFile(file).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        dialog.setTitle("Downloading..");
                        dialog.setMessage(taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()*100+"%");
                        dialog.show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(StorageActivity.this, "Successfully downloaded to "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StorageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mProgress.setMessage("Wait while We are uploading your Document..");
                mProgress.setTitle("Please Wait");
                mProgress.show();
                Uri resultUri = result.getUri();
                File thumb_filePath=new File(resultUri.getPath());
                Bitmap thumb_bitmap = null;
                try{
                    thumb_bitmap=new Compressor(this)
                            .setQuality(50)
                            .compressToBitmap(thumb_filePath);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
                final byte[] mbyte=byteArrayOutputStream.toByteArray();
                StorageReference filePath=docStore.child("doc").child(key+".jpg");
                final StorageReference thumbFilePath=docStore.child("thumb_doc").child(key+".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(StorageActivity.this, "Saving Your Document...", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumbFilePath.putBytes(mbyte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    final String thumb_downloadUrl = thumb_task.getResult().getDownloadUrl().toString();
                                    docRef.child("url").setValue(downloadUrl);
                                    docRef.child("full_url").setValue(thumb_downloadUrl);
                                    Toast.makeText(StorageActivity.this, "Document uploaded Successfully !", Toast.LENGTH_SHORT).show();

                                }
                            });
                        } else {
                            Toast.makeText(StorageActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        mProgress.dismiss();
                    }
                });
            }
        }
    }
}

