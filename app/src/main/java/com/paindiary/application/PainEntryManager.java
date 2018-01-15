package com.paindiary.application;

import com.paindiary.data.Database;
import com.paindiary.data.dao.PainEntryDao;
import com.paindiary.domain.PainEntry;

import java.util.Date;
import java.util.List;

public class PainEntryManager {

    private PainEntryDao _dao;

    private PainEntryManager() {
        _dao = Database.getInstance().getPainDiaryDb().painEntryDao();
    }

    private static PainEntryManager _instance;

    public static synchronized PainEntryManager getInstance() {
        if (_instance == null)
            _instance = new PainEntryManager();
        return _instance;
    }

    public PainEntry get(long id) {
        return _dao.get(id);
    }

    public List<PainEntry> getAll() {
        return _dao.getAll();
    }

    public List<PainEntry> getAll(Date fromDate, Date untilDate, int minLevel) {
        return _dao.getAllFiltered(fromDate, untilDate, minLevel);
    }

    public PainEntry add(Date date, int painLevel, String comment, String... joints) {
        return add(new PainEntry(date, painLevel, comment, joints));
    }

    public PainEntry add(PainEntry entry) {
        long id = _dao.insert(entry);
        entry.setId(id);

        JointUsageManager.getInstance().incrementJointUsages(entry.getJoints());

        return entry;
    }

    public PainEntry update(PainEntry entry) {
        String[] oldJoints = get(entry.getId()).getJoints();
        _dao.update(entry);

        JointUsageManager.getInstance().updateJointUsages(oldJoints, entry.getJoints());

        return entry;
    }

    public void delete(long id) {
        delete(get(id));

    }

    public void delete(PainEntry entry) {
        _dao.delete(entry);
        JointUsageManager.getInstance().decrementJointUsages(entry.getJoints());
    }

    public void deleteAll() {
        List<PainEntry> entries = getAll();
        for (PainEntry e : entries) {
            delete(e);
        }
    }
}
