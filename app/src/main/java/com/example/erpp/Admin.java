package com.example.erpp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Admin extends AppCompatActivity implements View.OnClickListener {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    HashMap<String, String> userDetails = new HashMap<>();
    ArrayList<String> getDetails = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allowPermission();
        setContentView(R.layout.activity_main3);
        ((Button)findViewById(R.id.button3)).setOnClickListener(this);
        showData();
        ((AutoCompleteTextView)findViewById(R.id.autoCompleteTextView))
                .setAdapter(new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,getDetails));


    }
    private void allowPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 42);
        }
    }
    private void getUserDetailsReady(String fileName) {
        File csvFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fileName);

        try {
            BufferedReader reader1 =new BufferedReader(new FileReader(csvFile));
            String line;
            while((line = reader1.readLine()) != null){
                userDetails.put(line.split(",")[0],line.split(",")[1]);
            }
            Toast.makeText(this,"UPLOADED",Toast.LENGTH_LONG).show();

        }
        catch(Exception E){
            Toast.makeText(this,"FILE NOT LOADED",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        performFileSearch();
    }

    private void uploadInFirebase() {
        for(Map.Entry details : userDetails.entrySet()){
            database.getReference()
                    .child(details.getKey().toString().substring(0,4))
                    .push()
                    .getParent().child(details.getKey()+"")
                    .setValue(details.getValue());
        }
        startActivity(new Intent(this,this.getClass()));
    }

    private void showData() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String S = "";
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    for(DataSnapshot dsChild : ds.getChildren()){
                        getDetails.add(dsChild.getKey()+"  "+dsChild.getValue());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void performFileSearch() {
        if( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),42);
        }else{
            Toast.makeText(this,"EXTERNAL STORAGE ACCESS DENIED",Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 42){
                if(data == null){ return; }


                Uri selectedFileUri = data.getData();
                String finalsPath = selectedFileUri.toString();
                int i = finalsPath.length()-1, start = -1;
                while( i-- > 0 ){
                    if(finalsPath.charAt(i) == '%'){
                        start = i;
                        break;
                    }
                }
                String fileName = finalsPath.substring(start+3);
                getUserDetailsReady(fileName);
                uploadInFirebase();


            }
        }}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Student.class));
    }
}
