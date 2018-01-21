package com.paindiary.domain;

import com.paindiary.util.ListUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.paindiary.util.DateUtils.startOfDay;

public class GraphData {
    private List<Integer> _levels;
    public List<Integer> getLevels() {
        return _levels;
    }

    public void setLevels(List<Integer> levels) {
        _levels = levels;
    }

    private List<Integer> _numberOfJoints;
    public List<Integer> getNumberOfJoints() {
        return _numberOfJoints;
    }

    public void setNumberOfJoints(List<Integer> numberOfJoints) {
        _numberOfJoints = numberOfJoints;
    }

    private Date _date;
    public Date getDate() {
        return _date;
    }

    public void setDate(Date date) {
        _date = startOfDay(date);
    }

    private PartOfDayDistribution _partOfDayDist;
    public PartOfDayDistribution getPartOfDayDist(){return _partOfDayDist;}

    public void setPartOfDayDist(PartOfDayDistribution partOfDayDist){
        _partOfDayDist = partOfDayDist;
    }

    public GraphData(Date date){
        this(new ArrayList<Integer>(), new ArrayList<Integer>(), date, new PartOfDayDistribution());
    }

    public GraphData(List<Integer> painLevel, List<Integer> jointCount, Date date, PartOfDayDistribution dist) {
        setLevels(painLevel);
        setNumberOfJoints(jointCount);
        setDate(date);
        setPartOfDayDist(dist);
    }

    public void addEntry(int painLevel, int jointCount, Date time)
    {
        getLevels().add(painLevel);
        getNumberOfJoints().add(jointCount);
        getPartOfDayDist().add(time);
    }
    public double getAveragePainLevel()
    {
        return ListUtils.avg(getLevels());
    }
    public double getAverageJountCount()
    {
        return ListUtils.avg(getNumberOfJoints());
    }
}
