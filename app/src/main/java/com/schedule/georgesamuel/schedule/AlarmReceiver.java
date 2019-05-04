package com.schedule.georgesamuel.schedule;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class AlarmReceiver extends BroadcastReceiver implements CreateTask.View{

    private NotificationManager alarmNotificationManager;

    //Notification ID for Alarm
    private static Uri alarmSound;
    private int taskId;
    private Task currentTask;
    private CreateTask.Presenter presenter;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        presenter = new TaskPresenter(context, this);
        taskId = intent.getIntExtra("taskId",0);
        this.context = context;
        presenter.getTask(taskId);
    }

    //handle notification
    private void sendNotification(Context context) {
        alarmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //get pending intent
        Intent intent = new Intent(context, TaskActivity.class);
        intent.putExtra("taskId", taskId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Create notification
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(context);
        alamNotificationBuilder.setContentTitle(currentTask.getTitle()).setSmallIcon(R.mipmap.ic_launcher);
        alamNotificationBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(currentTask.getContent()));
        alamNotificationBuilder.setContentText(currentTask.getContent());
        alamNotificationBuilder.setAutoCancel(true);
        alamNotificationBuilder.setContentIntent(contentIntent);
        alamNotificationBuilder.setSound(alarmSound);

        //notiy notification manager about new notification
        alarmNotificationManager.notify(taskId, alamNotificationBuilder.build());
    }

    @Override
    public void showTask(Task task) {
        currentTask = task;
        sendNotification(context);
    }

    @Override
    public void showTasks(List<Task> list) {

    }
}
