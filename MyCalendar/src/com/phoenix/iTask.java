package com.phoenix;

import com.phoenix.calendar.api.SimpleEvent;
import com.phoenix.calendar.api.Task;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

class iTask extends iEvent implements Task {

    private Priority priority;

    iTask(String activity, String place, Calendar calendar, String details, Priority priority) {
        super(activity, place, calendar, details);
        this.priority = priority;
    }

    iTask(SimpleEvent e, Priority priority) {
        super(e);
        this.priority = priority;
    }

    @Override
    public @NotNull ArrayList<Object> getAttributes() {
        ArrayList<Object> attributes = super.getAttributes();
        attributes.add(this.priority);
        return attributes;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        String priority = getPriority().name().toLowerCase();
        return super.toString()
                + (getPriority() != null ? "\nPriority: " + priority + "\n" : "");
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && priority.getImportance() == ((iTask) obj).priority.getImportance();
    }

}
