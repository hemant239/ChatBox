package com.hemant239.chatbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class LogInActivity extends AppCompatActivity {

    EditText    mPhoneNumber,
                mName,
                mVerificationCode;

    Button      mSend;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseAuth mAuth;

    String mVerificationId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        mAuth=FirebaseAuth.getInstance();
        userLoggedIn();
        setContentView(R.layout.activity_login);


        initializeViews();


        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVerificationId!=null){
                    verifyPhoneNumberWithCode();
                }
                else{
                    startPhoneNumberVerification();
                }
            }
        });


        mCallbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                findViewById(R.id.codeLayout).setVisibility(View.VISIBLE);
                mVerificationId=verificationId;
                mSend.setText("Verify Code");
            }
        };

    }
    //TODO:  Add conditions on inputs.
    private void verifyPhoneNumberWithCode() {
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(mVerificationId,mVerificationCode.getText().toString());
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser mUser=FirebaseAuth.getInstance().getCurrentUser();
                    if(mUser!=null){
                        final DatabaseReference mUserDB= FirebaseDatabase.getInstance().getReference().child("Users").child(mUser.getUid());
                        mUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()){
                                    HashMap<String,Object> mapUserInfo=new HashMap<>();
                                    mapUserInfo.put("Phone Number",mPhoneNumber.getText().toString());
                                    mapUserInfo.put("Name",mName.getText().toString());
                                    mUserDB.updateChildren(mapUserInfo);
                                }
                                userLoggedIn();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(),"something went wrong, please try again later",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        });
    }

    private void userLoggedIn() {
        FirebaseUser mUser= mAuth.getCurrentUser();
        if(mUser!=null){
            Intent intent= new Intent(this, AllChatsActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void startPhoneNumberVerification() {
        PhoneAuthOptions phoneAuthOptions= PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(mPhoneNumber.getText().toString())
                .setTimeout(90L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);
    }

    private void initializeViews() {
        mPhoneNumber        = findViewById(R.id.phoneNumber);
        mName               = findViewById(R.id.name);
        mVerificationCode   = findViewById(R.id.verificationCode);

        mSend       = findViewById((R.id.buttonSend));
    }
}