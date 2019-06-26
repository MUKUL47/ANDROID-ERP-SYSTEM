package com.example.erpp;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Student extends AppCompatActivity implements View.OnClickListener {
    String userId = "", marksheetExtension = ".pdf", finalName ;
    FirebaseStorage FBS;
    TextView noticeText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show(); }
        userId = getIntent().getStringExtra("id");
        ((TextView)findViewById(R.id.progressBarS)).setText(userId);
        noticeText = ((TextView)findViewById(R.id.notice));
        finalName = ":"+userId;
        updateNotice();
    }

    private void downloadThisFile(String fileNameAndPath) {
        final int id = R.id.progressBarStudent;
        findViewById(R.id.progressBarStudent).setVisibility(View.VISIBLE);
        finalName = fileNameAndPath.substring(fileNameAndPath.indexOf(":")+1,fileNameAndPath.length());
        StorageReference sR, R;
        sR = FBS.getInstance().getReference();
        R = sR.child(fileNameAndPath);
        R.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                DownloadManager dm = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request R = new DownloadManager.Request(Uri.parse(uri.toString()));
                R.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                R.setDestinationInExternalFilesDir(getApplicationContext(),DIRECTORY_DOWNLOADS,finalName);
                Toast.makeText(getApplicationContext(),"DOWNLOADING",Toast.LENGTH_LONG).show();
                dm.enqueue(R);
                findViewById(id).setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),finalName+" : FILE NOT FOUND (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                findViewById(id).setVisibility(View.INVISIBLE);
            }
        });

    }

    private void updateNotice() {
        FirebaseDatabase.
                getInstance().
                getReference().
                child(userId.substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String S = dataSnapshot.child("Notice").getValue(String.class);

                if( S != null && S.length() > 0) {
                    noticeText.setText(S);
                }else{
                    noticeText.setText("No notice[s] found");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        noticeText.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.progressBarStudent).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Login.class));
    }

    @Override
    public void onClick(View v) {
        String year = userId.substring(0,4);
        if(isInternetAvailable()){
            switch (v.getId()) {
                case R.id.y1s1:
                    downloadThisFile(year + "/1/1/" + userId + marksheetExtension);
                    break;

                case R.id.y1s2:
                    downloadThisFile(year + "/1/2/" + userId + marksheetExtension);
                    break;

                case R.id.y2s1:
                    downloadThisFile(year + "/2/1/" + userId + marksheetExtension);
                    break;

                case R.id.y2s2:
                    downloadThisFile(year + "/2/2/" + userId + marksheetExtension);
                    break;

                case R.id.y3s1:
                    downloadThisFile(year + "/3/1/" + userId + marksheetExtension);
                    break;

                case R.id.y3s2:
                    downloadThisFile(year + "/3/2/" + userId + marksheetExtension);
                    break;

                case R.id.y4s1:
                    downloadThisFile(year + "/4/1/" + userId + marksheetExtension);
                    break;

                case R.id.y4s2:
                    downloadThisFile(year + "/4/2/" + userId + marksheetExtension);
                    break;
                case R.id.seatingArrangement:
                    startActivity(new Intent(this,SeatingArrangements.class).putExtra("id",userId.substring(0,4)));
                    break;
                case R.id.subscription:
                    startActivity(new Intent(this,EmailNotification.class).putExtra("id",userId));
                    break;
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
}
