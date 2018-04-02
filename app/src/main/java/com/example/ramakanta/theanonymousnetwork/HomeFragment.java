package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {
    private boolean status;
    private AppCompatActivity main;
    private RecyclerView mContainer;
    private String name , dp_url;
    private LinearLayoutManager linearLayoutManager;
    private String uId;
    private FirebaseAuth mAuth;
    private DatabaseReference mPostDB,mUserDB;
    List<Post> l;

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
        l=new ArrayList<>();
        mPostDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot d) {
              for(DataSnapshot dataSnapshot:d.getChildren()){
                  String image=dataSnapshot.child("p_image").getValue().toString();
                  String user=dataSnapshot.child("p_user").getValue().toString();
                  String caption=dataSnapshot.child("p_caption").getValue().toString();
                  int likes=Integer.parseInt(dataSnapshot.child("p_like").getValue().toString());
                  long order=Long.parseLong(dataSnapshot.child("p_order").getValue().toString());
                  long time=Long.parseLong(dataSnapshot.child("p_time").getValue().toString());
                  Post p=new Post(image,user,caption,likes,time);
                  l.add(p);
              }
                PostAdapter adapter=new PostAdapter(l,getContext());
                mContainer.setAdapter(adapter);
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        linearLayoutManager.scrollToPosition(0);
    }
}
