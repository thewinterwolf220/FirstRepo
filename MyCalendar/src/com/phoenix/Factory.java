package com.phoenix;

import java.util.Calendar;

class Factory {

    static iEvent buildEvent(String activity, String place, Calendar calendar, String details) {
        return new iEvent(activity, place, calendar, details);
    }

    static iEvent buildEvent(iEvent e) {
        return new iEvent(e);
    }

    static iDurableEvent buildEvent(iEvent e, Calendar endDate) {
        return new iDurableEvent(e, endDate);
    }

    static iConference buildConference(iEvent e, String speaker, String topic, int numberOfAttendees) {
        return new iConference(e, speaker, topic, numberOfAttendees);
    }

    static iTask buildTask(iEvent e, iTask.Priority priority) {
        return new iTask(e, priority);
    }
}
