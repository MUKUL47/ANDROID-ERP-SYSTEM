package com.example.erpp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.util.Strings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText ID,PASS;
    boolean once = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ID   = findViewById(R.id.id);
        PASS = findViewById(R.id.password);
        (findViewById(R.id.login)).setOnClickListener(this);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();  }

    }
    @Override
    public void onClick(View v)
    {   if(isInternetAvailable()){
            if( v.getId() == R.id.admins ){
                startActivity(new Intent(this,admins.class));
            }
            else if(ID.getText().toString().toUpperCase() != "ADMIN" && ID.getText().toString().length() < 5){
                Toast.makeText(Login.this,"INVALID ID",Toast.LENGTH_LONG).show();
            }
            else{
                    FirebaseDatabase.getInstance().getReference().child(ID.getText().toString().substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String id = dataSnapshot.child(ID.getText().toString().toUpperCase()).getValue(String.class);
                            if( id == null){
                                Toast.makeText(Login.this,"ID NOT REGISTERED (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(dataSnapshot.child(ID.getText().toString().toUpperCase()).
                                        getValue(String.class)
                                        .equalsIgnoreCase(PASS.getText().toString())){
                                    startActivity(new Intent(Login.this,Student.class).putExtra("id",ID.getText().toString().toUpperCase()));
                                }else{
                                    Toast.makeText(Login.this,"INCORRECT ERP PASSWORD (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
            }

        }else{
            Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();
        }
    }

    public boolean isInternetAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }
    public boolean checkValidityOfId(String id){
        return Pattern.compile("[0-9]+[a-zA-z]*+[0-1]*").matcher(id).matches();
    }

}