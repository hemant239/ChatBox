package com.hemant239.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hemant239.chatbox.user.UserAdapter;
import com.hemant239.chatbox.user.UserObject;
import com.hemant239.chatbox.utils.CountryToPhonePrefix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateNewChatActivity extends AppCompatActivity {

    Button mCancelButton,mCreateChat;
    EditText mChatName;

    RecyclerView mUserList;
    RecyclerView.Adapter<UserAdapter.ViewHolder> mUserListAdapter;
    RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> contactsList;
    ArrayList<UserObject> userList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_chat);


        userList=new ArrayList<>();
        contactsList=new ArrayList<>();


        initializeViews();
        initializeRecyclerViews();

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AllChatsActivity.class));
            }
        });



        mCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatName!=null) {
                    String s = mChatName.getText().toString().trim();
                    if (s.length() != 0) {
                        createChat();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Give the name to the chat mf", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        getContactList();
    }

    private void createChat() {

        DatabaseReference mChatDB= FirebaseDatabase.getInstance().getReference().child("Chats");
        String key= mChatDB.push().getKey();

        DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("Users");

        mChatDB=mChatDB.child(key).child("info");

        HashMap<String,Object> mChatInfo=new HashMap<>();

        mChatInfo.put("Name",mChatName.getText().toString());
        mChatInfo.put("ID",key);

        mChatDB.updateChildren(mChatInfo);

        mChatDB=mChatDB.child("user");
        mChatInfo=new HashMap<>();

        for(UserObject user:userList){
            if(user.isSelected()){
                if(user.getUid()!=null) {
                    mChatInfo.put(user.getUid(), "true");
                    mUserDB.child(user.getUid()).child("chat").child(key).setValue("true");
                }
            }
        }

        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mUser!=null) {
            mChatInfo.put(mUser.getUid(),"true");
            mUserDB.child(mUser.getUid()).child("chat").child(key).setValue("true");
        }


        mChatDB.updateChildren(mChatInfo);

    }

    private void getContactList() {

        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        //Cursor cursor1=getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        while(cursor!=null && cursor.moveToNext()){
            String phoneNumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));


            phoneNumber=phoneNumber.replace(" ","");
            phoneNumber=phoneNumber.replace("-","");
            phoneNumber=phoneNumber.replace("(","");
            phoneNumber=phoneNumber.replace(")","");
            phoneNumber=phoneNumber.replace("/","");
            phoneNumber=phoneNumber.replace("#","");

            if(phoneNumber.charAt(0)!='+'){
                phoneNumber = getIsoPrefix()+phoneNumber;
            }



            if(phoneNumber!=null && name!=null){
                UserObject contactUser=new UserObject(name,phoneNumber);
                checkUserDetails(contactUser);
                
            }
        }
    }

    private String getIsoPrefix() {

        String iso="";

        TelephonyManager telephonyManager=(TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if(telephonyManager!=null){
            iso=CountryToPhonePrefix.prefixFor(telephonyManager.getNetworkCountryIso());
        }

        return iso;

    }

    private void checkUserDetails(final UserObject contactUser) {
        DatabaseReference mUserDB= FirebaseDatabase.getInstance().getReference().child("Users");
        Query query=mUserDB.orderByChild("Phone Number").equalTo(contactUser.getPhoneNumber());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot childSnapshot:snapshot.getChildren()){
                        if(childSnapshot.getKey()!=null && childSnapshot.child("Name").getValue()!=null  && childSnapshot.child("Phone Number").getValue()!=null ){
                            if(childSnapshot.child("Profile Image Uri").getValue()!=null){
                                UserObject user=new UserObject(childSnapshot.getKey(),contactUser.getName(),contactUser.getPhoneNumber(),childSnapshot.child("Profile Image Uri").getValue().toString());
                                userList.add(user);
                            }
                            else{
                                UserObject user=new UserObject(childSnapshot.getKey(),contactUser.getName(),contactUser.getPhoneNumber());
                                userList.add(user);
                            }

                            mUserListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initializeRecyclerViews() {
        mUserList=findViewById(R.id.recyclerViewList);
        mUserList.setHasFixedSize(false);
        mUserList.setNestedScrollingEnabled(false);



        mUserListAdapter= new UserAdapter(userList,getApplicationContext());
        mUserList.setAdapter(mUserListAdapter);

        mUserListLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
    }


    private void initializeViews() {
        mCancelButton=findViewById(R.id.cancelButton);
        mCreateChat = findViewById(R.id.newChat);

        mChatName=findViewById(R.id.chatName);

    }
}