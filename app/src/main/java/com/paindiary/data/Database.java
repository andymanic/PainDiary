package com.paindiary.data;

import android.content.Context;

import static android.arch.persistence.room.Room.databaseBuilder;

public class Database {

    private PainDiaryDatabase _painDiaryDb;

    private Database() {
        _painDiaryDb = null;
        _painDiaryLoaded = false;
    }

    private static Database _instance;

    public static synchronized Database getInstance() {
        if (_instance == null)
            _instance = new Database();
        return _instance;
    }

    private boolean _painDiaryLoaded;

    public boolean isPainDiaryLoaded() {
        return _painDiaryLoaded;
    }

    public void loadPainDiaryDb(Context applicationContext) {
        _painDiaryDb = databaseBuilder(applicationContext, PainDiaryDatabase.class, PainDiaryDatabase.DB_NAME).build();
        _painDiaryLoaded = true;
    }

    public PainDiaryDatabase getPainDiaryDb() {
        if (!isPainDiaryLoaded())
            throw new RuntimeException("Database '" + PainDiaryDatabase.DB_NAME + "' is not loaded before calling.");
        return _painDiaryDb;
    }
}
