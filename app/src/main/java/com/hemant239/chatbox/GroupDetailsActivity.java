package com.hemant239.chatbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemant239.chatbox.user.UserAdapter;
import com.hemant239.chatbox.user.UserObject;

import java.util.ArrayList;
import java.util.Objects;

public class GroupDetailsActivity extends AppCompatActivity {

    ImageView mGroupImage;
    TextView mGroupName;

    String image,key,name;
    RecyclerView mUserList;
    RecyclerView.Adapter<UserAdapter.ViewHolder> mUserListAdapter;
    RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_details);

        userList=new ArrayList<>();
        initializeViews();
        initializeRecyclerViews();


        key="";
        name="";
        image="";
        key=getIntent().getStringExtra("Key");
        name=getIntent().getStringExtra("Name");
        image=getIntent().getStringExtra("Image");

        mGroupName.setText(name);
        if(!image.equals("")) {
            mGroupImage.setClipToOutline(true);
            Glide.with(this).load(Uri.parse(image)).into(mGroupImage);

            mGroupImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                    intent.putExtra("URI", image);
                    startActivity(intent);
                }
            });
        }

        getUserList();
    }

    private void getUserList() {
        FirebaseDatabase.getInstance().getReference().child("Chats/"+key+"/info/user").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot childSnapshot:snapshot.getChildren()){
                        getUserDetails(childSnapshot.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserDetails(final String userKey) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name="",
                        phone="",
                        image="",
                        status="";
                if(snapshot.exists()){
                    if(snapshot.child("Name").getValue()!=null){
                        name= Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    }
                    if(snapshot.child("Phone Number").getValue()!=null){
                        phone= Objects.requireNonNull(snapshot.child("Phone Number").getValue()).toString();
                    }if(snapshot.child("Profile Image Uri").getValue()!=null){
                        image= Objects.requireNonNull(snapshot.child("Profile Image Uri").getValue()).toString();
                    }
                    if(snapshot.child("Status").getValue()!=null){
                        status= Objects.requireNonNull(snapshot.child("Status").getValue()).toString();
                    }

                    UserObject userObject=new UserObject(userKey,name,phone,status,image);
                    userList.add(userObject);
                    mUserListAdapter.notifyItemInserted(userList.size()-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void initializeViews() {
        mGroupName = findViewById(R.id.groupDetailName);
        mGroupImage=findViewById(R.id.groupDetailProfileImage);
    }
    private void initializeRecyclerViews() {
        mUserList=findViewById(R.id.groupDetailRecyclerView);
        mUserList.setHasFixedSize(false);
        mUserList.setNestedScrollingEnabled(false);

        mUserListAdapter= new UserAdapter(userList,this, false,true);
        mUserList.setAdapter(mUserListAdapter);

        mUserListLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
    }

}