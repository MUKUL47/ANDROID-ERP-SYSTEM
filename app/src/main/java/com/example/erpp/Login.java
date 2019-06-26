package com.example.erpp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText ID,PASS;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ID   = findViewById(R.id.id);
        PASS = findViewById(R.id.password);
        progressBar =  findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        (findViewById(R.id.login)).setOnClickListener(this);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();  }

    }
    @Override
    public void onClick(View v)
    {   if(isInternetAvailable()){
        progressBar.setVisibility(View.VISIBLE);
            if( v.getId() == R.id.admins ){
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(this,Contact.class));
            }
            else if(ID.getText().toString().toUpperCase() != "ADMIN" && ID.getText().toString().length() < 5){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this,"INVALID ID",Toast.LENGTH_LONG).show();

            }
            else{
                    FirebaseDatabase.getInstance().getReference().child(ID.getText().toString().substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String id = dataSnapshot.child(ID.getText().toString().toUpperCase()).getValue(String.class);
                            if( id == null){
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(Login.this,"ID NOT REGISTERED (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(dataSnapshot.child(ID.getText().toString().toUpperCase()).
                                        getValue(String.class)
                                        .equalsIgnoreCase(PASS.getText().toString())){
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(Login.this,Student.class).putExtra("id",ID.getText().toString().toUpperCase()));
                                }else{
                                    progressBar.setVisibility(View.INVISIBLE);
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

    class FirebaseAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

}