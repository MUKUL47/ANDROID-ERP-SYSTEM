package com.example.erpp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.erpp.PopupWindow.Contact;
import com.example.erpp.PopupWindow.LoginAs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText PASS;
    AutoCompleteTextView ID;
    ProgressBar progressBar;
    StaticDB staticDB;
    boolean exit = true;
    String autoId = "", autoPass = "";
    String autoLogin = "";
    ArrayList<String> pass, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        staticDB = new StaticDB(this);
        getIntentData();
        setContentView(R.layout.login);
        ID   = findViewById(R.id.id);
        PASS = findViewById(R.id.password);
        ID.setOnItemClickListener(this);
        progressBar =  findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        (findViewById(R.id.login)).setOnClickListener(this);
        updateAutoCompleteId(staticDB);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();  }
        //isAutoLoggedIn();
    }

    private void getIntentData() {
        exit = getIntent().getBooleanExtra("exit",true);
        autoLogin = getIntent().getStringExtra("autoLogin");
        autoId = getIntent().getStringExtra("autoId");
        autoPass = getIntent().getStringExtra("autoPass");
    }

    private void isAutoLoggedIn() {
        if(staticDB.getDb().size() == 0){
            findViewById(R.id.loggedInDetails).setVisibility(View.INVISIBLE);
        }
        if(autoLogin == "login"){
            ID.setText(autoId);
            PASS.setText(autoPass);
            onClick(findViewById(R.id.login));
        }else{
            exit(findViewById(R.id.exit));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(exit){
                Toast.makeText(this,"Press back again to exit",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,this.getClass())
                        .putExtra("exit",false));
        }else{
            exit(findViewById(R.id.exit));
        }
    }

    private void updateAutoCompleteId(StaticDB staticDB) {
        final String split = "_";
        id = new ArrayList<>();
        pass = new ArrayList<>();
        for( String onlyId : staticDB.getDb() ){
            String arr[] = onlyId.split(split);
            pass.add(arr[1]);
            id.add(arr[0]);
        }
        ID.setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,
                id));
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.loggedInDetails){
            startActivity(new Intent(this, LoginAs.class));
        }
        else if( v.getId() == R.id.showPass ){
            Switch simpleSwitch = findViewById(R.id.showPass);
                if( !simpleSwitch.isChecked() ){
                    simpleSwitch.setText("Show");
                    PASS.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }else{
                    simpleSwitch.setText("Hide");
                    PASS.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
        }
        else if(isInternetAvailable()){
        progressBar.setVisibility(View.VISIBLE);
            if( v.getId() == R.id.admins2){
                progressBar.setVisibility(View.INVISIBLE);
                startActivity(new Intent(this, Contact.class));
            }
            else if(ID.getText().toString().toUpperCase() != "ADMIN" && ID.getText().toString().length() < 5){
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(Login.this,"INVALID ID",Toast.LENGTH_LONG).show();

            }
            else{
                    FirebaseDatabase.getInstance().getReference().child("StudentLogs/"+ID.getText().toString().substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                    startActivity(new Intent(Login.this,Student.class)
                                            .putExtra("id",ID.getText().toString().toUpperCase())
                                            .putExtra("pass",PASS.getText().toString())
                                            .putExtra("rememberMe",((CheckBox)findViewById(R.id.loggedIn)).isChecked())
                                    );
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {
        PASS.setText(pass.get(position));
    }

    public void exit(View view) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }



}