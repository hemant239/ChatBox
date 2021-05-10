package com.hemant239.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hemant239.chatbox.user.UserObject;

public class UserDetailsActivity extends AppCompatActivity {


    TextView    mUserName,
                mUserPhone,
                mUserStatus;

    ImageView mUserImage;

    String  userName,
            userPhone,
            userStatus,
            userImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        UserObject userObject= (UserObject) getIntent().getSerializableExtra("userObject");
        assert userObject != null;
        userName=userObject.getName();
        userPhone=userObject.getPhoneNumber();
        userStatus=userObject.getStatus();
        userImage=userObject.getProfileImageUri();

        initializeViews();

        mUserName.setText(userName);
        mUserPhone.setText(userPhone);
        mUserStatus.setText(userStatus);

        if(!userImage.equals("")) {
            mUserImage.setClipToOutline(true);
            Glide.with(this).load(Uri.parse(userImage)).into(mUserImage);
            mUserImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                    intent.putExtra("URI", userImage);
                    startActivity(intent);
                }
            });
        }
    }

    private void initializeViews() {
        mUserName   =findViewById(R.id.userDetailName);
        mUserPhone  =findViewById(R.id.userDetailPhone);
        mUserStatus =findViewById(R.id.userDetailStatus);
        mUserImage  =findViewById(R.id.userDetailProfileImage);
    }
}