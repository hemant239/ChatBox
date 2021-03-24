package com.hemant239.chatbox;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hemant239.chatbox.R;

public class LoadingActivity extends AppCompatActivity {

    Button mCancelUpload;
    static Context context;

    public static Context getContextOfActivity(){
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initializeViews();

        context=this;


        mCancelUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                setResult(RESULT_OK);
                finish();
            }
        });




    }

    private void initializeViews() {
        mCancelUpload=findViewById(R.id.cancelUploadButton);
    }
}