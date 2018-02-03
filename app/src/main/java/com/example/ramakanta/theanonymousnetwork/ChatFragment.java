package com.example.ramakanta.theanonymousnetwork;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View root;
    private AppCompatActivity main;
    private FloatingActionButton addChat;
    private DatabaseReference chatRef;
    private FirebaseAuth mAuth;
    private String myUid;
    private RecyclerView allChats;
    public ChatFragment() {
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
        root=inflater.inflate(R.layout.fragment_chat, container, false);
        main.getSupportActionBar().setTitle("Chats");

        addChat=root.findViewById(R.id.add_chat);
        addChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(main,AllUsersChatActivity.class);
                startActivity(i);
            }
        });
        mAuth=FirebaseAuth.getInstance();
        myUid=mAuth.getCurrentUser().getUid();
        chatRef= FirebaseDatabase.getInstance().getReference().child("messages").child(myUid);
       // Toast.makeText(main, "hiii", Toast.LENGTH_SHORT).show();
        allChats=root.findViewById(R.id.all_chat);
        allChats.setHasFixedSize(true);
        allChats.setLayoutManager(new LinearLayoutManager(getActivity()));


        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<AllChatList,AllChatViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<AllChatList, AllChatViewHolder>
                (
                        AllChatList.class,
                        R.layout.all_chats,
                        AllChatViewHolder.class,
                        chatRef
                ) {
            @Override
            protected void populateViewHolder(final AllChatViewHolder viewHolder, AllChatList model, final int position) {
                String fromUid=model.getFrom();
                String uid=getRef(position).getKey();
                viewHolder.setSent(fromUid==myUid);
                viewHolder.setLastMessage(model.getLastMessage());
                viewHolder.setTime(LastSeen.getTimeAgo(model.getTime()));
                DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.setName(dataSnapshot.child("u_name").getValue().toString());
                        viewHolder.setDP(getContext(),dataSnapshot.child("u_thumb_image").getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent chatIntent=new Intent(getActivity(),ChatActivity.class);
                        chatIntent.putExtra("uid",getRef(position).getKey());
                        startActivity(chatIntent);
                    }
                });
            }
        };

        allChats.setAdapter(firebaseRecyclerAdapter);
    }

    public static class AllChatViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView lastMessageText;
        ImageView sent;
        TextView name;
        TextView time;
        CircleImageView dp;
        public AllChatViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            lastMessageText=mView.findViewById(R.id.last_message);
            sent=mView.findViewById(R.id.sent);
            name=mView.findViewById(R.id.all_users_chat_name);
            time=mView.findViewById(R.id.chat_time);
            dp=mView.findViewById(R.id.all_chats_dp);
        }
        public void setLastMessage(String lastMessage) {
            lastMessageText.setText(lastMessage);
        }
        public void setSent(boolean sentStatus){
            if(sentStatus){
                sent.setVisibility(View.VISIBLE);
            }else{
                sent.setVisibility(View.INVISIBLE);
            }
        }
        public void setTime(String chatTime){
            time.setText(chatTime);
        }
        public void setName(String u_name){
            name.setText(u_name);
        }
        public void setDP(final Context ctx, final String url){
            Picasso.with(ctx).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                    .into(dp, new Callback() {
                        @Override
                        public void onSuccess() {

                        }
                        @Override
                        public void onError() {
                            Picasso.with(ctx).load(url).placeholder(R.drawable.logo_def)
                                    .into(dp);
                        }
                    });
        }
    }

}
