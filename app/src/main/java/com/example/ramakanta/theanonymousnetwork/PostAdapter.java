package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by daduc on 28-03-2018.
 */

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    List<Post> l;
    Context ctx;

    public PostAdapter(List<Post> l,Context ctx) {
        this.l = l;
        this.ctx=ctx;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_element_row, parent, false);

        PostViewHolder vh = new PostViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        holder.setPost_caption(l.get(position).getP_caption());
        holder.setPost_image(ctx,l.get(position).getP_image());
        holder.setPost_time(LastSeen.getTimeAgo(l.get(position).getP_time()));
        DatabaseReference d= FirebaseDatabase.getInstance().getReference().child("Users").child(l.get(position).getP_user());
        d.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.setUser_image(ctx,dataSnapshot.child("u_image").getValue().toString());
                holder.setUser_name(dataSnapshot.child("u_name").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return l.size();
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
            this.setIsRecyclable(false);
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
            }

        }
        public void setLikedButton() {

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
}
