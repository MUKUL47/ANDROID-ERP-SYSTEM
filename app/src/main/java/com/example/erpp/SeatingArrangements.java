package com.example.erpp;

import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SeatingArrangements extends AppCompatActivity implements AdapterView.OnItemClickListener {
    FirebaseStorage FBS;
    String ID;
    ArrayList<String> arrangements = new ArrayList<>();
    ListView arrangement;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seatingarrangement);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels * .8),(int)(displayMetrics.heightPixels * 0.5));
        ID = getIntent().getStringExtra("id");
        arrangement = findViewById(R.id.arrangements);
        arrangement.setOnItemClickListener(this);
        if( isInternetAvailable() )updateSeatingArrangements();
        else Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();
    }

    private void updateSeatingArrangements() {
        FirebaseDatabase.getInstance().getReference().child(ID).child("seatingArrangement").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot s : dataSnapshot.getChildren()){
                    arrangements.add(s.getValue(String.class));
                }
                findViewById(R.id.progressBarSeatinArrangement).setVisibility(View.INVISIBLE);
                if( arrangements.size() == 0 ){
                    Toast.makeText(SeatingArrangements.this,"Not seating arrangement[s] found",Toast.LENGTH_LONG).show();
                }
                arrangement.setAdapter(new ArrayAdapter<>(SeatingArrangements.this,R.layout.support_simple_spinner_dropdown_item,arrangements));
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int idd = R.id.progressBarSeatinArrangement;
        findViewById(R.id.progressBarSeatinArrangement).setVisibility(View.VISIBLE);
        final String arrangementFile = arrangements.get(position);
        StorageReference noticeFile = FBS.getInstance().getReference().child(ID).child("seatingArrangement").child(arrangementFile+".pdf");
        noticeFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request R = new DownloadManager.Request(Uri.parse(uri.toString()));
                R.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                R.setDestinationInExternalFilesDir(getApplicationContext(),DIRECTORY_DOWNLOADS,arrangementFile);
                Toast.makeText(getApplicationContext(),"DOWNLOADING",Toast.LENGTH_LONG).show();
                dm.enqueue(R);
                findViewById(idd).setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
