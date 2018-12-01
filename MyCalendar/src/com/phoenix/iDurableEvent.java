package com.phoenix;

import com.phoenix.calendar.DurableEvent;
import com.phoenix.calendar.Event;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

public class iDurableEvent extends iEvent implements DurableEvent {

    private Calendar endTime;

    iDurableEvent(String activity, String place, Calendar calendar, String details, Calendar endTime) {
        super(activity, place, calendar, details);
        this.endTime = endTime;
    }

    iDurableEvent(@NotNull Event e, Calendar endTime) {
        super(e);
        this.endTime = endTime;
    }

    @Override
    public Calendar getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return super.toString()
                + " -> " + timeAsString(endTime) + "\n"
                + "Lasts:" + lasts();
    }

    @Override
    public @NotNull ArrayList<Object> getAttributes() {
        ArrayList<Object> attributes = super.getAttributes();
        attributes.add(this.endTime);
        return attributes;
    }
}
