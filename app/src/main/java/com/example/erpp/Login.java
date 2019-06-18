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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText ID,PASS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backgroundService();
        NotificationManagerCompat.from(this).cancel(1);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ID   = findViewById(R.id.id);
        PASS = findViewById(R.id.password);
        (findViewById(R.id.login)).setOnClickListener(this);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();  }

    }
    public void backgroundService(){
        startService(new Intent(getApplicationContext(),NotificationCenter.class));
        WorkManager.getInstance().enqueue(new PeriodicWorkRequest.Builder(
                WorkManage.class,15,TimeUnit.MINUTES
        ).build());
    }
    @Override
    public void onClick(View v)
    {   if(isInternetAvailable()){
            if(ID.getText().toString().toUpperCase() != "ADMIN" && ID.getText().toString().length() < 5){
                Toast.makeText(Login.this,"INVALID ID",Toast.LENGTH_LONG).show();
            }
            else{
                FirebaseDatabase.getInstance().getReference().child(ID.getText().toString().substring(0,4)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id = dataSnapshot.child(ID.getText().toString()).getValue(String.class);
                        if( id == null){
                            Toast.makeText(Login.this,"ID NOT REGISTERED (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                        }
                        else{
                            if(dataSnapshot.child(ID.getText().toString()).
                                    getValue(String.class)
                                    .equalsIgnoreCase(PASS.getText().toString())){
                                startActivity(new Intent(Login.this,Student.class).putExtra("id",ID.getText().toString()));
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

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    public boolean isInternetAvailable(){
        //.putExtra("notices",sql.getClass()));
        startService(new Intent(this, NotificationCenter.class));
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

}