package com.example.ramakanta.theanonymousnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;

public class MonthAttendanceActivity extends AppCompatActivity {
    private RecyclerView monthList;
    private TextView monthName;
    private String month,roll,year,batch;
    private DatabaseReference attRef,subjectRef;
    private List<MonthlyAttendance> attList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_attendance);
        monthList=findViewById(R.id.month_attendance_list);
        monthList.setLayoutManager(new LinearLayoutManager(this));
        monthList.setHasFixedSize(true);
        monthName=findViewById(R.id.month_attendance_name);
        final int m=getIntent().getExtras().getInt("month");
        year=Integer.toString(getIntent().getExtras().getInt("year"));
        batch=getIntent().getExtras().getString("batch");
        DateFormatSymbols s=new DateFormatSymbols();
        month=s.getMonths()[m];
        monthName.setText(month);
        roll=Integer.toString(getIntent().getExtras().getInt("roll"));
        subjectRef=FirebaseDatabase.getInstance().getReference().child("subjects").child(batch);
        subjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                attList=new ArrayList<>();
                for(final DataSnapshot d:dataSnapshot.getChildren()){
                    final String sub=d.getKey();
                    //Toast.makeText(MonthAttendanceActivity.this, sub+" "+batch+" "+year+" "+(m+1), Toast.LENGTH_SHORT).show();
                    attRef= FirebaseDatabase.getInstance().getReference().child("attendance").child("monthly").child(batch).child(year).child(Integer.toString(m+1)).child(sub);
                    attRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long t=dataSnapshot.child("total").getValue(Long.class);
                            long att=dataSnapshot.child(roll).getValue(Long.class);
                            MonthlyAttendance m=new MonthlyAttendance(t,att,sub);
                            attList.add(m);
                            MonthlyAdapter monthlyAdapter=new MonthlyAdapter(attList);
                            monthList.setAdapter(monthlyAdapter);
                            attRef.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    subjectRef.removeEventListener(this);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

}
