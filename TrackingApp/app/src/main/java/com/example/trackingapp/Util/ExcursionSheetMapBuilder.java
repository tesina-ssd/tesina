package com.example.trackingapp.Util;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExcursionSheetMapBuilder {
    private String activityType;
    private Date startingTimeDate;
    private Date finishingTimeDate;
    private int peopleNumber;

    public ExcursionSheetMapBuilder setActivityType(String activityTime) {
        this.activityType = activityTime;
        return this;
    }

    public ExcursionSheetMapBuilder setStartingTimeDate(Date startingTimeDate) {
        this.startingTimeDate = startingTimeDate;
        return this;
    }

    public ExcursionSheetMapBuilder setFinishingTimeDate(Date finishingTimeDate) {
        this.finishingTimeDate = finishingTimeDate;
        return this;
    }

    public ExcursionSheetMapBuilder setPeopleNumber(int peopleNumber) {
        this.peopleNumber = peopleNumber;
        return this;
    }

    public Map<String, Object>  build() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("activityType", activityType);
        ret.put("startingTimeDate", new Timestamp(startingTimeDate));
        ret.put("finishingTimeDate", new Timestamp(finishingTimeDate));
        ret.put("peopleNumber", peopleNumber);
        return ret;
    }
}
