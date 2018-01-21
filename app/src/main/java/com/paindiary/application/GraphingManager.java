package com.paindiary.application;


import com.jjoe64.graphview.GraphView;
import com.paindiary.domain.GraphData;
import com.paindiary.domain.PainEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphingManager {

   private GraphingManager(){

   }

   private static GraphingManager _instance;

   public static synchronized GraphingManager getInstance()
   {
       if(_instance == null) {
            _instance = new GraphingManager();
       }
       return _instance;
   }
   public List<GraphData> get(Date fromDate, Date untilDate){ return get(fromDate, untilDate, 0); }
   public List<GraphData> get(Date fromDate, Date untilDate, int minLevel)
   {

       List<PainEntry> entries = PainEntryManager.getInstance().getAll(fromDate, untilDate, minLevel);
       Map<String, GraphData> dataPoints = new HashMap<>();


       for (PainEntry e : entries
            ) {
           int painLevel = e.getPainLevel();
           int jointCount  = e.getJoints().length;
           Date date = e.getDate();

           String keyDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
           GraphData g;
           if(!dataPoints.containsKey(keyDate))
           {
               g = new GraphData(date);
               dataPoints.put(keyDate, g);
           }
           else
           {
               g = dataPoints.get(keyDate);

           }
           g.addEntry(painLevel, jointCount, date);

       }

       List<GraphData> graphList = new ArrayList<GraphData>(dataPoints.values());
       Collections.sort(graphList, new Comparator<GraphData>() {
           public int compare(GraphData obj1, GraphData obj2) {
               return obj1.getDate().compareTo(obj2.getDate());
           }
       });
       return graphList;
   }
}
