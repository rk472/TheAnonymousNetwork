package com.example.ramakanta.theanonymousnetwork;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class TimeTableFragment extends Fragment {

    private AppCompatActivity main;
    private View root;
    private Spinner dayList;
    private RecyclerView list;
    private FirebaseAuth mAuth;
    private DatabaseReference timeRef,userRef;
    private String uid,batch="0";
    private String[] weekDays;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Time-Table");
        root=inflater.inflate(R.layout.fragment_timetable, container, false);
        dayList=root.findViewById(R.id.day_list);
        dayList.setSelection(0);
        list=root.findViewById(R.id.time_table_view);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth=FirebaseAuth.getInstance();
        uid=mAuth.getCurrentUser().getUid();
        userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        weekDays=getResources().getStringArray(R.array.week);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                batch=dataSnapshot.child("u_joined").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        dayList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, final View view, int i, long l) {
                String item=weekDays[i];
                timeRef=FirebaseDatabase.getInstance().getReference().child("timetable").child(batch).child(item);
                FirebaseRecyclerAdapter<TimeTable,TimeTableViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<TimeTable, TimeTableViewHolder>(
                        TimeTable.class,
                        R.layout.timetable_layout,
                        TimeTableViewHolder.class,
                        timeRef
                ) {
                    @Override
                    protected void populateViewHolder(TimeTableViewHolder viewHolder, TimeTable model, int position) {
                        viewHolder.setName(model.getName());
                        viewHolder.setTime(model.getTime());
                    }
                };
                list.setAdapter(firebaseRecyclerAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return root;

    }


    public static class TimeTableViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameView,timeView;
        public TimeTableViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            nameView=mView.findViewById(R.id.timetable_subject);
            timeView=mView.findViewById(R.id.timetable_time);
        }
        public void setName(String name) {
            nameView.setText(name);
        }
        public void setTime(String time){
            timeView.setText(time);
        }
    }

}
