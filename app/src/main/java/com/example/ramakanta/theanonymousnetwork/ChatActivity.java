package com.example.ramakanta.theanonymousnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private String messageUid,myUid;
    private DatabaseReference messageRef;
    private FirebaseAuth mAuth;
    private RecyclerView userMessagesList;
    private LinearLayoutManager linearLayoutManager;
    private final List<AllChats> messageList=new ArrayList<>();
    private MessageAdapter messageAdapter;
    private EditText inputMessageText;
    private Button sendMessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
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

    }

    private void fetchMessage() {
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AllChats messages = dataSnapshot.getValue(AllChats.class);
                messageList.add(messages);
                messageAdapter.notifyDataSetChanged();
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
                    }
                });
            }
        }
    }

