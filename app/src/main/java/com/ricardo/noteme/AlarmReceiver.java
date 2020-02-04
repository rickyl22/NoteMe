package com.ricardo.noteme;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent2) {

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "rick")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Note")
                .setContentText(intent2.getStringExtra("message"))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "rick";
            String description = "rick channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("rick", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
//        int m =intent2.getIntExtra("month",0), d = intent2.getIntExtra("day",0);
//        if(intent2.getStringExtra("repeat").equalsIgnoreCase("Weekly")){
//            d+=7;
//            if(d > 31 ){
//                d -= 31;
//                m++;
//                m %= 12;
//            }
//        }else if(intent2.getStringExtra("repeat").equalsIgnoreCase("Daily")){
//            d++;
//            if(d>31){
//                d=1;
//                m++;
//                m %= 12;
//            }
//        }else if(intent2.getStringExtra("repeat").equalsIgnoreCase("Monthly")){
//            m++;
//            m %= 12;
//        }
//        Calendar calendar  = Calendar.getInstance();
//        calendar.set(intent2.getIntExtra("year",0),
//                m,
//                d,
//                intent2.getIntExtra("hour",0),
//                intent2.getIntExtra("minute",0)+2,
//                0);
//        Log.d("ALARM","WORKING");
//        Intent myIntent = new Intent(context,MainActivity.class);
//        myIntent.putExtra("message",intent2.getStringExtra("mes"));
//        myIntent.putExtra("year",intent2.getIntExtra("year",0));
//        myIntent.putExtra("month",m);
//        myIntent.putExtra("day",d);
//        myIntent.putExtra("hour",intent2.getIntExtra("hour",0));
//        myIntent.putExtra("minute",intent2.getIntExtra("minute",0)+2);
//        myIntent.putExtra("id",intent2.getIntExtra("id",0));
//        myIntent.putExtra("repeat",intent2.getStringExtra("repeat"));
//        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, intent2.getIntExtra("id",0), myIntent, PendingIntent.FLAG_ONE_SHOT);
//
//        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);
    }
}
