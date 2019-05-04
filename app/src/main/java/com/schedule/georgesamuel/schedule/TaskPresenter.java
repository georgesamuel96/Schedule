package com.schedule.georgesamuel.schedule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TaskPresenter implements CreateTask.Presenter {

    private CreateTask.View view;
    private List<Task> taskList = new ArrayList<>();
    private static TaskDatabase taskDatabase;
    private Context context;
    private PendingIntent pendingIntent;

    public TaskPresenter(Context context, CreateTask.View view){
        this.view = view;
        this.context = context;
        taskDatabase = Room.databaseBuilder(context, TaskDatabase.class, "taskdb").allowMainThreadQueries().build();

    }

    @Override
    public void addTask(Task task) {

        SharedPreferences preferences = context.getSharedPreferences("id", 0);
        int taskId = preferences.getInt("id", -1);
        taskId++;
        task.setId(taskId);

        getTimeInMilliseconds(task);

        taskDatabase.getTaskDao().insertTask(task);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id", taskId);
        editor.commit();


    }

    @Override
    public void getTask(int id) {
        Task task = taskDatabase.getTaskDao().getTask(id);
        view.showTask(task);
    }

    @Override
    public void getTasks() {
        taskList = taskDatabase.getTaskDao().getTasks();
        view.showTasks(taskList);
    }

    @Override
    public void updateTask(Task task) {

        getTimeInMilliseconds(task);

        taskDatabase.getTaskDao().updateTask(task);
    }

    @Override
    public void deleteTask(Task task) {

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, task.getId(), alarmIntent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent

        //Stop the Media Player Service to stop sound
        //stopService(new Intent(TaskActivity.this, AlarmSoundService.class));

        taskDatabase.getTaskDao().deleteTask(task);
    }

    //Trigger alarm manager with entered time interval
    private void triggerAlarmManager(Long alarmTriggerTime) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //set alarm manager with entered timer by converting into milliseconds
        manager.set(AlarmManager.RTC_WAKEUP, alarmTriggerTime, pendingIntent);
    }

    private void getTimeInMilliseconds(Task task){

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("taskId", task.getId());
        pendingIntent = PendingIntent.getBroadcast(context, task.getId(), alarmIntent, 0);

        String dateTime = task.getDate() + " " + task.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date mDateTime = sdf.parse(dateTime);
            triggerAlarmManager(mDateTime.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
