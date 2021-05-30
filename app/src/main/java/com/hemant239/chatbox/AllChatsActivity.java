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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AllChatsActivity extends AppCompatActivity {


    private static RecyclerView.Adapter<ChatAdapter.ViewHolder> mChatListAdapter;
    RecyclerView mChatList;
    RecyclerView.LayoutManager mChatListLayoutManager;

    ArrayList<ChatObject> chatList;

    public static UserObject curUser;
    static HashMap<String, UserObject> allContacts;
    static Context context;
    String curDate;
    DatabaseReference mUserDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);
        context = this;
        curUser = new UserObject();
        curUser = (UserObject) getIntent().getSerializableExtra("curUser");


        allContacts = new HashMap<>();
        if (getIntent().getBooleanExtra("First time", false)) {
            requestUserPermission();
        } else {
            File contactsFile = new File(context.getFilesDir(), "contacts.ser");
            try {
                FileInputStream contactFileInputStream = new FileInputStream(contactsFile);
                ObjectInputStream contactObjectInputStream = new ObjectInputStream(contactFileInputStream);
                allContacts = (HashMap<String, UserObject>) contactObjectInputStream.readObject();
                contactObjectInputStream.close();
                contactFileInputStream.close();


                AllContacts contacts = new AllContacts(context, allContacts);
                contacts.getAllContacts();


            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(getApplicationContext(), "no file", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("EEE, MMM dd, yyyy");
        curDate = simpleDateFormatDate.format(date).toUpperCase();

        chatList = new ArrayList<>();
        initializeViews();
        initializeRecyclerViews();
        getChatList();
    }


    @Override
    protected void onDestroy() {
        File contactFile = new File(context.getFilesDir(), "contacts.ser");
        File userFile = new File(context.getFilesDir(), "user.ser");
        try {
            FileOutputStream contactFileOutputStream = new FileOutputStream(contactFile);
            ObjectOutputStream contactObjectOutputStream = new ObjectOutputStream(contactFileOutputStream);
            contactObjectOutputStream.writeObject(allContacts);

            contactObjectOutputStream.flush();
            contactObjectOutputStream.close();
            contactFileOutputStream.close();

            FileOutputStream userFileOutputStream = new FileOutputStream(userFile);
            ObjectOutputStream userObjectOutputStream = new ObjectOutputStream(userFileOutputStream);
            userObjectOutputStream.writeObject(curUser);

            userObjectOutputStream.flush();
            userObjectOutputStream.close();
            userFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
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
        final DatabaseReference chatDb = FirebaseDatabase.getInstance().getReference().child("Chats").child(key);
        chatDb.child("info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {

                final String[] name = {""};
                final String[] imageUri = {""};
                final int[] numberOfUsers = {0};
                boolean isSingleChat = false;
                String curUserKey = curUser.getUid();

                if (snapshot.child("isSingleChat").getValue() != null) {
                    isSingleChat = true;
                }
                if (snapshot.child("Name").getValue() != null) {
                    name[0] = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                }
                if (snapshot.child("Chat Profile Image Uri").getValue() != null) {
                    imageUri[0] = Objects.requireNonNull(snapshot.child("Chat Profile Image Uri").getValue()).toString();
                }
                if (snapshot.child(curUserKey).child("Name").getValue() != null) {
                    name[0] = Objects.requireNonNull(snapshot.child(curUserKey).child("Name").getValue()).toString();
                }
                if (snapshot.child(curUserKey).child("Chat Profile Image Uri").getValue() != null) {
                    imageUri[0] = Objects.requireNonNull(snapshot.child(curUserKey).child("Chat Profile Image Uri").getValue()).toString();
                }
                if (snapshot.child("Number Of Users").getValue() != null) {
                    numberOfUsers[0] = Integer.parseInt(Objects.requireNonNull(snapshot.child("Number Of Users").getValue()).toString());
                }
                if (allContacts.get(name[0]) != null) {
                    name[0] = Objects.requireNonNull(allContacts.get(name[0])).getName();
                }

                chatDb.child("info/Name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot nameSnapshot) {
                        if (nameSnapshot.exists()) {
                            ChatObject tempChat = new ChatObject(key);
                            int indexOfChat = chatList.indexOf(tempChat);

                            if (indexOfChat > -1) {
                                chatList.get(indexOfChat).setName(Objects.requireNonNull(nameSnapshot.getValue()).toString());
                                mChatListAdapter.notifyItemChanged(indexOfChat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                chatDb.child("info").child(curUserKey).child("Chat Profile Image Uri").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                        if (imageSnapshot.exists()) {
                            ChatObject tempChat = new ChatObject(key);
                            int indexOfChat = chatList.indexOf(tempChat);

                            if (indexOfChat > -1) {
                                chatList.get(indexOfChat).setImageUri(Objects.requireNonNull(imageSnapshot.getValue()).toString());
                                mChatListAdapter.notifyItemChanged(indexOfChat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                chatDb.child("info/Chat Profile Image Uri").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot imageSnapshot) {
                        if (imageSnapshot.exists()) {
                            ChatObject tempChat = new ChatObject(key);
                            int indexOfChat = chatList.indexOf(tempChat);
                            if (indexOfChat > -1) {
                                chatList.get(indexOfChat).setImageUri(Objects.requireNonNull(imageSnapshot.getValue()).toString());
                                mChatListAdapter.notifyItemChanged(indexOfChat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final boolean finalIsSingleChat = isSingleChat;
                chatDb.child("info").child("Last Message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot lastMessageSnapshot) {
                        if (lastMessageSnapshot.exists()) {
                            String lastMessageId = Objects.requireNonNull(lastMessageSnapshot.getValue()).toString();
                            getMessageData(key, name[0], imageUri[0], numberOfUsers[0], finalIsSingleChat, lastMessageId);
                        } else {
                            ChatObject chatObject = new ChatObject(key, name[0], imageUri[0], numberOfUsers[0], finalIsSingleChat);
                            chatList.add(chatObject);
                            mChatListAdapter.notifyItemInserted(chatList.size() - 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMessageData(final String key, final String name, final String imageUri, final int numberOfUsers, final boolean finalIsSingleChat, final String lastMessageId) {
        DatabaseReference messageDB = FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages").child(lastMessageId);
        messageDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String lastSenderId = "";
                    String lastSenderPhone = "";
                    String lastMessageText = "Photo";
                    String lastMessageTime = "";
                    String lastMessageDate = "";

                    if (snapshot.child("Sender").getValue() != null) {
                        lastSenderId = Objects.requireNonNull(snapshot.child("Sender").getValue()).toString();
                    }
                    if (snapshot.child("Sender Phone").getValue() != null) {
                        lastSenderPhone = Objects.requireNonNull(snapshot.child("Sender Phone").getValue()).toString();
                    }
                    if (snapshot.child("text").getValue() != null) {
                        lastMessageText = Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                    }
                    if (snapshot.child("timestamp").getValue() != null) {
                        lastMessageTime = Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString();
                    }
                    if (snapshot.child("date").getValue() != null) {
                        lastMessageDate = Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                    }
                    if (!lastMessageDate.equals(curDate)) {
                        lastMessageTime = lastMessageTime + " " + lastMessageDate;
                    }
                    if (allContacts.get(lastSenderPhone) != null) {
                        lastSenderPhone = Objects.requireNonNull(allContacts.get(lastSenderPhone)).getPhoneNumber();
                    }

                    ChatObject chatObject = new ChatObject(key, name, imageUri, lastMessageText, lastSenderPhone, lastSenderId, lastMessageTime, numberOfUsers, lastMessageId, finalIsSingleChat);
                    int indexOfObject = chatList.indexOf(chatObject);
                    if (indexOfObject == -1) {
                        chatList.add(chatObject);
                        mChatListAdapter.notifyItemInserted(chatList.size() - 1);
                    } else {
                        chatList.remove(indexOfObject);
                        chatList.add(0, chatObject);
                        mChatListAdapter.notifyItemRangeChanged(0, indexOfObject + 1);
                    }
                } else {
                    getMessageData(key, name, imageUri, numberOfUsers, finalIsSingleChat, lastMessageId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            AllContacts contacts = new AllContacts(context, allContacts);
            contacts.getAllContacts();
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


        mChatListAdapter = new ChatAdapter(chatList, this);
        mChatListAdapter.setHasStableIds(true);
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