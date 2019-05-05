package com.schedule.georgesamuel.schedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity implements CreateTask.View, TimePickerDialog.OnTimeSetListener {

    private android.support.v7.widget.Toolbar toolbar;
    private CreateTask.Presenter presenter;
    private TextInputLayout titleET;
    private TextInputLayout contentET;
    private TextInputLayout dateEt;
    private TextInputLayout timeEt;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button addTask;
    private int year = -1, month = -1, day = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add New Task");

        titleET = (TextInputLayout) findViewById(R.id.title);
        contentET = (TextInputLayout) findViewById(R.id.content);
        dateEt = (TextInputLayout) findViewById(R.id.date);
        timeEt = (TextInputLayout) findViewById(R.id.time);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        addTask = (Button) findViewById(R.id.addTask);

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
                DatePickerDialog mDatePicker = new DatePickerDialog(AddTaskActivity.this,
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

        presenter = new TaskPresenter(getApplicationContext(), this);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = titleET.getEditText().getText().toString().trim();
                String content = contentET.getEditText().getText().toString().trim();
                String date = dateEt.getEditText().getText().toString().trim();
                String time = timeEt.getEditText().getText().toString().trim();

                int selectedColor = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedColor);
                String color = radioButton.getText().toString();

                Task task = new Task();
                task.setColor(color);
                task.setContent(content);
                task.setDate(date);
                task.setTime(time);
                task.setTitle(title);

                presenter.addTask(task);

                finish();
            }
        });
    }

    @Override
    public void showTask(Task task) {

    }

    @Override
    public void showTasks(List<Task> list) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timeEt.getEditText().setText(hourOfDay + ":" + minute);
    }
}
