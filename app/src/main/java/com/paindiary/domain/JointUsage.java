package com.paindiary.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.paindiary.data.PainDiaryDatabase;

@Entity(tableName = PainDiaryDatabase.JOINT_USAGE_TABLE_NAME)
public class JointUsage {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PainDiaryDatabase.JOINT_USAGE_ID_COLUMN)
    private long _id;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    @ColumnInfo(name = PainDiaryDatabase.JOINT_USAGE_DESCRIPTION_COLUMN)
    private String _description;

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        _description = description;
    }

    @ColumnInfo(name = PainDiaryDatabase.JOINT_USAGE_COUNT_COLUMN)
    private int _count;

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        _count = count;
    }

    public JointUsage() {
    }

    @Ignore
    public JointUsage(String description) {
        this(description, 0);
    }

    @Ignore
    public JointUsage(String description, int count) {
        setDescription(description);
        setCount(count);
    }

    public void incrementJointUsage() {
        incrementJointUsage(1);
    }

    public void incrementJointUsage(int increment) {
        setCount(getCount() + increment);
    }

    public void decrementJointUsage() {
        decrementJointUsage(1);
    }

    public void decrementJointUsage(int decrement) {
        setCount(Math.max(getCount() - decrement, 0));
    }
}