package com.example.ramakanta.theanonymousnetwork;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DayAttendanceActivity extends AppCompatActivity {
    private TextView dateText,dayText,errorText;
    private RecyclerView list;
    private DatabaseReference attRef;
    private String batch,date,roll,day;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_attendance);
        dateText=findViewById(R.id.day_attendance_date);
        dayText=findViewById(R.id.day_attendance_day);
        errorText=findViewById(R.id.day_attendance_error);
        list=findViewById(R.id.day_attendance_list);
        batch=getIntent().getExtras().getString("batch");
        date=getIntent().getExtras().getString("date");
        String d=date.split("/")[0];
        String m=date.split("/")[1];
        String y=date.split("/")[2];
        roll=getIntent().getExtras().getString("roll");
        day=getIntent().getExtras().getString("day");
        dateText.setText(date);
        dayText.setText(day);
        attRef= FirebaseDatabase.getInstance().getReference().child("attendance").child(batch).child(y).child(m).child(d);
        FirebaseRecyclerAdapter<DailyAttendance,DailyAttendanceViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<DailyAttendance, DailyAttendanceViewHolder>(
                DailyAttendance.class,
                R.layout.day_attendance_layout,
                DailyAttendanceViewHolder.class,
                attRef
        ) {
            @Override
            protected void populateViewHolder(final DailyAttendanceViewHolder viewHolder, final DailyAttendance model, final int position) {
                DatabaseReference subAttRef=FirebaseDatabase.getInstance().getReference().child("records").child(model.getAttendance());
                subAttRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean b=dataSnapshot.child(roll).getValue(Boolean.class);
                        viewHolder.setAllData(model.getTime(),getRef(position).getKey(),b);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        list.setAdapter(firebaseRecyclerAdapter);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));


    }
    public static class DailyAttendanceViewHolder extends RecyclerView.ViewHolder{
        TextView timeText,subjectText,attendanceText;
        public DailyAttendanceViewHolder(View itemView) {
            super(itemView);
            timeText=itemView.findViewById(R.id.day_attendance_time);
            subjectText=itemView.findViewById(R.id.day_attendance_subject);
            attendanceText=itemView.findViewById(R.id.day_attendance_attendance);
        }
        public void setAllData(String time,String subject,boolean attendance){
            timeText.setText(time);
            subjectText.setText(subject);
            if(attendance)
                attendanceText.setText("P");
            else
                attendanceText.setText("A");
        }
    }
}
