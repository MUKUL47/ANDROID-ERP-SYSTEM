package com.example.erpp.PopupWindow;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.erpp.Login;
import com.example.erpp.R;
import com.example.erpp.StaticDB;
import com.example.erpp.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginAs extends AppCompatActivity implements AdapterView.OnItemClickListener {
    StaticDB staticDB;
    ArrayList<String> ids = new ArrayList<>();
    ArrayList<String> pass = new ArrayList<>();
    ListView loggedInIds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginas);
        loggedInIds = (ListView)findViewById(R.id.loggedInIds);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        loggedInIds.setOnItemClickListener(this);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels * .5),(int)(displayMetrics.heightPixels * 0.3));
        staticDB = new StaticDB(this);
        updateList();
    }

    private void updateList() {
        for( String id : staticDB.getDb() ){
            ids.add(id.split("_")[0]);
            pass.add(id.split("_")[1]);
        }
        loggedInIds.setAdapter(
                new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,ids)
        );
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        startActivity(new Intent(this, Login.class)
//                .putExtra("autoLogin","login")
//                .putExtra("autoId",ids.get(position))
//                .putExtra("autoPass",pass.get(position)));
        login(ids.get(position),pass.get(position));
    }
    private void login(String id, String pass){
        final String ID = id, PASS = pass;
        FirebaseDatabase.getInstance().getReference().child("StudentLogs/"+ID.substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.child(ID.toUpperCase()).getValue(String.class);
                if( id == null){
                   // Toast.makeText(Login.this,"ID NOT REGISTERED (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                }
                else{
                    if(dataSnapshot.child(ID.toUpperCase()).
                            getValue(String.class)
                            .equalsIgnoreCase(PASS)){
                        startActivity(new Intent(LoginAs.this, Student.class)
                                .putExtra("id",ID.toUpperCase())
                                .putExtra("pass",PASS)
                        );
                    }else{
                        //Toast.makeText(Login.this,"INCORRECT ERP PASSWORD (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
