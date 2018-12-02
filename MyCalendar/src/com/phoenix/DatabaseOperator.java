package com.phoenix;

import com.phoenix.calendar.Task;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

class DatabaseOperator {

    private static final int MAX_ATTEMPTS = 3;
    private static PGSimpleDataSource source = new PGSimpleDataSource();
    private static int attempts = 0;
    private static String username = null;
    private static String password = null;
    private static boolean logged = false;

    static List<iEvent> eventsBetween(Calendar begin, Calendar end) {
        List<iEvent> all = new ArrayList<>();

        Timestamp begin_ts = Utils.calendarToTimestamp(begin);
        Timestamp end_ts = Utils.calendarToTimestamp(end);
        String[] tables = getTables().toArray(new String[]{});

        try (Connection connection = connectToDatabase()) {
            for (String table : tables) {

                String query = selectBetween(table) + "'" + begin_ts + "'" + " AND " + "'" + end_ts + "'";

                try (Statement statement = connection.createStatement()) {
                    statement.executeQuery(query);
                    ResultSet set = statement.getResultSet();
                    while (set.next()) {
                        iEvent fetched = Utils.fetchGeneric(table, set);
                        all.add(fetched);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        all.sort(Comparator.comparing(iEvent::getCalendar));
        return all;
    }

    static List<iEvent> allFutureEvents() {
        List<iEvent> all = new ArrayList<>();

        String[] tables = getTables().toArray(new String[]{});

        try (Connection connection = connectToDatabase()) {
            for (String table : tables) {

                String query = selectFutureEvent(table);

                try (Statement statement = connection.createStatement()) {
                    statement.executeQuery(query);
                    ResultSet set = statement.getResultSet();
                    while (set.next()) {
                        iEvent fetched = Utils.fetchGeneric(table, set);
                        all.add(fetched);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        all.sort(Comparator.comparing(iEvent::getCalendar));
        return all;
    }

    static boolean addEvent() {

        // To add an event, we must first ask for the attributes, which are inserted via the Wizards class.
        // These are converted to the corresponding types in postgresql and added to the database
        // The same occurs, but with variations according to the object type, in others adding methods

        ArrayList<Object> attributes = Wizards.eventWizard().getAttributes();

        Timestamp timeOfEvent = Utils.calendarToTimestamp(attributes.get(2));

        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String insert = "INSERT INTO events(activity, place, time_and_date, details) VALUES " +
                        "(" +
                        "'" + attributes.get(0) + "'," +
                        "'" + attributes.get(1) + "'," +
                        "'" + timeOfEvent + "'," +
                        "'" + attributes.get(3) + "'" +
                        ")";
                statement.execute(insert);
                return true;
            }
        } catch (Exception exx) {
            return false;
        }
    }

    static boolean addTask() {

        ArrayList<Object> attributes = Wizards.taskWizard().getAttributes();

        Timestamp timeOfEvent = Utils.calendarToTimestamp(attributes.get(2));

        //Don't be scared, it's just a cast operation.
        int priority = ((Task.Priority) attributes.get(4)).getImportance();

        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String insert = "INSERT INTO tasks(activity, place, time_and_date, details, priority) VALUES" +
                        "( " +
                        "'" + attributes.get(0) + "'," +
                        "'" + attributes.get(1) + "'," +
                        "'" + timeOfEvent + "'," +
                        "'" + attributes.get(3) + "'," +
                        "'" + priority + "'" +
                        ")";

                statement.execute(insert);
                return true;
            }
        } catch (Exception exx) {
            return false;
        }
    }

    static boolean addDurableEvent() {
        ArrayList<Object> attributes = Wizards.durableEventWizard().getAttributes();

        Timestamp timeOfEvent = Utils.calendarToTimestamp(attributes.get(2));
        Timestamp endOfEvent = Utils.calendarToTimestamp(attributes.get(4));

        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String insert = "INSERT INTO durable_events(activity, place, time_and_date, details, end_time) VALUES" +
                        "( " +
                        "'" + attributes.get(0) + "'," +
                        "'" + attributes.get(1) + "'," +
                        "'" + timeOfEvent + "'," +
                        "'" + attributes.get(3) + "'," +
                        "'" + endOfEvent + "'" +
                        ")";
                statement.execute(insert);
                return true;
            }
        } catch (Exception exx) {
            return false;
        }
    }

    private static int countTables() {
        int count = 0;
        try (Connection connection = connectToDatabase()) {

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet res = metaData.getTables(null, null, null, new String[]{"TABLE"});

            while (res.next())
                count++;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static List<String> getTables() {
        List<String> tables = new ArrayList<>(countTables());

        try (Connection connection = connectToDatabase()) {

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet res = metaData.getTables(null, null, null, new String[]{"TABLE"});

            while (res.next())
                tables.add(res.getString("TABLE_NAME"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    private static String selectFutureEvent(String table) {
        switch (table) {
            case "events":
                return "SELECT activity, place, time_and_date, details FROM events " +
                        "WHERE time_and_date > clock_timestamp()";
            case "durable_events":
                return "SELECT activity, place, time_and_date, details, end_time FROM durable_events " +
                        "WHERE time_and_date > clock_timestamp()";
            case "tasks":
                return "SELECT activity, place, time_and_date, details, priority FROM tasks " +
                        "WHERE time_and_date > clock_timestamp()";
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static String selectBetween(String table) {
        switch (table) {
            case "events":
                return "SELECT activity, place, time_and_date, details FROM events " +
                        "WHERE time_and_date BETWEEN ";
            case "durable_events":
                return "SELECT activity, place, time_and_date, details, end_time FROM durable_events " +
                        "WHERE time_and_date BETWEEN ";
            case "tasks":
                return "SELECT activity, place, time_and_date, details, priority FROM tasks " +
                        "WHERE time_and_date BETWEEN ";
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static Connection connectToDatabase() {
        source.setServerName("localhost");
        source.setDatabaseName("calendar");

        if (logged) { // Reuses username and password previously inserted
            source.setUser(username);
            source.setPassword(password);
        } else login();

        try {
            logged = true;
            return source.getConnection();
        } catch (SQLException e) {
            attempts++;
            logged = false;
            System.out.println("Wrong credentials, retry.");

            if (attempts > MAX_ATTEMPTS) {
                System.out.println("You can't retry");
                return null;
            }

            return connectToDatabase();
        }
    }

    private static void login() {
        System.out.println("User ");
        username = Main.keyboard.nextLine();

        System.out.println("Password: ");
        password = Main.keyboard.nextLine();

        source.setUser(username);
        source.setPassword(password);
    }

}

