package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllUsersChatActivity extends AppCompatActivity {
    private MaterialSearchView searchView;
    private RecyclerView allUsersView;
    private DatabaseReference allUsersRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users_chat);
        searchView=findViewById(R.id.search_chat);
        allUsersView=findViewById(R.id.all_users_chat);
        allUsersView.setHasFixedSize(true);
        allUsersView.setLayoutManager(new LinearLayoutManager(this));
        allUsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        allUsersRef.keepSynced(true);
        Toolbar mToolBar=findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("All Users");
        mToolBar.setTitleTextColor(Color.parseColor("#ffffff"));
        allUsersView.setAdapter(getAdapter(""));
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                allUsersView.setAdapter(getAdapter(""));

            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                allUsersView.setAdapter(getAdapter(newText));
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;

    }
    public static class AllUsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView nameText;
        CircleImageView dp;
        public AllUsersViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
            nameText=mView.findViewById(R.id.all_users_chat_name);
            dp=mView.findViewById(R.id.all_users_chat_profile);
        }
        public void setU_thumb_image(final Context ctx, final String thumb_image) {

            Picasso.with(ctx).load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                    .into(dp, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.logo_def)
                                    .into(dp);
                        }
                    });
        }
        public void setU_name(String name) {

            nameText.setText(name);
        }

    }
    FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> getAdapter(String query){
        Query mQuery=allUsersRef.startAt(query).orderByChild("u_name").endAt(query+"\uf8ff");
        FirebaseRecyclerAdapter<AllUsers,AllUsersViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllUsers, AllUsersViewHolder>
                (
                        AllUsers.class,
                        R.layout.all_users,
                        AllUsersViewHolder.class,
                        mQuery
                ) {
            @Override
            protected void populateViewHolder(AllUsersViewHolder viewHolder, AllUsers model, final int position) {
                viewHolder.setU_name(model.getU_name());
                viewHolder.setU_thumb_image(getApplicationContext(),model.getU_thumb_image());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent=new Intent(AllUsersChatActivity.this,ChatActivity.class);
                        chatIntent.putExtra("uid",getRef(position).getKey());
                        startActivity(chatIntent);
                        finish();
                    }
                });

            }
        };
        return firebaseRecyclerAdapter;

    }
}
