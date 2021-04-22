package com.hemant239.chatbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.hemant239.chatbox.message.MessageAdapter;
import com.hemant239.chatbox.message.MessageObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class SpecificChatActivity extends AppCompatActivity {

    //TODO:  Add functionality to delete single messages for everyone and single user.
    
    EditText mMessageText;
    Button mSendMessage,
            mAddMedia;

    TextView chatName;
    ImageView chatPhoto;

    TextView onDateScrolling;

    RecyclerView mMessageList;
    RecyclerView.Adapter<MessageAdapter.ViewHolder> mMessageAdapter;
    RecyclerView.LayoutManager mMessageListLayoutManager;

    ArrayList<MessageObject> messageList;

    StorageReference profileStorage;
    UploadTask uploadTask;

    String key;

    String mediaAdded;

    String lastMessageId;

    int numberOfUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specific_chat);

        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.app_bar_specific_chat);
        View view=getSupportActionBar().getCustomView();

        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float density=displayMetrics.density;


        chatName=view.findViewById(R.id.chatAppName);
        chatPhoto=view.findViewById(R.id.chatAppProfileImage);

        chatPhoto.setClipToOutline(true);

        String name=getIntent().getStringExtra("Chat Name");
        final String imageUri=(getIntent().getStringExtra("Image Uri"));
        numberOfUsers=getIntent().getIntExtra("Number Of Users",0);
        lastMessageId=getIntent().getStringExtra("Last Message ID");


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
        initializeRecyclerViews(density);

        mediaAdded="";

        key=getIntent().getStringExtra("Chat Key");

        getMessageList(key);


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

        mMessageList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState==RecyclerView.SCROLL_STATE_IDLE){
                    onDateScrolling.setVisibility(View.GONE);
                }
                else if(newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    int position=((LinearLayoutManager)mMessageListLayoutManager).findFirstCompletelyVisibleItemPosition();
                    onDateScrolling.setText(messageList.get(position).getDate());
                    onDateScrolling.setVisibility(View.VISIBLE);
                }
            }
        });
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
    final int CANCEL_UPLOAD_TASK=3;
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

                    profileStorage= FirebaseStorage.getInstance().getReference().child("ChatProfilePhotos").child(key);
                    final DatabaseReference mChatDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("info");

                    assert data != null;
                    uploadTask=profileStorage.putFile(Objects.requireNonNull(data.getData()));
                    Intent intent =new Intent(getApplicationContext(),LoadingActivity.class);
                    startActivityForResult(intent,CANCEL_UPLOAD_TASK);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            profileStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mChatDb.child("Chat Profile Image Uri").setValue(uri.toString());
                                    Glide.with(getApplicationContext()).load(uri).into((ImageView) findViewById(R.id.chatAppProfileImage));
                                    ((LoadingActivity)LoadingActivity.context).finish();
                                }
                            });
                        }
                    });

                    break;


                case ADD_MEDIA_CODE:
                    assert data != null;
                    mediaAdded= Objects.requireNonNull(data.getData()).toString();
                    break;

                case CANCEL_UPLOAD_TASK:
                    if(uploadTask.isComplete()){
                        profileStorage.delete();
                    }
                    uploadTask.cancel();
                    break;

                default:
                    Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void getMessageList(final String key) {

        FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mMessageDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages");

        if(mUser!=null){

            mMessageDb.orderByPriority().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if(Objects.requireNonNull(snapshot.getValue()).toString().equals("true")) {
                            mMessageDb.setValue(null);
                            recreate();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            mMessageDb.orderByPriority().addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        int numberOfMessages= (int) snapshot.getChildrenCount();
                        for(int i=0;i<numberOfMessages;i++){
                            messageList.add(new MessageObject());
                        }

                        for(DataSnapshot childSnapshot:snapshot.getChildren()){
                            if(childSnapshot.exists()){
                                numberOfMessages--;
                                String senderId= Objects.requireNonNull(childSnapshot.child("Sender").getValue()).toString();
                                String text="";
                                String imageUri="";
                                String time="";
                                String date="";

                                if(childSnapshot.child("text").getValue()!=null){
                                    text= Objects.requireNonNull(childSnapshot.child("text").getValue()).toString();
                                }
                                if(childSnapshot.child("Image Uri").getValue()!=null){
                                    imageUri= Objects.requireNonNull(childSnapshot.child("Image Uri").getValue()).toString();
                                }
                                if(childSnapshot.child("timestamp").getValue()!=null){
                                    time= Objects.requireNonNull(childSnapshot.child("timestamp").getValue()).toString();
                                }
                                if(childSnapshot.child("date").getValue()!=null) {
                                    date = Objects.requireNonNull(childSnapshot.child("date").getValue()).toString();
                                }
                                getMessageUserData(childSnapshot.getKey(),text,imageUri,senderId,time,date,false,numberOfMessages);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            mMessageDb.limitToFirst(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    if(snapshot.exists() && !snapshot.getKey().equals(lastMessageId)){
                        String senderId= Objects.requireNonNull(snapshot.child("Sender").getValue()).toString();
                        String text="";
                        String imageUri="";
                        String time="";
                        String date="";

                        if(snapshot.child("text").getValue()!=null){
                            text= Objects.requireNonNull(snapshot.child("text").getValue()).toString();
                        }
                        if(snapshot.child("Image Uri").getValue()!=null){
                            imageUri= Objects.requireNonNull(snapshot.child("Image Uri").getValue()).toString();
                        }
                        if(snapshot.child("timestamp").getValue()!=null){
                            time= Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString();
                        }
                        if(snapshot.child("date").getValue()!=null){
                            date= Objects.requireNonNull(snapshot.child("date").getValue()).toString();
                        }

                        getMessageUserData(snapshot.getKey(),text,imageUri,senderId,time,date,true,0);
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

    private void getMessageUserData(final String messageKey, final String text, final String imageUri, final String senderId, final String time, final String date, final boolean isNewMessage, final int index) {
        DatabaseReference mUserDB=FirebaseDatabase.getInstance().getReference().child("Users").child(senderId);
        mUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String senderName= Objects.requireNonNull(snapshot.child("Name").getValue()).toString();

                    MessageObject newMessage=new MessageObject(messageKey,text,imageUri,senderId,senderName,time,date);



                    if(isNewMessage){
                        messageList.add(newMessage);
                        mMessageListLayoutManager.scrollToPosition(messageList.size()-1);
                        mMessageAdapter.notifyItemInserted(messageList.size()-1);
                    }
                    else{
                        messageList.set(index,newMessage);
                        mMessageListLayoutManager.scrollToPosition(messageList.size()-1);
                        mMessageAdapter.notifyItemChanged(index);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();
            }
        });
    }

//
    private void sendMessage(String key) {
        final DatabaseReference mChatDb= FirebaseDatabase.getInstance().getReference().child("Chats").child(key);
        FirebaseUser mUser= FirebaseAuth.getInstance().getCurrentUser();

        final HashMap<String,Object> newMessageMap=new HashMap<>();

        if(mUser!=null && (!mMessageText.getText().toString().equals("") || !mediaAdded.equals(""))){

            final String messageId=mChatDb.child("Messages").push().getKey();


            newMessageMap.put("Messages/"+messageId+"/Sender",mUser.getUid());

            newMessageMap.put("info/Last Message",messageId);
            if(!mMessageText.getText().toString().equals("")) {
                newMessageMap.put("Messages/" + messageId + "/text", mMessageText.getText().toString());
            }

            Calendar calendar=Calendar.getInstance();
            final Date date=calendar.getTime();
            assert messageId != null;



            SimpleDateFormat simpleDateFormatTime=new SimpleDateFormat("h:mm a");
            String time=simpleDateFormatTime.format(date);
            SimpleDateFormat simpleDateFormatDate=new SimpleDateFormat("EEE, MMMM dd, yyyy");
            String dateTemp=simpleDateFormatDate.format(date);

            newMessageMap.put("Messages/"+messageId+"/timestamp",time.toUpperCase());
            newMessageMap.put("Messages/"+messageId+"/date",dateTemp.toUpperCase());
            if(!mediaAdded.equals("")){
                final StorageReference mediaStorage=FirebaseStorage.getInstance().getReference().child("ChatPhotos").child(key).child(messageId);
                UploadTask uploadTask= mediaStorage.putFile(Uri.parse(mediaAdded));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        mediaStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                newMessageMap.put("Messages/"+messageId+"/Image Uri",uri.toString());
                                mChatDb.updateChildren(newMessageMap);
                                mChatDb.child("Messages").child(messageId).setPriority(-date.getTime());
                            }
                        });
                    }
                });
            }
            else{
                mChatDb.updateChildren(newMessageMap);
                mChatDb.child("Messages").child(messageId).setPriority(-date.getTime());
            }
            mMessageText.setText("");
            mediaAdded="";
        }
    }

    private void initializeRecyclerViews(float density) {

        mMessageList=findViewById(R.id.recyclerViewListMessages);
        mMessageList.setHasFixedSize(false);
        mMessageList.setNestedScrollingEnabled(false);



        mMessageAdapter= new MessageAdapter(messageList,this,numberOfUsers,density);
        mMessageList.setAdapter(mMessageAdapter);
        mMessageListLayoutManager=new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        mMessageList.setLayoutManager(mMessageListLayoutManager);
    }

    private void initializeViews() {
        
        mMessageText=findViewById(R.id.messageText);
        mSendMessage=findViewById(R.id.sendMessage);
        mAddMedia=findViewById(R.id.addMedia);
        onDateScrolling=findViewById(R.id.onScrollingDate);
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
            case R.id.changeChatPictureMenu:
                openGalleryToChangeProfilePhoto();
                break;


            case R.id.clearChat:
                DatabaseReference mMessageDb=FirebaseDatabase.getInstance().getReference().child("Chats").child(key).child("Messages");
                mMessageDb.setValue(true);
                recreate();
                break;


            default:
                Toast.makeText(getApplicationContext(),"sahi sahi select kar, zyaada dimaag mat chala",Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}