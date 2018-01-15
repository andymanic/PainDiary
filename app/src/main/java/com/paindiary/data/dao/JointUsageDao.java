package com.paindiary.data.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.paindiary.data.PainDiaryDatabase;
import com.paindiary.domain.JointUsage;

import java.util.List;

@Dao
public interface JointUsageDao {

    @Insert()
    public long insert(JointUsage jointUsage);

    @Update()
    public void update(JointUsage jointUsage);

    @Delete()
    public void delete(JointUsage jointUsage);

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " WHERE " + PainDiaryDatabase.JOINT_USAGE_ID_COLUMN + " = :id LIMIT 1")
    public JointUsage get(long id);

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " WHERE " + PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN + " LIKE :description")
    public JointUsage getByDescription(String description);

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME)
    public List<JointUsage> getAll();

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " ORDER BY " + PainDiaryDatabase.JOINT_USAGE_COUNT_COLUMN + " DESC, " + PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN)
    public List<JointUsage> getAllByUsage();

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " ORDER BY " + PainDiaryDatabase.JOINT_USAGE_COUNT_COLUMN + " DESC LIMIT :limit")
    public List<JointUsage> getTopXUsed(int limit);

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " WHERE " + PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN + " LIKE :description ORDER BY " + PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN)
    public List<JointUsage> getSuggestionOrderedByDescription(String description);

    @Query("SELECT * FROM " + PainDiaryDatabase.JOINT_USAGE_TABLE_NAME + " WHERE " + PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN + " LIKE :description ORDER BY " + PainDiaryDatabase.JOINT_USAGE_COUNT_COLUMN + " DESC")
    public List<JointUsage> getSuggestionOrderedByUsageCount(String description);
}
