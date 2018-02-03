package com.example.ramakanta.theanonymousnetwork;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import br.com.simplepass.loading_button_lib.interfaces.OnAnimationEndListener;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CircularProgressButton circularProgressButton;
    private EditText uName,upass;
    private LinearLayout linearLayout;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;
    private String uId;
    private ValueEventListener mListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Checking Login Status...");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        uId = mAuth.getCurrentUser().getUid();
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(uId)) {
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Intent i = new Intent(LoginActivity.this, RegisterOnceActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        circularProgressButton = findViewById(R.id.login_btn);
        uName = findViewById(R.id.ltUserName);
        upass = findViewById(R.id.ltPassword);

        linearLayout = findViewById(R.id.lin_layout);
        circularProgressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uName.getText().toString().equals("") || upass.getText().toString().equals(""))
                {
                    Snackbar snackbar = Snackbar
                            .make(linearLayout , "Fields Cannot Be Blank", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else{
                    @SuppressLint("StaticFieldLeak")
                    AsyncTask<String, String, String> demoDownload = new AsyncTask<String, String, String>() {
                        @Override
                        protected String doInBackground(String... params) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mAuth.signInWithEmailAndPassword(uName.getText().toString(), upass.getText().toString())
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                circularProgressButton.revertAnimation();
                                                circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
                                                hasUserData();
                                            } else {
                                                circularProgressButton.revertAnimation(new OnAnimationEndListener() {
                                                    @Override
                                                    public void onAnimationEnd() {
                                                        Snackbar snackbar = Snackbar
                                                                .make(linearLayout , "Username or Password Mismatch", Snackbar.LENGTH_SHORT);
                                                        snackbar.show();
                                                    }
                                                });
                                                circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
                                            }
                                        }
                                    });
                            return null;
                        }
                        @Override
                        protected void onPostExecute(String s)
                        {
                        }
                    };
                    circularProgressButton.startAnimation();
                    demoDownload.execute();
                }
            }
        });
    }

    private void hasUserData() {
        mDatabase.addValueEventListener(mListener);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        circularProgressButton.setBackgroundResource(R.drawable.btnshape11);
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