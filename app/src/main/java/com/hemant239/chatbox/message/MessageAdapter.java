package com.hemant239.chatbox.message;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemant239.chatbox.R;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<MessageObject> messageList;

    String userKey;

    Context context;

    public MessageAdapter(ArrayList<MessageObject> messageList, Context context){
        this.messageList=messageList;
        this.context=context;
        userKey= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,null,false);

        RecyclerView.LayoutParams layoutParams= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);


        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);


        holder.messageText.setText(messageList.get(position).getText());
        holder.messageSender.setText(messageList.get(position).getSenderName());
        if(!messageList.get(position).getImageUri().equals("")) {
            holder.mediaImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(messageList.get(position).getImageUri())).into(holder.mediaImage);
        }
        if(messageList.get(position).getSenderId().equals(userKey)){
            holder.relativeLayout.setLayoutParams(layoutParams);
            holder.relativeLayout.setBackgroundResource(R.drawable.custom_background_message_reciever);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView    messageText,
                    messageSender;

        ImageView mediaImage;

        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender=itemView.findViewById(R.id.messageSender);
            messageText=itemView.findViewById(R.id.messageText);
            mediaImage=itemView.findViewById(R.id.mediaImage);
            relativeLayout=itemView.findViewById(R.id.parentRelativeLayout);
        }
    }
}
