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
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.erpp.PopupWindow.EmailNotification;
import com.example.erpp.PopupWindow.NoticeFiles;
import com.example.erpp.PopupWindow.SeatingArrangements;
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

public class Student extends AppCompatActivity implements View.OnClickListener {
    String userId = "", pass = "", marksheetExtension = ".pdf", finalName ;
    FirebaseStorage FBS;
    TextView noticeText;
    StaticDB staticDB;
    boolean rememberMe, againLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student);
        staticDB = new StaticDB(this);
        userId = getIntent().getStringExtra("id");
        pass = getIntent().getStringExtra("pass");
        rememberMe = getIntent().getBooleanExtra("rememberMe",false);
        updateOrInitDb(staticDB);
        if(!isInternetAvailable()) { Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show(); }
        ((TextView)findViewById(R.id.progressBarS)).setText(userId);
        noticeText = ((TextView)findViewById(R.id.notice));
        finalName = userId;
        updateNotice();
    }

    private void updateOrInitDb(StaticDB staticDB) {
        ArrayList<String> idPass = staticDB.getDb();
        String found = "";
        for( String getId : idPass ){
            if(getId.split("_")[0].toUpperCase().equals(userId)){
                found = getId.split("_")[1];
                break;
            }
        }
        if(found != ""){
            staticDB.updateIdPass(userId.toUpperCase(),pass);
        }
        if(rememberMe){
            if( found != "" && found != pass){
                staticDB.updateIdPass(userId.toUpperCase(),pass);
            }else if(found == ""){
                staticDB.initIdPass(userId.toUpperCase(),pass);
            }
        }
        else if(found == "" && !rememberMe){
            findViewById(R.id.dontRemember).setVisibility(View.INVISIBLE);
        }

    }

    private void downloadThisFile(String fileNameAndPath) {
        final int id = R.id.progressBarStudent;
        findViewById(R.id.progressBarStudent).setVisibility(View.VISIBLE);
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
                Toast.makeText(getApplicationContext(),"FILE NOT FOUND (CONTACT ADMIN)",Toast.LENGTH_LONG).show();
                findViewById(id).setVisibility(View.INVISIBLE);
            }
        });

    }

    private void updateNotice() {
        FirebaseDatabase.
                getInstance().
                getReference().
                child("StudentLogs/"+userId.substring(0,4)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String S = dataSnapshot.child("Notice").getValue(String.class);

                if( S != null && S.length() > 0) {
                    noticeText.setText(S);
                }else{
                    noticeText.setText("Welcome all notice[s] from admin will be displayed here.");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        noticeText.setMovementMethod(new ScrollingMovementMethod());
        noticeText.setMovementMethod(LinkMovementMethod.getInstance());
        findViewById(R.id.progressBarStudent).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(!againLogin){
            againLogin = true;
            Toast.makeText(this,"Press again to logout",Toast.LENGTH_LONG).show();
        }
        else{super.onBackPressed();}
    }

    @Override
    public void onClick(View v) {
        if(isInternetAvailable()){
            switch (v.getId()) {
                case R.id.result:
                    downloadThisFile("Results/" + userId + marksheetExtension);
                    break;
                case R.id.noticeFiles:
                    startActivity(new Intent(this, NoticeFiles.class));
                    break;

                case R.id.seatingArrangement:
                    startActivity(new Intent(this, SeatingArrangements.class).putExtra("id",userId.substring(0,4)));
                    break;
                case R.id.subscription:
                    startActivity(new Intent(this, EmailNotification.class).putExtra("id",userId));
                    break;
                case R.id.refresh:
                    animateRefresh();
                    findViewById(R.id.progressBarStudent).setVisibility(View.VISIBLE);
                    updateNotice();
                    break;
                case R.id.dontRemember:
                    removeFromStaticDb();
                    break;
                case R.id.logout:
                    onBackPressed();
                    break;
            }
        }else{
            Toast.makeText(this,"Not connected to internet",Toast.LENGTH_LONG).show();
        }
        }

    private void animateRefresh() {
        RotateAnimation rotate = new RotateAnimation
                (0, 360, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(500);
        rotate.setInterpolator(new LinearInterpolator());
        ImageView image= findViewById(R.id.refresh);
        image.startAnimation(rotate);
    }

    private void removeFromStaticDb() {
        staticDB.deleteId(userId);
        findViewById(R.id.dontRemember).setVisibility(View.INVISIBLE);
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
