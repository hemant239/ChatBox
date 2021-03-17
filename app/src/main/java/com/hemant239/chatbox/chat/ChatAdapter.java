package com.hemant239.chatbox.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hemant239.chatbox.AllChatsActivity;
import com.hemant239.chatbox.ImageViewActivity;
import com.hemant239.chatbox.R;
import com.hemant239.chatbox.SpecificChatActivity;

import java.util.ArrayList;



public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    ArrayList<ChatObject> mChatList;
    Context context;


    RecyclerView.Adapter<ChatAdapter.ViewHolder> tempChatAdapter;

    public ChatAdapter(ArrayList<ChatObject> chatList, AllChatsActivity allChatsActivity){
        mChatList=chatList;
        context=allChatsActivity;
        tempChatAdapter=this;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat,null,false);

        RecyclerView.LayoutParams layoutParams=new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return new ChatAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mChatID.setText(mChatList.get(position).getUid());
        holder.mChatName.setText(mChatList.get(position).getName());

        if(!mChatList.get(position).getImageUri().equals("")){
            holder.mChatImage.setClipToOutline(true);
            holder.mChatImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, ImageViewActivity.class);
                    intent.putExtra("URI",mChatList.get(position).getImageUri());
                    context.startActivity(intent);
                }
            });
            Glide.with(context).load(Uri.parse(mChatList.get(position).getImageUri())).into(holder.mChatImage);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SpecificChatActivity.class);

                //intent.putExtra("Chat Adapter", (Parcelable) ChatAdapter.this);
                intent.putExtra("Chat Key",mChatList.get(position).getUid());
                intent.putExtra("Chat Name",mChatList.get(position).getName());
                intent.putExtra("Image Uri",mChatList.get(position).getImageUri());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView mChatName,mChatID;

        ImageView mChatImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mChatName=itemView.findViewById(R.id.chatName);
            mChatID=itemView.findViewById(R.id.chatID);
            mChatImage=itemView.findViewById(R.id.chatProfileImage);
        }

    }
}
