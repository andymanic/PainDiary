package com.paindiary.application;

import com.paindiary.data.Database;
import com.paindiary.data.dao.JointUsageDao;
import com.paindiary.domain.JointUsage;

import java.util.Arrays;
import java.util.List;

public class JointUsageManager {

    private JointUsageDao _dao;

    private JointUsageManager() {
        _dao = Database.getInstance().getPainDiaryDb().jointUsageDao();
    }

    private static JointUsageManager _instance;

    public static synchronized JointUsageManager getInstance() {
        if (_instance == null)
            _instance = new JointUsageManager();
        return _instance;
    }

    public JointUsage get(long id) {
        return _dao.get(id);
    }

    public JointUsage getByDescription(String description) {
        return _dao.getByDescription(description);
    }

    public List<JointUsage> getAll() {
        return _dao.getAll();
    }

    public List<JointUsage> getAllByUsage() {
        return _dao.getAllByUsage();
    }

    public JointUsage add(String description) {
        return add(new JointUsage(description, 1));
    }

    public JointUsage add(JointUsage jointUsage) {
        long id = _dao.insert(jointUsage);
        jointUsage.setId(id);

        return jointUsage;
    }

    public JointUsage update(JointUsage jointUsage) {
        _dao.update(jointUsage);

        return jointUsage;
    }

    public void delete(long id) {
        delete(get(id));
    }

    public void delete(JointUsage jointUsage) {
        _dao.delete(jointUsage);
    }

    public void deleteAll() {
        List<JointUsage> entries = getAll();
        for (JointUsage e : entries) {
            delete(e);
        }
    }

    public void updateJointUsages(String[] oldJoints, String[] newJoints) {
        List<String> oldList = Arrays.asList(oldJoints);
        List<String> newList = Arrays.asList(newJoints);

        for (String s : newList) {
            if (!oldList.contains(s))
                incrementJointUsages(s);
        }

        for (String s : oldList) {
            if (!newList.contains(s))
                decrementJointUsages(s);
        }
    }

    public void incrementJointUsages(String... joints) {
        for (String joint : joints) {
            JointUsage j = getByDescription(joint);
            if (j != null)
                incrementUsage(j);
            else
                add(joint);
        }
    }

    public void incrementUsage(JointUsage jointUsage) {
        jointUsage.incrementJointUsage();
        update(jointUsage);
    }

    public void decrementJointUsages(String... joints) {
        for (String joint : joints) {
            JointUsage j = getByDescription(joint);
            if (j != null)
                decrementUsage(j);
        }
    }

    public void decrementUsage(JointUsage jointUsage) {
        jointUsage.decrementJointUsage();

        if (jointUsage.getCount() > 0)
            update(jointUsage);
        else
            delete(jointUsage);
    }
}
