package com.hemant239.chatbox;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemant239.chatbox.chat.ChatAdapter;
import com.hemant239.chatbox.chat.ChatObject;
import com.hemant239.chatbox.user.UserObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class AllChatsActivity extends AppCompatActivity {


    private static RecyclerView.Adapter<ChatAdapter.ViewHolder> mChatListAdapter;
    RecyclerView mChatList;
    RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList;

    DatabaseReference mUserDB, mChatDB;

    static UserObject curUser;

    static Context context;
    String curDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);


        if (getIntent().getBooleanExtra("First time", false)) {
            requestUserPermission();
        }

        context = this;
        curUser = new UserObject();
        getCurUserDetails();

        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("EEE, MMM dd, yyyy");
        curDate = simpleDateFormatDate.format(date).toUpperCase();

        chatList = new ArrayList<>();
        initializeViews();
        initializeRecyclerViews();
        getChatList();
    }

    private void getCurUserDetails() {
        final String userKey = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = "";
                    String userPhone = "";
                    String userImage = "";
                    String userStatus = "";

                    if (snapshot.child("Name").getValue() != null) {
                        userName = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    }
                    if (snapshot.child("Phone Number").getValue() != null) {
                        userPhone = Objects.requireNonNull(snapshot.child("Phone Number").getValue()).toString();
                    }
                    if (snapshot.child("Profile Image Uri").getValue() != null) {
                        userImage = Objects.requireNonNull(snapshot.child("Profile Image Uri").getValue()).toString();
                    }
                    if (snapshot.child("Status").getValue() != null) {
                        userStatus = Objects.requireNonNull(snapshot.child("Status").getValue()).toString();
                    }
                    curUser = new UserObject(userKey, userName, userPhone, userStatus, userImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getChatList() {

        FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
        mUserDB= FirebaseDatabase.getInstance().getReference().child("Users");

        if(mUser!=null){
            mUserDB=mUserDB.child(mUser.getUid()).child("chat");
            mUserDB.orderByValue().addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists() && snapshot.getKey()!=null){
                        getChatDetails(snapshot.getKey());
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


    }

    private void getChatDetails(final String key) {
        mChatDB=FirebaseDatabase.getInstance().getReference().child("Chats").child(key);
        mChatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    final String[] name = {""};
                    String imageUri="";
                    final String lastMessageId;
                    String lastSenderId;
                    final String[] lastSenderName = {""};
                    String lastMessageText="Photo";
                    String lastMessageTime="";
                    int numberOfUsers=0;
                    boolean isSingleChat=false;


                    String curUserKey= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    if(snapshot.child("info").child("isSingleChat").getValue()!=null){
                        isSingleChat= true;
                    }

                    if(snapshot.child("info").child("Name").getValue()!=null){
                        name[0] = Objects.requireNonNull(snapshot.child("info").child("Name").getValue()).toString();
                    }

                    if(isSingleChat && snapshot.child("info").child(curUserKey).child("Name").getValue()!=null){
                        name[0] = Objects.requireNonNull(snapshot.child("info").child(curUserKey).child("Name").getValue()).toString();
                    }

                    if(snapshot.child("info").child("Chat Profile Image Uri").getValue()!=null){
                        imageUri= Objects.requireNonNull(snapshot.child("info").child("Chat Profile Image Uri").getValue()).toString();
                    }

                    if(isSingleChat && snapshot.child("info").child(curUserKey).child("Chat Profile Image Uri").getValue()!=null){
                        imageUri = Objects.requireNonNull(snapshot.child("info").child(curUserKey).child("Chat Profile Image Uri").getValue()).toString();
                    }

                    if(snapshot.child("info").child("Number Of Users").getValue()!=null){
                        numberOfUsers =Integer.parseInt(Objects.requireNonNull(snapshot.child("info").child("Number Of Users").getValue()).toString());
                    }
                    if(snapshot.child("info").child("Last Message").getValue()!=null) {
                        lastMessageId = Objects.requireNonNull(snapshot.child("info").child("Last Message").getValue()).toString();

                        if (snapshot.child("Messages").child(lastMessageId).child("text").getValue() != null) {
                            lastMessageText = Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("text").getValue()).toString();
                        }

                        if (snapshot.child("Messages").child(lastMessageId).child("date").getValue() != null) {
                            String lastMessageDate = Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("date").getValue()).toString();
                            if (snapshot.child("Messages").child(lastMessageId).child("timestamp").getValue() != null) {
                                lastMessageTime = Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("timestamp").getValue()).toString();
                            }
                            if (!lastMessageDate.equals(curDate)) {
                                lastMessageTime = lastMessageTime + " " + lastMessageDate;
                            }
                        }


                        if (snapshot.child("Messages").child(lastMessageId).child("Sender").getValue() != null) {
                            lastSenderId = Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("Sender").getValue()).toString();
                            DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(lastSenderId).child("Name");
                            final String finalImageUri = imageUri;
                            final String finalLastMessageText = lastMessageText;
                            final String finalLastMessageTime = lastMessageTime;
                            final int finalNumberOfUsers1 = numberOfUsers;
                            final boolean finalIsSingleChat = isSingleChat;
                            mUserDB.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot thisSnapshot) {
                                    if(thisSnapshot.exists()){
                                        if(thisSnapshot.getValue()!=null){
                                            lastSenderName[0] = Objects.requireNonNull(thisSnapshot.getValue()).toString();
                                            ChatObject chatObject=new ChatObject(key, name[0], finalImageUri, finalLastMessageText,lastSenderName[0], finalLastMessageTime, finalNumberOfUsers1,lastMessageId, finalIsSingleChat);
                                            int indexOfObject=chatList.indexOf(chatObject);
                                            if(indexOfObject==-1){
                                                chatList.add(chatObject);
                                                mChatListAdapter.notifyItemInserted(chatList.size()-1);
                                            }
                                            else{
                                                chatList.remove(indexOfObject);
                                                chatList.add(0, chatObject);
                                                mChatListAdapter.notifyItemRangeChanged(0, indexOfObject + 1);
                                            }


                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getApplicationContext(),"something went wrong, please try again later last Message Sender",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        else{
                            ChatObject chatObject=new ChatObject(key, name[0],imageUri,numberOfUsers,isSingleChat);
                            chatList.add(chatObject);
                            mChatListAdapter.notifyItemInserted(chatList.size()-1);
                        }



                    }

                    else{
                        ChatObject chatObject=new ChatObject(key, name[0],imageUri,numberOfUsers,isSingleChat);
                        chatList.add(chatObject);
                        mChatListAdapter.notifyItemInserted(chatList.size()-1);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"something went wrong, please try again later get chat details",Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void requestUserPermission() {
        String[] permissions={Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_NETWORK_STATE};
        requestPermissions(permissions,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[1] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getApplicationContext(), "you didn't give the permission to access the contacts, So Fuck Off", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void viewProfile() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("userObject", curUser);
        startActivity(intent);
    }

    private void createNewGroup() {
        Intent intent = new Intent(this, CreateNewChatActivity.class);
        intent.putExtra("isSingleChatActivity", false);
        startActivity(intent);
    }

    private void singleChats() {
        Intent intent = new Intent(this, CreateNewChatActivity.class);
        intent.putExtra("isSingleChatActivity", true);
        startActivity(intent);
    }
    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,LogInActivity.class);
        startActivity(intent);
        finish();
    }



    private void initializeViews() {
    }
    private void initializeRecyclerViews() {

        mChatList=findViewById(R.id.recyclerViewListChats);
        mChatList.setHasFixedSize(false);
        mChatList.setNestedScrollingEnabled(false);

        mChatList.addItemDecoration(new DividerItemDecoration(mChatList.getContext(),DividerItemDecoration.VERTICAL));



        mChatListAdapter= new ChatAdapter(chatList,this);
        mChatList.setAdapter(mChatListAdapter);

        mChatListLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        mChatList.setLayoutManager(mChatListLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_chats_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logoutMenu:
                logOut();
                break;

            case R.id.createNewGroupMenu:
                createNewGroup();
                break;


            case R.id.viewProfileMenu:
                viewProfile();
                break;

            case R.id.singleChatsMenu:
                singleChats();
                break;


            default:
                Toast.makeText(getApplicationContext(), "please select a valid option", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }


}