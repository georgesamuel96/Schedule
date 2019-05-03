package com.schedule.georgesamuel.schedule;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskActivity extends AppCompatActivity implements CreateTask.View{

    private android.support.v7.widget.Toolbar toolbar;
    private int taskId;
    private CreateTask.Presenter presenter;
    private TextView titleText, contentText, dayText, hourText, minuteText, secondsText;
    private Boolean cancelActivity = false;
    private Task currentTask;
    private CountDownTimer downTimer;
    private Long reminderTime;
    private AlertDialog.Builder alertBuilder;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Task");

        alertBuilder = new AlertDialog.Builder(this);

        titleText = (TextView) findViewById(R.id.title);
        contentText = (TextView) findViewById(R.id.content);
        dayText = (TextView) findViewById(R.id.day);
        hourText = (TextView) findViewById(R.id.hour);
        minuteText = (TextView) findViewById(R.id.minute);
        secondsText = (TextView) findViewById(R.id.seconds);

        taskId = taskId = getIntent().getIntExtra("taskId", -1);

        presenter = new TaskPresenter(this, this);

        presenter.getTask(taskId);
    }

    @Override
    public void showTask(final Task task) {

        currentTask = task;

        titleText.setText(task.getTitle());
        contentText.setText(task.getContent());

        String dateTime = task.getDate() + " " + task.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date mDateTime = sdf.parse(dateTime);
            Long timeInMilliseconds = mDateTime.getTime();
            reminderTime = timeInMilliseconds - System.currentTimeMillis();
            reminderTime = Math.max(reminderTime, 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        downTimer = new CountDownTimer(reminderTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                reminderTime = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                reminderTime = 900L;
                updateTimer();
            }
        }.start();
    }

    private void updateTimer() {
        Long seconds, minute, hour, day;
        System.out.println(reminderTime);
        reminderTime = reminderTime / 1000 +  ((!(reminderTime % 1000 == 0)) ? 1L : 0L);
        seconds = reminderTime % 60;
        reminderTime = reminderTime / 60;
        minute = reminderTime % 60;
        reminderTime = reminderTime / 60;
        hour = reminderTime % 24;
        reminderTime = reminderTime / 24;
        day = reminderTime;

        dayText.setText(day + "");
        hourText.setText(hour + "");
        minuteText.setText(minute + "");
        secondsText.setText(seconds + "");
    }

    @Override
    public void showTasks(List<Task> list) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.edit){
            sendToEditTask();
        }
        else if(item.getItemId() == R.id.delete){
            deleteTask();
        }

        return true;
    }

    private void deleteTask() {
        alertBuilder.setTitle("Delete task");
        alertBuilder.setMessage("Are you Sure to delete this task ?");
        alertBuilder.setCancelable(false);
        alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent alarmIntent = new Intent(TaskActivity.this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(TaskActivity.this, taskId, alarmIntent, 0);

                AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                manager.cancel(pendingIntent);//cancel the alarm manager of the pending intent

                //Stop the Media Player Service to stop sound
                //stopService(new Intent(TaskActivity.this, AlarmSoundService.class));


                presenter.deleteTask(currentTask);
                finish();
            }
        });
        alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private void sendToEditTask() {
        cancelActivity = true;
        Intent intent = new Intent(TaskActivity.this, UpdateTaskActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(cancelActivity){
            finish();
        }
    }
}
