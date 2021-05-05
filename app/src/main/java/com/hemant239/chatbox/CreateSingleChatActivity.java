package com.hemant239.chatbox;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class CreateSingleChatActivity extends AppCompatActivity {

    String  userKey,
            curUserKey,
            userName,
            curUserName,
            userImage,
            curUserImage;

    Button mCancelButton;

    ValueEventListener userEvent,
                        chatEvent;

    DatabaseReference   mUserDb,
                        mChatDb;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_single_chat);

        initializeViews();

        curUserKey= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        curUserName="";
        curUserImage="";

        userKey= getIntent().getStringExtra("User Key");
        userName=getIntent().getStringExtra("User Name");
        userImage=getIntent().getStringExtra("User image");


        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserDb.removeEventListener(userEvent);
                mChatDb.removeEventListener(chatEvent);
                finish();
            }
        });

        checkChat();

    }



    private void checkChat() {
        mUserDb= FirebaseDatabase.getInstance().getReference().child("Users").child(curUserKey);
        mUserDb.addListenerForSingleValueEvent(userEvent=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    if(snapshot.child("Name").getValue()!=null){
                        curUserName= Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    }
                    if(snapshot.child("Profile Image Uri").getValue()!=null){
                        curUserImage= Objects.requireNonNull(snapshot.child("Profile Image Uri").getValue()).toString();
                    }

                    if(snapshot.child("Single chats").child(userKey).getValue()!=null){
                        openChat(Objects.requireNonNull(snapshot.child("Single chats").child(userKey).getValue()).toString());
                    }
                    else{
                        createChat();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createChat() {
        mChatDb= FirebaseDatabase.getInstance().getReference().child("Chats");
        final String key= mChatDb.push().getKey();
        HashMap<String,Object> mUserInfo=new HashMap<>();

        mUserInfo.put(userKey+"/Single chats/"+curUserKey,key);
        mUserInfo.put(curUserKey+"/Single chats/"+userKey,key);
        mUserInfo.put(curUserKey+"/chat/"+key,true);
        mUserInfo.put(userKey+"/chat/"+key,true);

        FirebaseDatabase.getInstance().getReference().child("Users").updateChildren(mUserInfo);

        HashMap<String,Object> mChatInfo=new HashMap<>();

        mChatInfo.put("ID",key);
        mChatInfo.put("isSingleChat",true);
        mChatInfo.put(userKey+"/Name",curUserName);
        mChatInfo.put(curUserKey+"/Name",userName);
        mChatInfo.put("Number Of Users",1);
        mChatInfo.put(userKey+"/Chat Profile Image Uri",curUserImage);
        mChatInfo.put(curUserKey+"/Chat Profile Image Uri",userImage);
        mChatInfo.put("user/"+curUserKey,true);
        mChatInfo.put("user/"+userKey,true);


        assert key != null;
        mChatDb.child(key).child("info").updateChildren(mChatInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    openChat(key);
                }
                else{
                    Toast.makeText(getApplicationContext(),"chat loading failed",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    private void openChat(final String chatKey) {
        mChatDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(chatKey).child("info");
        mChatDb.addListenerForSingleValueEvent(chatEvent=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String chatName="";
                    String imageUri="";
                    String lastMessageId="";

                    if(snapshot.child(curUserKey).child("Name").getValue()!=null){
                        chatName= Objects.requireNonNull(snapshot.child(curUserKey).child("Name").getValue()).toString();
                    }
                    if(snapshot.child(curUserKey).child("Chat Profile Image Uri").getValue()!=null){
                        imageUri= Objects.requireNonNull(snapshot.child(curUserKey).child("Chat Profile Image Uri").getValue()).toString();
                    }
                    if(snapshot.child("Last Message").getValue()!=null){
                        lastMessageId= Objects.requireNonNull(snapshot.child("Last Message").getValue()).toString();
                    }

                    Intent intent=new Intent(getApplicationContext(),SpecificChatActivity.class);
                    intent.putExtra("Chat Key",chatKey);
                    intent.putExtra("Chat Name",chatName);
                    intent.putExtra("Image Uri",imageUri);
                    intent.putExtra("Number Of Users",1);
                    intent.putExtra("Last Message ID",lastMessageId);
                    intent.putExtra("is single chat",true);
                    startActivity(intent);
                    ((CreateNewChatActivity)CreateNewChatActivity.context).finish();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void initializeViews() {
        mCancelButton=findViewById(R.id.cancelChatLoadButton);
    }
}