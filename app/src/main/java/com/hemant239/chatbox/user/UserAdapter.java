package com.hemant239.chatbox.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hemant239.chatbox.ImageViewActivity;
import com.hemant239.chatbox.R;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {


    ArrayList<UserObject> mUserList;

    Context context;


    public UserAdapter(ArrayList<UserObject> userList, Context context){
        mUserList=userList;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null,false);

        RecyclerView.LayoutParams layoutParams= new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(layoutParams);

        return new UserAdapter.ViewHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.mName.setText(mUserList.get(position).getName());
        holder.mPhoneNumber.setText(mUserList.get(position).getPhoneNumber());

        if(!mUserList.get(position).getProfileImageUri().equals("")){
            holder.mProfilePhoto.setClipToOutline(true);
            holder.mProfilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, ImageViewActivity.class);
                    intent.putExtra("URI",mUserList.get(position).getProfileImageUri());
                    context.startActivity(intent);
                }
            });
            Glide.with(context).load(Uri.parse(mUserList.get(position).getProfileImageUri())).into(holder.mProfilePhoto);
        }

        holder.isSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserList.get(position).setSelected(isChecked);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView    mName,
                    mPhoneNumber;

        ImageView mProfilePhoto;

        CheckBox isSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mName=itemView.findViewById(R.id.userName);
            mPhoneNumber=itemView.findViewById(R.id.userPhone);

            mProfilePhoto=itemView.findViewById(R.id.profileImage);

            isSelected=itemView.findViewById(R.id.userSelected);
        }
    }
}
