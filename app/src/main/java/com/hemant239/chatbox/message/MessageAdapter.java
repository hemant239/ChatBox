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
    float density;

    Context context;

    public MessageAdapter(ArrayList<MessageObject> messageList, Context context, int numberOfUsers,float density){
        this.messageList=messageList;
        this.context=context;
        this.numberOfUsers=numberOfUsers;
        userKey= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.density=density;
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


        final MessageObject curMessage=messageList.get(position);
        if(curMessage.messageId  ==null && curMessage.text==null &&
                curMessage.senderId==null && curMessage.senderName==null &&
                curMessage.imageUri==null && curMessage.time==null &&  curMessage.date==null){
            holder.relativeLayout.setVisibility(View.GONE);
        }

        else {
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.messageText.setText(curMessage.getText());
            holder.messageSender.setText(curMessage.getSenderName());
            holder.messageTime.setText(curMessage.getTime());


            if (curMessage.getText().equals("")) {
                holder.messageText.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.ALIGN_END, R.id.mediaImage);
                layoutParams1.addRule(RelativeLayout.BELOW, R.id.mediaImage);
                holder.messageTime.setLayoutParams(layoutParams1);
            }


            if ((position != messageList.size() - 1 && curMessage.getSenderId().equals(messageList.get(position + 1).getSenderId()))
                    || numberOfUsers <= 2 || curMessage.getSenderId().equals(userKey)) {
                holder.messageSender.setVisibility(View.GONE);
            }

            if(position>0 && position==messageList.size()-1){
                MessageObject prevMessage=messageList.get(position-1);
                if(prevMessage.messageId  ==null && prevMessage.text==null &&
                        prevMessage.senderId==null && prevMessage.senderName==null &&
                        prevMessage.imageUri==null && prevMessage.time==null &&  prevMessage.date==null){
                }
                else if(curMessage.getSenderId().equals(prevMessage.senderId)){
                    holder.messageSender.setVisibility(View.GONE);
                }

            }



            holder.messageText.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            float width = holder.messageText.getMeasuredWidth() / density;

            if (width > 240) {
                //RelativeLayout.LayoutParams layoutParams2= (RelativeLayout.LayoutParams) holder.messageTime.getLayoutParams();
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.ALIGN_END, R.id.messageText);
                layoutParams1.addRule(RelativeLayout.BELOW, R.id.messageText);
                holder.messageTime.setLayoutParams(layoutParams1);
            }

            if (!curMessage.getImageUri().equals("")) {

                holder.mediaImage.setVisibility(View.VISIBLE);
                holder.mediaImage.setClipToOutline(true);
                if (!curMessage.getText().equals("")) {
                    RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    layoutParams1.addRule(RelativeLayout.ALIGN_END, R.id.mediaImage);
                    layoutParams1.addRule(RelativeLayout.BELOW, R.id.messageText);
                    holder.messageTime.setLayoutParams(layoutParams1);

                }


                holder.mediaImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ImageViewActivity.class);
                        intent.putExtra("URI", curMessage.getImageUri());
                        context.startActivity(intent);
                    }
                });
                Glide.with(context).load(Uri.parse(curMessage.getImageUri())).into(holder.mediaImage);
            }


            if (curMessage.getSenderId().equals(userKey)) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.relativeLayout.getLayoutParams();
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
                holder.relativeLayout.setLayoutParams(layoutParams);
                holder.relativeLayout.setBackgroundResource(R.drawable.custom_background_message_receiver);
            }

            if (position!=messageList.size()-1 && !curMessage.getDate().equals(messageList.get(position + 1).getDate())) {
                holder.messageDateLower.setText(messageList.get(position+1).getDate());
                holder.messageDateLower.setVisibility(View.VISIBLE);
            }

            if(position==0){
                holder.messageDateUpper.setText(curMessage.getDate());
                holder.messageDateUpper.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView    messageText,
                    messageSender,
                    messageTime,
                    messageDateUpper,
                    messageDateLower;

        ImageView mediaImage;

        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageSender=itemView.findViewById(R.id.messageSender);
            messageText=itemView.findViewById(R.id.messageText);
            messageTime=itemView.findViewById(R.id.messageTime);
            mediaImage=itemView.findViewById(R.id.mediaImage);
            messageDateUpper=itemView.findViewById(R.id.DateViewUpper);
            messageDateLower=itemView.findViewById(R.id.DateViewLower);
            relativeLayout=itemView.findViewById(R.id.parentRelativeLayout);
        }
    }
}
