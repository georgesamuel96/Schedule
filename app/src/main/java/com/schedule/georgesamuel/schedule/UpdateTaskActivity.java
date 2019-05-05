package com.schedule.georgesamuel.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class UpdateTaskActivity extends AppCompatActivity implements CreateTask.View, TimePickerDialog.OnTimeSetListener {

    private android.support.v7.widget.Toolbar toolbar;
    private CreateTask.Presenter presenter;
    private TextInputLayout titleET;
    private TextInputLayout contentET;
    private TextInputLayout dateEt;
    private TextInputLayout timeEt;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button updateTask;
    private String[] colors = new String[3];
    private int taskId;
    private int year = -1, month = -1, day = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Task");

        titleET = (TextInputLayout) findViewById(R.id.title);
        contentET = (TextInputLayout) findViewById(R.id.content);
        dateEt = (TextInputLayout) findViewById(R.id.date);
        timeEt = (TextInputLayout) findViewById(R.id.time);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        updateTask = (Button) findViewById(R.id.addTask);

        colors[0] = "Red";
        colors[1] = "Yellow";
        colors[2] = "Blue";

        presenter = new TaskPresenter(getApplicationContext(), this);

        taskId = getIntent().getIntExtra("taskId", -1);

        presenter.getTask(taskId);

        dateEt.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                Calendar currentDate = Calendar.getInstance();
                int mYear = currentDate.get(Calendar.YEAR);
                int mMonth = currentDate.get(Calendar.MONTH);
                int mDay = currentDate.get(Calendar.DAY_OF_MONTH);
                if(year == -1){
                    year = mYear;
                    month = mMonth;
                    day = mDay;
                }
                dateEt.getEditText().setText(day + "/" + month + "/" + year);
                DatePickerDialog mDatePicker = new DatePickerDialog(UpdateTaskActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                                Calendar myCalendar = Calendar.getInstance();
                                myCalendar.set(Calendar.YEAR, selectedyear);
                                myCalendar.set(Calendar.MONTH, selectedmonth);
                                myCalendar.set(Calendar.DAY_OF_MONTH, selectedday);
                                String myFormat = "dd/MM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                                dateEt.getEditText().setText(sdf.format(myCalendar.getTime()));

                                year = selectedyear;
                                month = selectedmonth + 1;
                                day = selectedday;

                            }
                        }, year, month, day);
                mDatePicker.show();
            }
        });

        timeEt.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        updateTask.setText("Update");
        updateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Task task = getValues();
                presenter.updateTask(task);
                sendToTask();
            }
        });

    }

    private Task getValues() {

        String title = titleET.getEditText().getText().toString().trim();
        String content = contentET.getEditText().getText().toString().trim();
        String date = dateEt.getEditText().getText().toString().trim();
        String time = timeEt.getEditText().getText().toString().trim();

        int selectedColor = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) findViewById(selectedColor);
        String color = radioButton.getText().toString();

        Task task = new Task();
        task.setId(taskId);
        task.setColor(color);
        task.setContent(content);
        task.setDate(date);
        task.setTime(time);
        task.setTitle(title);

        return task;
    }

    @Override
    public void showTask(Task task) {
        titleET.getEditText().setText(task.getTitle());
        contentET.getEditText().setText(task.getContent());
        dateEt.getEditText().setText(task.getDate());
        timeEt.getEditText().setText(task.getTime());
        for(int position = 0; position < 3; position++){
            if(colors[position].equals(task.getColor()))
                radioGroup.check(radioGroup.getChildAt(position).getId());
        }
    }

    @Override
    public void showTasks(List<Task> list) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeEt.getEditText().setText(hourOfDay + ":" + minute);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        sendToTask();
    }

    private void sendToTask() {
        Intent intent = new Intent(UpdateTaskActivity.this, TaskActivity.class);
        intent.putExtra("taskId", taskId);
        startActivity(intent);
        finish();
    }
}
