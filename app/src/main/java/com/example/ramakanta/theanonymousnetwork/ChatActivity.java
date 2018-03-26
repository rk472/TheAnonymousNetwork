package com.example.ramakanta.theanonymousnetwork;

import
        android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static RelativeLayout chatContainer;
    private String messageUid,myUid;
    private int currTheme;
    private DatabaseReference messageRef;
    private FirebaseAuth mAuth;
    private RecyclerView userMessagesList;
    private static SharedPreferences pref;
    private LinearLayoutManager linearLayoutManager;
    private final List<AllChats> messageList=new ArrayList<>();
    private MessageAdapter messageAdapter;
    private EditText inputMessageText;
    private ImageButton sendMessage;
    private TextView nameText;
    private CircleImageView dp;
    private Toolbar chatToolbar;

    public static void changeTheme(int drawable) {
        chatContainer.setBackgroundResource(drawable);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("background",drawable);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatContainer = findViewById(R.id.chatContainer);
        pref = getApplicationContext().getSharedPreferences("ChatBackground", MODE_PRIVATE);
        currTheme = pref.getInt("background",R.drawable.chat_background);
        changeTheme(currTheme);
        messageAdapter=new MessageAdapter(messageList);
        mAuth=FirebaseAuth.getInstance();
        inputMessageText=findViewById(R.id.input_message);
        myUid=mAuth.getCurrentUser().getUid();
        messageUid=getIntent().getExtras().getString("uid");
        messageRef= FirebaseDatabase.getInstance().getReference().child("chats").child(myUid).child(messageUid);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList=findViewById(R.id.messages_list);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);
        fetchMessage();
        sendMessage=findViewById(R.id.send_message);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        chatToolbar=findViewById(R.id.chat_app_bar);
        chatToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent=new Intent(ChatActivity.this,ProfileActivity.class);
                profileIntent.putExtra("uid",messageUid);
                startActivity(profileIntent);
            }
        });
        setSupportActionBar(chatToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView=layoutInflater.inflate(R.layout.chat_bar_layout,null);
        actionBar.setCustomView(actionBarView);
        nameText=findViewById(R.id.custom_user_profile_name);
        dp=findViewById(R.id.custom_profile_image);
        DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child("Users").child(messageUid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("u_name").getValue().toString();
                final String url=dataSnapshot.child("u_thumb_image").getValue().toString();
                nameText.setText(name);
                Picasso.with(getApplicationContext()).load(url).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.logo_def)
                        .into(dp, new Callback() {
                            @Override
                            public void onSuccess() {
                            }
                            @Override
                            public void onError() {
                                Picasso.with(getApplicationContext()).load(url).placeholder(R.drawable.logo_def)
                                        .into(dp);
                            }
                        });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void fetchMessage() {
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AllChats messages = dataSnapshot.getValue(AllChats.class);
                messageList.add(messages);
                messageAdapter.notifyDataSetChanged();
                linearLayoutManager.scrollToPosition(messageList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
        public void sendMessage(){
            linearLayoutManager.scrollToPosition(messageList.size()-1);
            final String message=inputMessageText.getText().toString();
            if(!TextUtils.isEmpty(message)){
                String message_sender_ref="chats/"+myUid+"/"+messageUid;
                final String message_reciever_ref="chats/"+messageUid+"/"+myUid;
                DatabaseReference userMessageRef=messageRef.push();
                String messagePushId=userMessageRef.getKey();

                Map messageTextBody=new HashMap();
                messageTextBody.put("message",message);
                messageTextBody.put("time", ServerValue.TIMESTAMP);
                messageTextBody.put("from",myUid);


                Map messageBodyDetails=new HashMap();
                messageBodyDetails.put(message_sender_ref+"/"+messagePushId,messageTextBody);
                messageBodyDetails.put(message_reciever_ref+"/"+messagePushId,messageTextBody);
                final DatabaseReference rootRef=FirebaseDatabase.getInstance().getReference();
                rootRef.updateChildren(messageBodyDetails, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Map chats=new HashMap();
                        String message1=message;
                        if(message.length()>25){
                            message1=message.substring(0,24);
                        }
                        chats.put("lastMessage",message1);
                        chats.put("from",myUid);
                        chats.put("time",ServerValue.TIMESTAMP);
                        String chatSenderRef="messages/"+myUid+"/"+messageUid;
                        String chatRecieverRef="messages/"+messageUid+"/"+myUid;

                        Map chatDetails=new HashMap();
                        chatDetails.put(chatSenderRef,chats);
                        chatDetails.put(chatRecieverRef,chats);

                        rootRef.updateChildren(chatDetails, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null){
                                    Log.d("Chat_Log",databaseError.getMessage().toString());
                                }
                            }
                        });

                        if(databaseError!=null){
                            Log.d("Chat_Log",databaseError.getMessage().toString());
                        }
                        inputMessageText.setText("");
                        linearLayoutManager.scrollToPosition(messageList.size()-1);
                    }
                });
            }
        }

    public void startThemeSelect(View view) {
        /*ntent intent = new Intent(ChatActivity.this, ChatThemeActivity.class);
        intent.putExtra("currTheme",currTheme);
        startActivity(intent);*/
    }
}

