package com.hemant239.chatbox.message;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.hemant239.chatbox.ImageViewActivity;
import com.hemant239.chatbox.R;

import java.util.ArrayList;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<MessageObject> messageList;

    String userKey;
    int numberOfUsers;

    Context context;

    public MessageAdapter(ArrayList<MessageObject> messageList, Context context){
        this.messageList=messageList;
        this.context=context;
        userKey= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public MessageAdapter(ArrayList<MessageObject> messageList, Context context, int numberOfUsers){
        this.messageList=messageList;
        this.context=context;
        this.numberOfUsers=numberOfUsers;
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
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, final int position) {

        RelativeLayout.LayoutParams layoutParams= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);

        holder.messageText.setText(messageList.get(position).getText());
        holder.messageSender.setText(messageList.get(position).getSenderName());
        holder.messageTime.setText(messageList.get(position).getTime());
        if(messageList.get(position).getText().equals("")){
            holder.messageText.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams1.addRule(RelativeLayout.ALIGN_END,R.id.mediaImage);
            layoutParams1.addRule(RelativeLayout.BELOW,R.id.mediaImage);
            holder.messageTime.setLayoutParams(layoutParams1);
        }
        if((position!=0 && messageList.get(position).getSenderId().equals(messageList.get(position-1).getSenderId()))
                || numberOfUsers<=2 || messageList.get(position).getSenderId().equals(userKey)){
            holder.messageSender.setVisibility(View.GONE);
        }
        if(!messageList.get(position).getImageUri().equals("")) {

            holder.mediaImage.setVisibility(View.VISIBLE);
            holder.mediaImage.setClipToOutline(true);
            if(!messageList.get(position).getText().equals("")){
                RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.ALIGN_END,R.id.mediaImage);
                layoutParams1.addRule(RelativeLayout.BELOW,R.id.messageText);
                holder.messageTime.setLayoutParams(layoutParams1);

            }

            holder.mediaImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, ImageViewActivity.class);
                    intent.putExtra("URI",messageList.get(position).getImageUri());
                    context.startActivity(intent);
                }
            });
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
                    messageSender,
                    messageTime;

        ImageView mediaImage;

        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender=itemView.findViewById(R.id.messageSender);
            messageText=itemView.findViewById(R.id.messageText);
            messageTime=itemView.findViewById(R.id.messageTime);
            mediaImage=itemView.findViewById(R.id.mediaImage);
            relativeLayout=itemView.findViewById(R.id.parentRelativeLayout);
        }
    }
}
