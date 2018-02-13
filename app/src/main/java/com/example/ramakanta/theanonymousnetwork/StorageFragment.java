package com.example.ramakanta.theanonymousnetwork;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class StorageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AppCompatActivity main;
    private View root;
    private StorageReference imageStorage;
    private DatabaseReference storageRef;
    private FirebaseAuth mAuth;
    private RecyclerView storageCard;




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
        // Inflate the layout for this fragment
        root=inflater.inflate(R.layout.fragment_storage, container, false);
        main=(AppCompatActivity)getActivity();
        main.getSupportActionBar().setTitle("Storage");
        mAuth=FirebaseAuth.getInstance();
        storageRef= FirebaseDatabase.getInstance().getReference().child("docs").child(mAuth.getCurrentUser().getUid());
        storageRef.keepSynced(true);
        FirebaseRecyclerAdapter<Storage,StorageViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Storage, StorageViewHolder>
                (
                        Storage.class,
                        R.layout.storage_layout,
                        StorageViewHolder.class,
                        storageRef
                ) {
            @Override
            protected void populateViewHolder(StorageViewHolder viewHolder, Storage model, int position) {
                String url=model.getUrl();
                viewHolder.setImage(getActivity(),url);
                viewHolder.setName(model.getName());
            }
        };
        storageCard=root.findViewById(R.id.storage_card);
        storageCard.setHasFixedSize(true);
        storageCard.setLayoutManager(new LinearLayoutManager(getActivity()));
        storageCard.setAdapter(firebaseRecyclerAdapter);
        return root;
    }
    public static class StorageViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView mDoc;
        TextView mDocName;
        public StorageViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            mDoc=mView.findViewById(R.id.storage_image);
            mDocName=mView.findViewById(R.id.storage_name);
        }
        void setImage(final Activity ctx, final String url){
            Picasso.with(ctx).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.mipmap.no_image)
                    .into(mDoc, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(url).placeholder(R.mipmap.no_image).into(mDoc);
                        }
                    });
            mDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityOptionsCompat optionsCompat=ActivityOptionsCompat.makeSceneTransitionAnimation(ctx,
                            new Pair<View, String>(mView.findViewById(R.id.storage_image),"storage"),
                            new Pair<View, String>(mView.findViewById(R.id.storage_name),"s_name"));
                    Intent i=new Intent(ctx,StorageActivity.class);
                    ctx.startActivity(i,optionsCompat.toBundle());
                }
            });
        }
        public void setName(String name){
            mDocName.setText(name);
        }
    }

}
