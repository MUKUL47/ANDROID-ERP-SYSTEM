package com.example.erpp.UnnessaryModules;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import android.content.Intent;

import com.example.erpp.Login;
import com.example.erpp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class NotificationCenter extends Service{
    String currentNotification = "";

    public void checkNotification() {
        FirebaseDatabase.getInstance().getReference().child("NoticeBoard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {if( dataSnapshot.getValue(String.class) != null ){
                if( currentNotification != dataSnapshot.getValue(String.class) ){
                    currentNotification = dataSnapshot.getValue(String.class);
                    notifY(currentNotification);
                }
            }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void notifY(String notice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    "1",
                    "11",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Hey there !");
        bigTextStyle.bigText(notice);
        Notification notification = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("You have a new notice!")
                .setContentText(notice)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, Login.class), 0))
                .build();
        NotificationManagerCompat.from(this).notify(1, notification);
    }

    @Override
    public void onCreate() {
        checkNotification();
        super.onCreate();
    }
}