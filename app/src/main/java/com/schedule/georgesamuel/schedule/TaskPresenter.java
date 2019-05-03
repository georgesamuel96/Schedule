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

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra("taskId", taskId);
        pendingIntent = PendingIntent.getBroadcast(context, taskId, alarmIntent, 0);

        String dateTime = task.getDate() + " " + task.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date mDateTime = sdf.parse(dateTime);
            int timeInSeconds = (int) mDateTime.getTime();
            int reminderTime = timeInSeconds - (int) System.currentTimeMillis();
            //reminderTime = reminderTime / 1000 + (!(reminderTime % 1000 == 0)? 1 : 0);
            triggerAlarmManager(reminderTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


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
        taskDatabase.getTaskDao().updateTask(task);
    }

    @Override
    public void deleteTask(Task task) {
        taskDatabase.getTaskDao().deleteTask(task);
    }

    //Trigger alarm manager with entered time interval
    public void triggerAlarmManager(int alarmTriggerTime) {

        // get a Calendar object with current time
        Calendar cal = Calendar.getInstance();

        // add alarmTriggerTime seconds to the calendar object
        cal.add(Calendar.MILLISECOND, alarmTriggerTime);

        //get instance of alarm manager
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //set alarm manager with entered timer by converting into milliseconds
        manager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }
}
