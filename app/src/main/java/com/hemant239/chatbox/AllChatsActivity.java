package com.hemant239.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hemant239.chatbox.chat.ChatAdapter;
import com.hemant239.chatbox.chat.ChatObject;

import java.util.ArrayList;
import java.util.Objects;

public class AllChatsActivity extends AppCompatActivity {


    private static RecyclerView.Adapter<ChatAdapter.ViewHolder> mChatListAdapter;
    RecyclerView mChatList;
    RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList;

    DatabaseReference mUserDB, mChatDB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);


        if(getIntent().getBooleanExtra("First time",false)){
            requestUserPermission();
        }

        chatList=new ArrayList<>();
        initializeViews();
        initializeRecyclerViews();
        getChatList();
    }

    final int CHANGE_PROFILE_PHOTO_CODE=1;
    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an image"),CHANGE_PROFILE_PHOTO_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case CHANGE_PROFILE_PHOTO_CODE:
                    String userId= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    final StorageReference profileStorage= FirebaseStorage.getInstance().getReference().child("ProfilePhotos").child(userId);
                    final DatabaseReference mUserDb=FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                    assert data != null;
                    final UploadTask uploadTask=profileStorage.putFile(Objects.requireNonNull(data.getData()));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mUserDb.child("Profile Image Uri").setValue(uri.toString());
                                }
                            });

                        }
                    });

                    break;

                case 100:
                    break;

                default:
                    Toast.makeText(getApplicationContext(),"something went wrong, please try again later onActivity result",Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void getChatList() {

        FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
        mUserDB= FirebaseDatabase.getInstance().getReference().child("Users");

        if(mUser!=null){
            mUserDB=mUserDB.child(mUser.getUid()).child("chat");
            mUserDB.addChildEventListener(new ChildEventListener() {
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


                    //Toast.makeText(getApplicationContext(),"on Data Change is called",Toast.LENGTH_SHORT).show();

                    //TODO: add toast messages to check if it runs for other chats.

                    if(snapshot.child("info").child("Name").getValue()!=null){
                        name[0] = Objects.requireNonNull(snapshot.child("info").child("Name").getValue()).toString();
                    }

                    if(snapshot.child("info").child("Number Of Users").getValue()!=null){
                        numberOfUsers =Integer.parseInt(Objects.requireNonNull(snapshot.child("info").child("Number Of Users").getValue()).toString());
                    }

                    if(snapshot.child("info").child("Chat Profile Image Uri").getValue()!=null){
                        imageUri= Objects.requireNonNull(snapshot.child("info").child("Chat Profile Image Uri").getValue()).toString();
                    }
                    if(snapshot.child("info").child("Last Message").getValue()!=null){
                        lastMessageId= Objects.requireNonNull(snapshot.child("info").child("Last Message").getValue()).toString();

                        if(snapshot.child("Messages").child(lastMessageId).child("text").getValue()!=null){
                            lastMessageText= Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("text").getValue()).toString();
                        }
                        if(snapshot.child("Messages").child(lastMessageId).child("timestamp").getValue()!=null){
                            lastMessageTime= Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("timestamp").getValue()).toString();
                        }

                        if(snapshot.child("Messages").child(lastMessageId).child("Sender").getValue()!=null){
                            lastSenderId= Objects.requireNonNull(snapshot.child("Messages").child(lastMessageId).child("Sender").getValue()).toString();
                            DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("Users").child(lastSenderId).child("Name");
                            final String finalImageUri = imageUri;
                            final String finalLastMessageText = lastMessageText;
                            final String finalLastMessageTime = lastMessageTime;
                            final int finalNumberOfUsers1 = numberOfUsers;
                            mUserDB.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot thisSnapshot) {
                                    if(thisSnapshot.exists()){
                                        if(thisSnapshot.getValue()!=null){
                                            lastSenderName[0] = Objects.requireNonNull(thisSnapshot.getValue()).toString();
                                            ChatObject chatObject=new ChatObject(key, name[0], finalImageUri, finalLastMessageText,lastSenderName[0], finalLastMessageTime, finalNumberOfUsers1,lastMessageId);
                                            //Toast.makeText(getApplicationContext(),"chat is added to list",Toast.LENGTH_SHORT).show();

                                            int indexOfObject=chatList.indexOf(chatObject);
                                            if(indexOfObject==-1){
                                                chatList.add(chatObject);
                                                mChatListAdapter.notifyItemInserted(chatList.size()-1);
                                            }
                                            else{
                                                chatList.set(indexOfObject,chatObject);
                                                mChatListAdapter.notifyItemChanged(indexOfObject);
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
                            ChatObject chatObject=new ChatObject(key, name[0],imageUri,numberOfUsers);
                            chatList.add(chatObject);
                            //Toast.makeText(getApplicationContext(),"chat is added to list",Toast.LENGTH_SHORT).show();
                            mChatListAdapter.notifyItemInserted(chatList.size()-1);
                        }



                    }

                    else{
                        ChatObject chatObject=new ChatObject(key, name[0],imageUri,numberOfUsers);
                        chatList.add(chatObject);
                        //Toast.makeText(getApplicationContext(),"chat is added to list",Toast.LENGTH_SHORT).show();
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

    private void requestUserPermission() {
        String[] permissions={Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS,Manifest.permission.ACCESS_NETWORK_STATE};
        requestPermissions(permissions,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[1]== PackageManager.PERMISSION_DENIED){
                Toast.makeText(getApplicationContext(),"you didn't give the permission to access the contacts, So Fuck Off",Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void createNewChat() {
        Intent intent=new Intent(this,CreateNewChatActivity.class);
        startActivity(intent);
        finish();
    }

    private void logOut() {

        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,LogInActivity.class);
        startActivity(intent);
        finish();

    }



    private void initializeViews() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_chats_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logoutMenu:
                logOut();
                break;

            case R.id.createNewChatMenu:
                createNewChat();
                break;


            case R.id.changeProfilePictureMenu:
                openGallery();
                break;


            default:
                Toast.makeText(getApplicationContext(),"sahi sahi select kar, zyaada dimaag mat chala",Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }


}