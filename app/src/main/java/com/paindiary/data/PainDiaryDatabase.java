package com.paindiary.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.paindiary.data.dao.JointUsageDao;
import com.paindiary.data.dao.PainEntryDao;
import com.paindiary.domain.JointUsage;
import com.paindiary.domain.PainEntry;

@Database(entities = {PainEntry.class, JointUsage.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class PainDiaryDatabase extends RoomDatabase {

    public static final String DB_NAME = "paindiary.db";

    public static final String PAIN_ENTRIES_TABLE_NAME = "pain_entries";
    public static final String PAIN_ENTRIES_ID_COLUMN = "id";
    public static final String PAIN_ENTRIES_DATE_COLUMN = "date";
    public static final String PAIN_ENTRIES_LEVEL_COLUMN = "level";
    public static final String PAIN_ENTRIES_COMMENTS_COLUMN = "comments";
    public static final String PAIN_ENTRIES_JOINTS_COLUMN = "joints";

    public static final String JOINT_USAGE_TABLE_NAME = "joint_usages";
    public static final String JOINT_USAGE_ID_COLUMN = "id";
    public static final String JOINT_USAGE_DESCRIPTION_COLUMN = "description";
    public static final String JOINT_USAGE_COUNT_COLUMN = "count";

    public abstract PainEntryDao painEntryDao();

    public abstract JointUsageDao jointUsageDao();
}
