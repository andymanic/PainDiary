package com.paindiary.domain;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.paindiary.domain.PartOfDay.mapHourToPartOfDay;

public class PartOfDayDistribution {


        private Map<PartOfDay, Integer> _distribution;
        public Map<PartOfDay, Integer> getDistribution(){return _distribution;}

        public void setDistribution(Map<PartOfDay, Integer> distribution){
            for (PartOfDay p: PartOfDay.values()
                    ) {
                if(!distribution.containsKey(p))
                {
                    distribution.put(p, 0);
                }
            }
            _distribution = distribution;
        }

        public PartOfDayDistribution(){
            this(new HashMap<PartOfDay, Integer>());
        }
        public PartOfDayDistribution(Map<PartOfDay, Integer> distribution){
            setDistribution(distribution);
        }

        public void add(Date date){
            PartOfDay day = PartOfDay.mapHourToPartOfDay(date);
            getDistribution().put(day, getDistribution().get(day) + 1);
        }

        public void subtract(Date date){
            PartOfDay day = PartOfDay.mapHourToPartOfDay(date);
            getDistribution().put(day, Math.max(getDistribution().get(day) - 1, 0));
        }

        public PartOfDayDistribution merge(PartOfDayDistribution otherDist){
            for (PartOfDay p: PartOfDay.values()
                    ) {
                getDistribution().put(p, getDistribution().get(p) + otherDist.getValue(p));
            }
            return this;
        }

        public Integer getValue(PartOfDay part){
            return getDistribution().get(part);
        }

        public List<PartOfDay> getPartsOfDayWithHighestValue() {
            List<PartOfDay> candidates = new ArrayList<>();
            int max = getValue(PartOfDay.EARLY_MORNING);
            for (PartOfDay p : PartOfDay.values()) {
                int value = getValue(p);
                if(value > max) {
                    candidates.clear();
                    candidates.add(p);
                    max = value;
                } else if (value == max) {
                    candidates.add(p);
                }
            }
            return candidates;
        }

        public List<PartOfDay> getPartsOfDayWithLowestValue() {
            List<PartOfDay> candidates = new ArrayList<>();
            int min = getValue(PartOfDay.EARLY_MORNING);
            for (PartOfDay p : PartOfDay.values()) {
                int value = getValue(p);
                if(value < min) {
                    candidates.clear();
                    candidates.add(p);
                    min = value;
                } else if (value == min) {
                    candidates.add(p);
                }
            }
            return candidates;
        }
}
