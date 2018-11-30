package com.phoenix;

import com.phoenix.calendar.Event;
import com.phoenix.calendar.Task;
import com.phoenix.calendar.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class iTask extends iEvent implements Task {

    private Priority priority;

    iTask(String activity, String place, Calendar calendar, String details, Priority priority) {
        super(activity, place, calendar, details);
        this.priority = priority;
    }

    iTask(Event e, Priority priority) {
        super(e);
        this.priority = priority;
    }


    // Tested method.
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
