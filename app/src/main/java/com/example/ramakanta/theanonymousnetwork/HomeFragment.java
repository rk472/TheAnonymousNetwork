package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
    private AppCompatActivity main;
    private RecyclerView mContainer;
    private LinearLayoutManager linearLayoutManager;
    private String uId;
    private FirebaseAuth mAuth;
    private DatabaseReference mPostDB,mUserDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Home");
        NavigationView navigationView = (NavigationView) main.findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
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
        FirebaseRecyclerAdapter<Post,PostViewHolder> f=new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.post_element_row,
                PostViewHolder.class,
                mPostDB
        ) {
            @Override
            protected void populateViewHolder(final PostViewHolder viewHolder, final Post model, final int position) {
                viewHolder.setPost_image(getActivity(),model.getP_image());
                viewHolder.setPost_caption(model.getP_caption());
                viewHolder.setPost_likes(model.getP_like());
                viewHolder.setPost_time(LastSeen.getTimeAgo(model.getP_time()));
                mUserDB=FirebaseDatabase.getInstance().getReference().child("Users").child(model.getP_user());
                mUserDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setUser_image(getActivity(),dataSnapshot.child("u_image").getValue().toString());
                        viewHolder.setUser_name(dataSnapshot.child("u_name").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                final DatabaseReference likesRef=FirebaseDatabase.getInstance().getReference().child("likes").child(getRef(position).getKey());
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            viewHolder.setLikedButton(dataSnapshot.hasChild(uId));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.post_like_button.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        likesRef.child(uId).setValue(true);
                        mPostDB.child(getRef(position).getKey()).child("p_like").setValue(model.getP_like()+1);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        likesRef.child(uId).removeValue();
                        mPostDB.child(getRef(position).getKey()).child("p_like").setValue(model.getP_like()-1);
                    }
                });
            }
        };
        mContainer.setAdapter(f);


        return root;
    }
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView user_name,post_caption,post_likes,post_time;
        ImageView post_image;
        LikeButton post_like_button;
        CircleImageView user_image;
        public PostViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            user_image = mView.findViewById(R.id.posted_user_image);
            user_name = mView.findViewById(R.id.posted_user_name);
            post_caption = mView.findViewById(R.id.posted_caption);
            post_image = mView.findViewById(R.id.posted_image);
            post_likes = mView.findViewById(R.id.posted_likes);
            post_time = mView.findViewById(R.id.posted_time);
            post_like_button=mView.findViewById(R.id.post_like_button);
        }

        public void setUser_name(String name) {
            user_name.setText(name);
        }

        public void setPost_caption(String caption) {
            post_caption.setText(caption);
        }

        public void setPost_likes(int likes) {
            post_likes.setText(likes+"");
        }

        public void setPost_time(String time) {
            post_time.setText(""+time);
        }

        public void setPost_image(final Context ctx, final String thumb_image) {
            if(!thumb_image.equals("no_image")) {
                post_image.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.loading)
                        .into(post_image, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.loading)
                                        .into(post_image);
                            }
                        });
            }else{
                post_image.setVisibility(View.GONE);
            }

        }
        public void setLikedButton(boolean like) {
            post_like_button.setLiked(like);

        }

        public void setUser_image(final Context ctx, final String thumb_image) {
            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.loading)
                    .into(user_image, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.loading)
                                    .into(user_image);
                        }
                    });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        linearLayoutManager.scrollToPosition(0);
    }
}
