package com.phoenix;

import com.phoenix.calendar.Conference;
import com.phoenix.calendar.Event;

import java.util.Calendar;

public class iConference extends iEvent implements Conference {

    private String speaker;
    private String topic;
    private int numberOfAttendees;

    iConference(String activity, String place, Calendar calendar, String details, String speaker, String topic, int numberOfAttendees) {
        super(activity, place, calendar, details);
        this.speaker = speaker;
        this.topic = topic;
        this.numberOfAttendees = numberOfAttendees;
    }

    iConference(Event e, String speaker, String topic, int numberOfAttendees) {
        super(e);
        this.speaker = speaker;
        this.topic = topic;
        this.numberOfAttendees = numberOfAttendees;
    }

    @Override
    public String getSpeaker() {
        return speaker;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    @Override
    public int getNumberOfAttendees() {
        return numberOfAttendees;
    }

    @Override
    public String toString() {
        return "Conference\n"
                + "Speaker: " + speaker + "\n"
                + "Topic: " + topic + "\n"
                + "Number of attendees: " + numberOfAttendees + "\n"
                + super.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && this.speaker.equalsIgnoreCase(((iConference) obj).getSpeaker())
                && topic.equalsIgnoreCase(((iConference) obj).getTopic());
    }
}
