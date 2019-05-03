package com.schedule.georgesamuel.schedule;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskDatabase extends RoomDatabase {

    public abstract TaskDao getTaskDao();
}
