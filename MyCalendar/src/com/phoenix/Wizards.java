package com.phoenix;

import com.phoenix.calendar.Task;
import com.phoenix.calendar.Utils;

import java.util.Calendar;
import java.util.Scanner;

import static com.phoenix.Factory.*;

class Wizards {
    private static Scanner reader = new Scanner(System.in);

    static iEvent eventWizard() {
        System.out.println("Activity: ");
        String activity = reader.nextLine();

        System.out.println("Place: ");
        String place = reader.nextLine();

        System.out.println("Date: hh mm [dd MM YYYY]");
        String time = reader.nextLine();

        Calendar date = Utils.createTime(Utils.lineToTime(time));

        System.out.println("Details? ");
        String details = reader.nextLine();

        return buildEvent(activity, place, date, details);
    }


    static iDurableEvent durableEventWizard() {
        iEvent tmp = buildEvent(eventWizard());

        System.out.println("End: hh mm dd [MM YYYY]");
        String time = reader.nextLine();

        Calendar end = Utils.createTime(Utils.lineToTime(time));

        return buildEvent(tmp, end);
    }


    static iTask taskWizard() {
        iEvent tmp = buildEvent(eventWizard());

        System.out.println("Insert priority level of the task");
        System.out.println("1. Low\n2. Medium\n3. High");
        String priority = reader.next();

        return buildTask(tmp, Task.getPriority(Integer.valueOf(priority)));
    }


    // TODO: 28/11/18 Write Object version

    static iConference conferenceWizard() {
        System.out.println("Speaker of the conference: ");
        String speaker = reader.nextLine();

        System.out.println("Topic of the conference: ");
        String topic = reader.nextLine();

        System.out.println("Number of attendees: ");
        String attendees = reader.nextLine();

        iEvent tmp = buildEvent(eventWizard());
        return buildConference(tmp, speaker, topic, Integer.valueOf(attendees));
    }

}
