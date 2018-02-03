package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by ramak on 2/4/2018.
 */

public class PostViewHolder extends RecyclerView.ViewHolder {
    View mView;
    TextView user_name,post_caption,post_likes,post_time;
    ImageView post_image;
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

    public void setPost_time(long time) {
        post_time.setText(""+time);
    }

    public void setPost_image(final Context ctx, final String thumb_image) {
        Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.vector_add_photo)
                .into(post_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.vector_add_photo)
                                .into(post_image);
                    }
                });
    }

    public void setUser_image(final Context ctx, final String thumb_image) {
        Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                .into(user_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError() {
                        Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.logo_def)
                                .into(user_image);
                    }
                });
    }
}
