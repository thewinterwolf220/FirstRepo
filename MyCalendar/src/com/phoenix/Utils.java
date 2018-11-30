package com.phoenix;

import com.phoenix.calendar.Task;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class Utils {

    @Contract("null -> null")
    static Timestamp calendarToTimestamp(Object obj) {
        if (!(obj instanceof Calendar))
            return null;

        Calendar newCalendar = (Calendar) obj;
        long timeInMillis = newCalendar.getTimeInMillis();
        return new Timestamp(timeInMillis);
    }

    @Contract("null -> null")
    static Calendar timestampToCalendar(Object obj) {
        if (!(obj instanceof Timestamp))
            return null;

        Timestamp newTS = (Timestamp) obj;
        long timeInMs = newTS.getTime();

        return new Calendar.Builder().setInstant(timeInMs).build();
    }

    @NotNull
    @Contract("_ -> new")
    static iEvent fetchEvent(@NotNull ResultSet resultSet) {
        try {
            String activity = resultSet.getString(1);
            String place = resultSet.getString(2);
            Calendar date = timestampToCalendar(resultSet.getTimestamp(3));
            String details = resultSet.getString(4);

            return new iEvent(activity, place, date, details);
        } catch (SQLException e) {
            System.out.println("Exception in fetchEvent");
            e.printStackTrace();
            return null;
        }
    }

    // TODO: 29/11/18 reuse code common to fetchEvent and fetchTask

    @NotNull
    @Contract("_ -> new")
    static iTask fetchTask(@NotNull ResultSet resultSet) throws SQLException {
        String activity = resultSet.getString(1);
        String place = resultSet.getString(2);
        Calendar date = timestampToCalendar(resultSet.getTimestamp(3));
        String details = resultSet.getString(4);
        int importance = resultSet.getInt(5);
        Task.Priority priority = Task.getPriority(importance);

        return new iTask(activity, place, date, details, priority);
    }

    static iDurableEvent fetchDurableEvent(@NotNull ResultSet resultSet) throws SQLException {

        String activity = resultSet.getString(1);
        String place = resultSet.getString(2);
        Calendar date = timestampToCalendar(resultSet.getTimestamp(3));
        String details = resultSet.getString(4);
        Calendar endTime = timestampToCalendar(resultSet.getTimestamp(5));

        return new iDurableEvent(activity, place, date, details, endTime);
    }


}
