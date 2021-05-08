package com.hemant239.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class UserDetails extends AppCompatActivity {


    TextView    mUserName,
                mUserPhone,
                mUserStatus;

    ImageView mUserImage;
    String imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        imageUri="";

        mUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(), ImageViewActivity.class);
                intent.putExtra("URI",imageUri);
                startActivity(intent);

            }
        });





        initializeViews();
    }

    private void initializeViews() {
        mUserName   =findViewById(R.id.userDetailName);
        mUserPhone  =findViewById(R.id.userDetailPhone);
        mUserStatus =findViewById(R.id.userDetailStatus);
        mUserImage  =findViewById(R.id.userDetailProfileImage);
    }
}