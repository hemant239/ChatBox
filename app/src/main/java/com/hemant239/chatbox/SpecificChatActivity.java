package com.hemant239.chatbox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.hemant239.chatbox.message.MessageAdapter;
import com.hemant239.chatbox.message.MessageObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class SpecificChatActivity extends AppCompatActivity {
    
    EditText mMessageText;
    Button mSendMessage,
            mAddMedia;

    TextView chatName;
    ImageView chatPhoto;

    RecyclerView mMessageList;
    RecyclerView.Adapter<MessageAdapter.ViewHolder> mMessageAdapter;
    RecyclerView.LayoutManager mMessageListLayoutManager;




    ArrayList<MessageObject> messageList;

    String key;

    String mediaAdded;

    int numberOfUsers;

    boolean isImageChanged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.app_bar_specific_chat);
        View view=getSupportActionBar().getCustomView();

        chatName=view.findViewById(R.id.chatAppName);
        chatPhoto=view.findViewById(R.id.chatAppProfileImage);

        chatPhoto.setClipToOutline(true);

        String name=getIntent().getStringExtra("Chat Name");
        final String imageUri=(getIntent().getStringExtra("Image Uri"));
        numberOfUsers=getIntent().getIntExtra("Number Of Users",0);


        chatName.setText(name);
        Glide.with(getApplicationContext()).load(Uri.parse(imageUri)).into(chatPhoto);


        chatPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("URI",imageUri);
                startActivity(intent);

            }
        });





        
        initializeViews();
        messageList=new ArrayList<>();
        initializeRecyclerViews();

        mediaAdded="";

        key=getIntent().getStringExtra("Chat Key");


        
        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(key);
            }
        });

        mAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalleryToAddMedia();
            }
        });

        getMessageList(key);
    }
    final int ADD_MEDIA_CODE=2;
    private void openGalleryToAddMedia() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an image"),ADD_MEDIA_CODE);
    }

    final int CHANGE_CHAT_PHOTO_CODE=1;
    private void openGalleryToChangeProfilePhoto() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an image"),CHANGE_CHAT_PHOTO_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case CHANGE_CHAT_PHOTO_CODE:

                    final StorageReference profileStorage= FirebaseStorage.getInstance().getReference().child("ChatProfilePhotos").child(key);
                    final DatabaseReference mChatDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("info");

                    assert data != null;
                    final UploadTask uploadTask=profileStorage.putFile(Objects.requireNonNull(data.getData()));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mChatDb.child("Chat Profile Image Uri").setValue(uri.toString());
                                    isImageChanged=true;
                                }
                            });

                        }
                    });

                    break;


                case ADD_MEDIA_CODE:
                    assert data != null;
                    mediaAdded= Objects.requireNonNull(data.getData()).toString();
                    break;





                default:
                    Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void getMessageList(final String key) {

        FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mMessageDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages");

        if(mUser!=null){

            mMessageDb.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if(snapshot.exists()){
                        String senderId= Objects.requireNonNull(snapshot.child("Sender").getValue()).toString();
                        String text="";
                        String imageUri="";
                        String time="";

                        if(snapshot.child("text").getValue()!=null){
                            text= Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                        }
                        if(snapshot.child("Image Uri").getValue()!=null){
                            imageUri= Objects.requireNonNull(snapshot.child("Image Uri").getValue()).toString();
                        }
                        if(snapshot.child("timestamp").getValue()!=null){
                            time= Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString();
                        }


                        FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("info").child("Last Message").setValue(snapshot.getKey());
                        getMessageUserData(snapshot.getKey(),text,imageUri,senderId,time);
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

    private void getMessageUserData(final String messageKey, final String text,final String imageUri, final String senderId,final String time) {
        DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("Users").child(senderId);
        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String senderName= Objects.requireNonNull(snapshot.child("Name").getValue()).toString();

                    MessageObject newMessage=new MessageObject(messageKey,text,imageUri,senderId,senderName,time);
                    messageList.add(newMessage);

                    mMessageListLayoutManager.scrollToPosition(messageList.size()-1);
                    mMessageAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendMessage(String key) {
        final DatabaseReference mChatDb= FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages");
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();
        final String messageId=mChatDb.push().getKey();
        final HashMap<String,Object> newMessageMap=new HashMap<>();

        if(mUser!=null && (!mMessageText.getText().toString().equals("") || !mediaAdded.equals(""))){

            newMessageMap.put("Sender",mUser.getUid());

            if(!mMessageText.getText().toString().equals(""))
                newMessageMap.put("text",mMessageText.getText().toString());

            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
            String time=simpleDateFormat.format(Calendar.getInstance().getTime());
            newMessageMap.put("timestamp",time);

            assert messageId != null;
            if(!mediaAdded.equals("")){
                final StorageReference mediaStorage=FirebaseStorage.getInstance().getReference().child("ChatPhotos").child(key).child(messageId);
                UploadTask uploadTask= mediaStorage.putFile(Uri.parse(mediaAdded));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mediaStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("Image Uri",uri.toString());
                                mChatDb.child(messageId).updateChildren(newMessageMap);
                            }
                        });
                    }
                });
            }
            else{
                mChatDb.child(messageId).updateChildren(newMessageMap);
            }

            mMessageText.setText("");
            mediaAdded="";
        }


    }

    private void initializeRecyclerViews() {

        mMessageList=findViewById(R.id.recyclerViewListMessages);
        mMessageList.setHasFixedSize(false);
        mMessageList.setNestedScrollingEnabled(false);



        mMessageAdapter= new MessageAdapter(messageList,this,numberOfUsers);
        mMessageList.setAdapter(mMessageAdapter);

        mMessageListLayoutManager=new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        mMessageList.setLayoutManager(mMessageListLayoutManager);
    }

    private void initializeViews() {
        
        mMessageText=findViewById(R.id.messageText);
        mSendMessage=findViewById(R.id.sendMessage);
        mAddMedia=findViewById(R.id.addMedia);
    }


    @Override
    public void onBackPressed() {
            Intent intent=new Intent(this,AllChatsActivity.class);
            startActivity(intent);
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.specific_chat_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.changeProfilePictureMenu:
                openGalleryToChangeProfilePhoto();
                break;


            case R.id.clearChat:
                DatabaseReference mMessageDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages");
                mMessageDb.setValue(null);
                recreate();
                break;


            default:
                Toast.makeText(getApplicationContext(),"sahi sahi select kar, zyaada dimaag mat chala",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}