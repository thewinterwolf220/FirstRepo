package com.phoenix;

import java.util.Calendar;

class Factory {

    //______________________________________EVENT BUILDERS______________________________________________________________
    static iEvent buildEvent(String activity, String place, Calendar calendar, String details) {
        return new iEvent(activity, place, calendar, details);
    }

    static iEvent buildEvent(iEvent e) {
        return new iEvent(e);
    }
    //------------------------------------------------------------------------------------------------------------------


    //______________________________________DURABLE EVENT BUILDERS______________________________________________________
    static iDurableEvent buildEvent(String activity, String place, Calendar calendar, String details, Calendar endDate) {
        return new iDurableEvent(activity, place, calendar, details, endDate);
    }

    static iDurableEvent buildEvent(iEvent e, Calendar endDate) {
        return new iDurableEvent(e, endDate);
    }
    //------------------------------------------------------------------------------------------------------------------


    //_______________________________________CONFERENCE BUILDERS________________________________________________________
    static iConference buildConference(String activity, String place, Calendar calendar, String details,
                                       String speaker, String topic, int numberOfAttendees) {
        return new iConference(activity, place, calendar, details, speaker, topic, numberOfAttendees);
    }

    static iConference buildConference(iEvent e, String speaker, String topic, int numberOfAttendees) {
        return new iConference(e, speaker, topic, numberOfAttendees);
    }
    //------------------------------------------------------------------------------------------------------------------


    //_______________________________________TASK BUILDERS______________________________________________________________
    static iTask buildTask(String activity, String place, Calendar calendar, String details, iTask.Priority priority) {
        return new iTask(activity, place, calendar, details, priority);
    }

    static iTask buildTask(iEvent e, iTask.Priority priority) {
        return new iTask(e, priority);
    }
    //------------------------------------------------------------------------------------------------------------------
}
