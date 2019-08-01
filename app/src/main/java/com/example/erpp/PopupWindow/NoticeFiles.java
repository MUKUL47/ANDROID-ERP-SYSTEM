package com.example.erpp.PopupWindow;

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

import com.example.erpp.R;
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

public class NoticeFiles extends AppCompatActivity implements AdapterView.OnItemClickListener {
    FirebaseStorage FBS;
    ArrayList<String> noticeFiles = new ArrayList<>();
    ListView noticeF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticefiles);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getWindow().setLayout((int)(displayMetrics.widthPixels * .8),(int)(displayMetrics.heightPixels * 0.5));
        noticeF = findViewById(R.id.noticeF);
        noticeF.setOnItemClickListener(this);
        if( isInternetAvailable() )updateNoticeFiles();
        else Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();

    }

    private void updateNoticeFiles() {
        FirebaseDatabase.getInstance().getReference().child("NoticeFiles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot s : dataSnapshot.getChildren()){
                    noticeFiles.add(s.getValue(String.class));
                }
                findViewById(R.id.noticeBar).setVisibility(View.INVISIBLE);
                if( noticeFiles.size() == 0 ){
                    Toast.makeText(NoticeFiles.this,"No notice[s] found",Toast.LENGTH_LONG).show();
                }
                noticeF.setAdapter(new ArrayAdapter<>(NoticeFiles.this,R.layout.support_simple_spinner_dropdown_item,noticeFiles));
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
        final int idd = R.id.noticeBar;
        findViewById(idd).setVisibility(View.VISIBLE);
        final String arrangementFile = noticeFiles.get(position);
        StorageReference noticeFile = FBS.getInstance().getReference().child("NoticeFiles/"+arrangementFile);
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
    public void exitNoticeFiles(View view) {
        onBackPressed();
    }
}
