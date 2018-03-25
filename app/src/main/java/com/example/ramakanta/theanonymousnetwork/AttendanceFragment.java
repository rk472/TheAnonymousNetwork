package com.example.ramakanta.theanonymousnetwork;

import android.app.DatePickerDialog;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AttendanceFragment extends Fragment {
    private AppCompatActivity main;
    private View mView;
    private Button dayButton,monthButton;
    private EditText dateText;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private String batch,roll;
    private Calendar c;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Attendance");
        // Inflate the layout for this fragment
        mView=inflater.inflate(R.layout.fragment_attendance, container, false);
        mAuth=FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();
        dayButton=mView.findViewById(R.id.day_button);
        monthButton=mView.findViewById(R.id.month_button);
        dateText=mView.findViewById(R.id.date_attendance);
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                batch=dataSnapshot.child("u_joined").getValue().toString();
                roll=dataSnapshot.child("u_roll").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        c=Calendar.getInstance();
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        dateText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        c.set(Calendar.YEAR,year);c.set(Calendar.MONTH,month);c.set(Calendar.DATE,dayOfMonth);
                    }
                },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity(),DayAttendanceActivity.class);
                String date=dateText.getText().toString();
                i.putExtra("batch",batch);
                i.putExtra("roll",roll.split("0")[1]);
                i.putExtra("date",date);
                int y=Integer.parseInt(date.split("/")[2]);
                int m=Integer.parseInt(date.split("/")[1]);
                int d=Integer.parseInt(date.split("/")[0]);
                String day=new SimpleDateFormat("EEEE").format(new Date(y,m,d-1));
                i.putExtra("day",day);
                startActivity(i);
            }
        });
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(dateText.getText())) {
                    Intent i = new Intent(getActivity(), MonthAttendanceActivity.class);
                    startActivity(i);
                }
            }
        });

        return mView;

    }

}
