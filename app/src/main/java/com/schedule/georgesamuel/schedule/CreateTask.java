package com.schedule.georgesamuel.schedule;

import java.util.List;

public interface CreateTask {

    interface View{
        void showTask(Task task);
        void showTasks(List<Task> list);
    }

    interface Presenter{
        void addTask(Task task);
        void getTask(int id);
        void getTasks();
        void updateTask(Task task);
        void deleteTask(Task task);
    }
}
