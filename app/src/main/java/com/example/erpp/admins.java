package com.example.erpp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class admins extends AppCompatActivity {
    TextView aB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aB = ((TextView)findViewById(R.id.adminBoard));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels * .8),(int)(displayMetrics.heightPixels * 0.3));
        updateAdmin();
        ((TextView)findViewById(R.id.adminBoard)).setMovementMethod(new ScrollingMovementMethod());
    }

    private void updateAdmin() {

        FirebaseDatabase.getInstance().getReference("ADMIN").child("ADMIN").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(String.class) != null){
                    ((TextView)findViewById(R.id.adminBoard)).setHint(dataSnapshot.getValue(String.class));
                }else{
                    ((TextView)findViewById(R.id.adminBoard)).setHint("Admin[s] not found");

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
