package com.hemant239.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageViewActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        initializeViews();

        String uri=getIntent().getStringExtra("URI");
        assert uri != null;
        if(!uri.toString().equals("")) {
            Glide.with(this).load(Uri.parse(uri)).into(imageView);
        }
    }

    private void initializeViews() {
        imageView=findViewById(R.id.imageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}