package com.example.whatsappv50_23feb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;

    private EditText editTextPhone, editTextCode;

    private TextView textViewMessage;

    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;

    //code sent
    private String codeReceivedFromFirebase;

    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        signInButton = findViewById(R.id.signInButtonID);
        editTextPhone = findViewById(R.id.editTextPhoneID);
        editTextCode = findViewById(R.id.editTextCodeID);
        textViewMessage = findViewById(R.id.messageID);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(codeReceivedFromFirebase!=null){
                    verifyCredential(codeReceivedFromFirebase,editTextCode.getText().toString());
                }
                startVerification();

            }
        });

        //callback setup

        mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                textViewMessage.setText("logging in automatically..");

                signInWithPhoneCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                textViewMessage.setText("getting credential failed..");
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                textViewMessage.setText("receiving code from firebase..");
                codeReceivedFromFirebase = s;

            }
        };



    }

    private void verifyCredential(String codeReceivedFromFirebase, String toString) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeReceivedFromFirebase,toString);

        signInWithPhoneCredential(credential);

    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {

        textViewMessage.setText("verifying credential..");

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if(task.isSuccessful()){

                    textViewMessage.setText("verified");
                    userIsLoggedIn();


                }
            }
        });

    }

    private void userIsLoggedIn() {


        textViewMessage.setText("user is logged in");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null){

            Intent intent = new Intent(MainActivity.this,MainPageActivity.class);
            startActivity(intent);
            finish();


        }



    }




    private void startVerification() {

        textViewMessage.setVisibility(View.VISIBLE);
        textViewMessage.setText("start verifying..");


        PhoneAuthProvider.getInstance().verifyPhoneNumber(editTextPhone.getText().toString(),

                50,
                TimeUnit.SECONDS,
                this,
                mCallBack

                );



    }
}
