package com.phoenix;

import com.phoenix.calendar.api.Task;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.phoenix.Utils.calendarToTimestamp;
import static com.phoenix.calendar.api.Utils.createTime;

class DatabaseOperator {
    private static PGSimpleDataSource source = new PGSimpleDataSource();
    private static Connection connection;
    private static String username = null;
    private static String password = null;
    private static boolean loggedIn = false;

    static List<iEvent> eventsBetween(Calendar begin, Calendar end) {
        List<iEvent> all = new ArrayList<>();

        Timestamp begin_ts = calendarToTimestamp(begin);
        Timestamp end_ts = calendarToTimestamp(end);

        String[] tables = getTables().toArray(new String[]{});

        for (String table : tables) {

            String query = selectBetween(table) + "'" + begin_ts + "'" + " AND " + "'" + end_ts + "'";

            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                ResultSet set = statement.getResultSet();

                while (set.next()) {
                    iEvent fetched = Utils.fetchGeneric(table, set);
                    all.add(fetched);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        all.sort(Comparator.comparing(iEvent::getCalendar));
        return all;
    }

    static void establishConnection() {
        assert connection == null;

        if (!loggedIn) login();
        configureDatabaseConnection();

        try {
            connection = source.getConnection();
            loggedIn = true;
        } catch (SQLException e) {
            System.exit(1);
        }

    }

    static List<iEvent> allFutureEvents() {
        List<iEvent> all = new ArrayList<>();
        String[] tables = getTables().toArray(new String[]{});

        for (String table : tables) {

            String query = selectFutureEvent(table);

            try (Statement statement = connection.createStatement()) {
                statement.executeQuery(query);
                ResultSet set = statement.getResultSet();
                while (set.next()) {
                    iEvent fetched = Utils.fetchGeneric(table, set);
                    all.add(fetched);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        all.sort(Comparator.comparing(iEvent::getCalendar));
        return all;
    }

    static boolean addEvent() {

        // To add an event, we must first ask for the attributes, which are inserted via the Wizards class.
        // These are converted to the corresponding types in postgresql and added to the database
        // The same occurs, but with variations according to the object type, in others adding methods

        ArrayList<Object> attributes = Wizards.eventWizard().getAttributes();

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));

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
        } catch (Exception exx) {
            return false;
        }
    }

    static boolean addTask() {

        ArrayList<Object> attributes = Wizards.taskWizard().getAttributes();

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));

        //Don't be scared, it's just a cast operation.
        int priority = ((Task.Priority) attributes.get(4)).getImportance();

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

        } catch (Exception exx) {
            return false;
        }
    }

    static boolean addDurableEvent() {
        ArrayList<Object> attributes = Wizards.durableEventWizard().getAttributes();

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));
        Timestamp endOfEvent = calendarToTimestamp(attributes.get(4));

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
        } catch (Exception exx) {
            return false;
        }
    }

    private static boolean addPreparedEvent() {
        Object[] attributes = Wizards.eventWizard().getAttributes().toArray();

        String sql = "INSERT INTO events(activity, place, time_and_date, details) VALUES(?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, (String) attributes[0]);
            statement.setString(2, (String) attributes[1]);
            statement.setTimestamp(3, calendarToTimestamp(attributes[2]));
            statement.setString(4, (String) attributes[3]);

            int count = statement.executeUpdate();
            System.out.println(count);
            return statement.execute();
        } catch (Exception exx) {
            return false;
        }
    }

    @TestOnly
    private static void test_speed_prepared_and_not() {
        long start = System.currentTimeMillis();

        String sql = "INSERT INTO events(activity,time_and_date) VALUES(?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Event1");
            statement.setTimestamp(2, calendarToTimestamp(createTime(18, 0)));
            statement.execute();
        } catch (Exception exx) {
            System.out.println("Problem here");
        }

        long stop = System.currentTimeMillis();
        long elapsed = stop - start;

        System.out.println("Elapsed when using a prepared statement: " + elapsed);


        start = System.currentTimeMillis();
        try (Statement statement = connection.createStatement()) {
            String insert = "INSERT INTO events(activity, time_and_date) VALUES " +
                    "('Event2', '2018-12-2 18:00')";
            statement.execute(insert);
        } catch (Exception exx) {
            System.out.println("Problem here");
        }
        stop = System.currentTimeMillis();
        elapsed = stop - start;

        System.out.println("Elapsed when using a statement: " + elapsed);
    }

    private static int countTables() {
        int count = 0;
        try {
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
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet res = metaData.getTables(null, null, null, new String[]{"TABLE"});

            while (res.next())
                tables.add(res.getString("TABLE_NAME"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    @NotNull
    @Contract(pure = true)
    private static String selectFutureEvent(@NotNull String table) {
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

    @NotNull
    @Contract(pure = true)
    private static String selectBetween(@NotNull String table) {
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

    // Just to learn new things, metadata and other stuff.
    private static void getCols(@NotNull String table) {
        List<String> columns = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {

            statement.executeQuery("SELECT * FROM " + table);

            ResultSet resultSetMetaData = statement.getResultSet();
            ResultSetMetaData r = resultSetMetaData.getMetaData();

            for (int i = 1; i <= r.getColumnCount(); i++)
                columns.add(r.getColumnName(i));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(columns.stream().collect(Collectors.joining(", ", "{", "}")));
    }


    private static void configureDatabaseConnection() {
        source.setUser(username);
        source.setPassword(password);
        source.setServerName("localhost");
        source.setDatabaseName("calendar");
    }

    private static void login() {
        System.out.println("User ");
        username = Main.keyboard.nextLine();
        System.out.println("Password: ");
        password = Main.keyboard.nextLine();
    }

}

