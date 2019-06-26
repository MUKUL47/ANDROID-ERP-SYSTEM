package com.example.erpp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmailNotification extends AppCompatActivity {
    String ID;
    EditText email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emailnotification);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels * .8),(int)(displayMetrics.heightPixels * .5));
        ID = getIntent().getStringExtra("id");
        email = findViewById(R.id.email);
        checkIfAlreadySubscribed();

    }

    private void checkIfAlreadySubscribed() {
        FirebaseDatabase.getInstance().getReference("Subscriptions").
                child("Emails").child(ID.substring(0,4)).child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null){
                    email.setHint(dataSnapshot.getValue(String.class));
                }else{
                    email.setHint("Not subscribed");
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }


    public void onClick(View view) {
        if(isInternetAvailable()){
            if( view.getId() == R.id.enable ){
                if( email.getText().toString().trim().length() == 0){
                    Toast.makeText(this,"Enter email first",Toast.LENGTH_LONG).show();
                }else{
                    setEmail(email.getText().toString());
                }
            }
            if( view.getId() == R.id.disable ){
                disableEmail();
            }
        }else{
            Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();
        }
    }

    private void disableEmail() {
        FirebaseDatabase.getInstance().getReference("Subscriptions").
                child("Emails").child(ID.substring(0,4)).child(ID).removeValue();
        email.setHint("Not subscribed");
        Toast.makeText(this,"Subscription disabled",Toast.LENGTH_LONG).show();
    }

    private void setEmail(String email) {
        FirebaseDatabase.getInstance().getReference("Subscriptions").
                child("Emails").child(ID.substring(0,4)).child(ID).setValue(email.toLowerCase());
        Toast.makeText(this,"Email updated",Toast.LENGTH_LONG).show();
    }
}
