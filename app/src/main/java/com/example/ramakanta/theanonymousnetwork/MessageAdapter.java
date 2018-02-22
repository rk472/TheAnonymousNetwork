package com.example.ramakanta.theanonymousnetwork;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by daduc on 03-02-2018.
 */

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<AllChats> userMessagesList;
    private FirebaseAuth mAuth;
    public MessageAdapter(List<AllChats> userMessageList){
        this.userMessagesList=userMessageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout_users,parent,false);
        mAuth=FirebaseAuth.getInstance();
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        AllChats messages=userMessagesList.get(position);
        String fromUserId=messages.getFrom();
        String myUid=mAuth.getCurrentUser().getUid();
        if(fromUserId.equals(myUid)){
            holder.messageText.setBackgroundResource(R.drawable.message_text_background_2);
            holder.messageText.setTextColor(Color.BLACK);
            holder.messageHolder.setGravity(Gravity.RIGHT);
        }else{
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
            holder.messageHolder.setGravity(Gravity.LEFT);
        }
        holder.messageText.setText(messages.getMessage());
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText;
        private LinearLayout messageHolder;

        public MessageViewHolder(View view){
            super(view);
            messageText=view.findViewById(R.id.message_text);
            messageHolder = view.findViewById(R.id.message_holder);
        }
    }
}
