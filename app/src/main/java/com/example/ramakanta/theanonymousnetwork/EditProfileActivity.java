package com.example.ramakanta.theanonymousnetwork;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private EditText cityEdit,dobEdit,phoneEdit,mailEdit,bioEdit;
    private String cityString,dobString,phoneString,mailString,bioString,uId;
    private DatabaseReference mUserEdit;
    private FirebaseAuth mAuth;
    private Button save_btn,cancel_btn;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date_pick;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        //Progresss Dialog
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Please Wait");
        mProgress.setMessage("Loading the Datas");
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        //Edit Texts initialization
        dobEdit = findViewById(R.id.dob_edprof);
        cityEdit = findViewById(R.id.lives_edprof);
        phoneEdit = findViewById(R.id.phone_edprof);
        mailEdit = findViewById(R.id.email_edprof);
        bioEdit = findViewById(R.id.bio_edprof);
        save_btn = findViewById(R.id.save_btn_edprof);
        cancel_btn = findViewById(R.id.cancel_btn_edprof);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mUserEdit = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
        setTheDatas();

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
    }

    private void setTheDatas() {
        mUserEdit.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dobEdit.setText(dataSnapshot.child("u_dob").getValue().toString());
                cityEdit.setText(dataSnapshot.child("u_lives").getValue().toString());
                phoneEdit.setText(dataSnapshot.child("u_phone").getValue().toString());
                mailEdit.setText(dataSnapshot.child("u_email").getValue().toString());
                bioEdit.setText(dataSnapshot.child("u_bio").getValue().toString());

                mProgress.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onDobClick(View v) {
        new DatePickerDialog(EditProfileActivity.this, date_pick, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dobEdit.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        save_btn.setBackgroundResource(R.drawable.btnshape11);
        cancel_btn.setBackgroundResource(R.drawable.btnshape11);
    }

    public void onSaveClicked(View view) {
        if(cityEdit.getText().toString().equals("")){
            Toast.makeText(this, "City Name can't be blank..", Toast.LENGTH_SHORT).show();
            cityEdit.requestFocus();
        }else if(phoneEdit.getText().toString().equals("")){
            Toast.makeText(this, "Phone No can't be blank..", Toast.LENGTH_SHORT).show();
            phoneEdit.requestFocus();
        }else if(mailEdit.getText().toString().equals("")){
            Toast.makeText(this, "Email can't be blank..", Toast.LENGTH_SHORT).show();
            mailEdit.requestFocus();
        }else if(bioEdit.getText().toString().equals("")){
            Toast.makeText(this, "Bio can't be blank..", Toast.LENGTH_SHORT).show();
            bioEdit.requestFocus();
        }else {
            mUserEdit.child("u_dob").setValue(dobEdit.getText().toString());
            mUserEdit.child("u_lives").setValue(cityEdit.getText().toString());
            mUserEdit.child("u_phone").setValue(phoneEdit.getText().toString());
            mUserEdit.child("u_email").setValue(mailEdit.getText().toString());
            mUserEdit.child("u_bio").setValue(bioEdit.getText().toString());
            finish();
        }
    }

    public void onCancelClick(View view) {
        finish();
    }
}
