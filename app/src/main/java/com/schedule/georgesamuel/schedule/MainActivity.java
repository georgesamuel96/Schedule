package com.schedule.georgesamuel.schedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CreateTask.View{

    private android.support.v7.widget.Toolbar toolbar;
    private CreateTask.Presenter presenter;
    private List<Task> taskList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private RecyclerViewClickListener clickListener;
    private TextView noDates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        noDates = (TextView) findViewById(R.id.noDates);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        clickListener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("taskId", taskList.get(position).getId());
                startActivity(intent);
            }
        };
        adapter = new TaskAdapter(taskList, clickListener);
        recyclerView.setAdapter(adapter);

        presenter = new TaskPresenter(getApplicationContext(), this);
        presenter.getTasks();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add){
            Intent i = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void showTask(Task task) {

    }

    @Override
    public void showTasks(List<Task> list) {
        taskList.clear();
        taskList.addAll(list);
        adapter.notifyDataSetChanged();

        if(taskList.size() == 0){
            noDates.setVisibility(View.VISIBLE);
        }
        else {
            noDates.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        System.out.println("onRestart");
        presenter.getTasks();

    }
}
