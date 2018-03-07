package com.example.ramakanta.theanonymousnetwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.PipedOutputStream;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean status;
    private AppCompatActivity main;
    private RecyclerView mContainer;
    private String name , dp_url;
    private LinearLayoutManager linearLayoutManager;
    private String uId;
    private FirebaseAuth mAuth;
    private DatabaseReference mPostDB,mUserDB;
    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Home");

        View root=inflater.inflate(R.layout.fragment_home, container, false);
        FloatingActionButton fab =  root.findViewById(R.id.fab_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(main,AddPostActivity.class);
                startActivity(i);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();
        mPostDB =  FirebaseDatabase.getInstance().getReference().child("posts");
        mPostDB.keepSynced(true);
        mContainer = root.findViewById(R.id.post_container_home);
        mContainer.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(main);
        mContainer.setLayoutManager(linearLayoutManager);
        FirebaseRecyclerAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,R.layout.post_element_row,PostViewHolder.class,mPostDB.orderByChild("p_order")
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, final Post model, final int position) {
                final PostViewHolder holder = viewHolder;
                String user_id = model.getP_user();
                mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                mUserDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        name = dataSnapshot.child("u_name").getValue().toString();
                        dp_url = dataSnapshot.child("u_thumb_image").getValue().toString();
                        holder.setUser_image(getActivity().getApplicationContext(),dp_url);
                        holder.setUser_name(name);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                viewHolder.setPost_caption(model.getP_caption());
                viewHolder.setPost_likes(model.getP_like());
                viewHolder.setPost_time(LastSeen.getTimeAgo(model.getP_time()));

                if(model.getP_image().equalsIgnoreCase("no_image")){
                }else{
                    viewHolder.post_image.setVisibility(View.VISIBLE);
                    viewHolder.setPost_image(getActivity().getApplicationContext(),model.getP_image());
                }
                viewHolder.post_like_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        mContainer.setAdapter(adapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        linearLayoutManager.scrollToPosition(0);
    }
}
