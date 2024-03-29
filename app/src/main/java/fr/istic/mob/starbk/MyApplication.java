package fr.istic.mob.starbk;

import android.app.Application;

import fr.istic.mob.starbk.DataBaseManager.DataStorage;

public class MyApplication extends Application {
    private DataStorage dataSore;

    @Override
    public void onCreate() {
        super.onCreate();

        this.dataSore = new DataStorage(this);
    }

    public DataStorage getDataSore() {
        return dataSore;
    }
}
