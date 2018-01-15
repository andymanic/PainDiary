package com.paindiary.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.paindiary.data.PainDiaryDatabase;
import com.paindiary.domain.PainEntry;

import java.util.Date;
import java.util.List;

@Dao
public interface PainEntryDao {

    @Insert()
    public long insert(PainEntry painEntry);

    @Update()
    public void update(PainEntry painEntry);

    @Delete
    public void delete(PainEntry painEntry);

    @Query("SELECT * FROM " + PainDiaryDatabase.PAIN_ENTRIES_TABLE_NAME + " WHERE " + PainDiaryDatabase.PAIN_ENTRIES_ID_COLUMN + " = :id LIMIT 1")
    public PainEntry get(long id);

    @Query("SELECT * FROM " + PainDiaryDatabase.PAIN_ENTRIES_TABLE_NAME)
    public List<PainEntry> getAll();

    @Query("SELECT * FROM " + PainDiaryDatabase.PAIN_ENTRIES_TABLE_NAME + " WHERE " + PainDiaryDatabase.PAIN_ENTRIES_DATE_COLUMN + " BETWEEN :fromDate AND :untilDate AND " + PainDiaryDatabase.PAIN_ENTRIES_LEVEL_COLUMN + " >= :minLevel")
    public List<PainEntry> getAllFiltered(Date fromDate, Date untilDate, int minLevel);
}
