package com.phoenix;

import com.phoenix.calendar.Event;
import com.phoenix.calendar.Utils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class iEvent implements Event {
    private String activity;
    private String place;
    private Calendar calendar;
    private String details;

    iEvent(String activity, String place, Calendar calendar, String details) {
        this.activity = activity;
        this.place = place;
        this.calendar = calendar;
        this.details = details;
    }

    //Shallow copy builder
    iEvent(@NotNull Event e) {
        this.activity = e.getActivity();
        this.place = e.getPlace();
        this.details = e.getDetails();
        this.calendar = Utils.createTime(e);
    }

    String timeAsString(Calendar calendar) {
        return dateFormat.format(calendar.getTime());
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public ArrayList<Object> getAttributes() {
        ArrayList<Object> attributes = new ArrayList<>(4);
        attributes.add(this.activity);
        attributes.add(this.place);
        attributes.add(this.calendar);
        attributes.add(this.details);
        return attributes;
    }

    @Override
    public String getPlace() {
        return this.place;
    }

    @Override
    public Calendar getCalendar() {
        return calendar;
    }


    @Override
    public String getDetails() {
        return details;
    }

    @Override
    public String getActivity() {
        return activity;
    }

    @Override
    public String toString() {
        return "\n"
                + ("Activity: " + activity)
                + (place != null? "\nPlace: " + place : "")
                + ("\nTime: " + timeAsString(calendar))
                + (details != null && !details.equalsIgnoreCase("") ? "\nOther details: " + details : "")
                + (isPast() ? "" : "\n" + timeFromNow());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (obj == null || obj.getClass() != this.getClass()) return false;
        iEvent otherEvent = (iEvent) obj;
        return this.activity.equalsIgnoreCase(otherEvent.activity)
                && this.place.equalsIgnoreCase(otherEvent.place)
                && this.calendar.getTime() == otherEvent.calendar.getTime();
    }


}

