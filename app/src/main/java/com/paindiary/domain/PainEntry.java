package com.paindiary.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.paindiary.data.PainDiaryDatabase;

import java.util.Date;

@Entity(tableName = PainDiaryDatabase.PAIN_ENTRIES_TABLE_NAME)
public class PainEntry {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = PainDiaryDatabase.PAIN_ENTRIES_ID_COLUMN)
    private long _id;

    public long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    @ColumnInfo(name = PainDiaryDatabase.PAIN_ENTRIES_DATE_COLUMN)
    private Date _date;

    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = date;
    }

    @ColumnInfo(name = PainDiaryDatabase.PAIN_ENTRIES_LEVEL_COLUMN)
    private int _painLevel;

    public int getPainLevel() {
        return _painLevel;
    }

    public void setPainLevel(int painLevel) {
        _painLevel = painLevel;
    }

    @ColumnInfo(name = PainDiaryDatabase.PAIN_ENTRIES_COMMENTS_COLUMN)
    private String _comment;

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        _comment = comment;
    }

    @ColumnInfo(name = PainDiaryDatabase.PAIN_ENTRIES_JOINTS_COLUMN)
    private String[] _joints;

    public String[] getJoints() {
        return _joints;
    }

    public void setJoints(String[] joints) {
        _joints = new String[joints.length];
        for (int i = 0; i < joints.length; i++) {
            _joints[i] = joints[i].toLowerCase();
        }
    }

    public PainEntry() {
    }

    @Ignore
    public PainEntry(Date date, int painLevel, String comment, String... joints) {
        setDate(date);
        setPainLevel(painLevel);
        setComment(comment);
        setJoints(joints);
    }

    @Ignore
    public PainEntry(long id, Date date, int painLevel, String comment, String... joints) {
        this(date, painLevel, comment, joints);
        setId(id);
    }

    public boolean hasJoint(String joint) {
        for (String j : _joints) {
            if (j.toLowerCase().equals(joint)) return true;
        }
        return false;
    }
}