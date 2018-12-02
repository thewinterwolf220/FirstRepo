package com.phoenix;

import com.phoenix.calendar.Task;
import org.jetbrains.annotations.NotNull;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.phoenix.Utils.calendarToTimestamp;
import static com.phoenix.calendar.Utils.createTime;

class DatabaseOperator {
    private static final int MAX_ATTEMPTS = 3;
    private static PGSimpleDataSource source = new PGSimpleDataSource();
    private static int attempts = 0;
    private static String username = null;
    private static String password = null;
    private static boolean logged = false;


    static List<iEvent> eventsBetween(Calendar begin, Calendar end) {
        List<iEvent> all = new ArrayList<>();

        Timestamp begin_ts = calendarToTimestamp(begin);
        Timestamp end_ts = calendarToTimestamp(end);
        String[] tables = getTables().toArray(new String[]{});

        try (Connection connection = connectToDatabase()) {
            for (String table : tables) {

                String query = selectBetween(table) + "'" + begin_ts + "'" + " AND " + "'" + end_ts + "'";

                assert connection != null;
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

                assert connection != null;
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

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));

        try (Connection connection = connectToDatabase()) {
            assert connection != null;
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

    private static boolean addPreparedEvent() {
        Object[] attributes = Wizards.eventWizard().getAttributes().toArray();

        try (Connection connection = connectToDatabase()) {
            assert connection != null;

            String sql = "INSERT INTO events(activity, place, time_and_date, details) VALUES(?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, (String) attributes[0]);
                statement.setString(2, (String) attributes[1]);
                statement.setTimestamp(3, calendarToTimestamp(attributes[2]));
                statement.setString(4, (String) attributes[3]);

                int count = statement.executeUpdate();
                System.out.println(count);
                return statement.execute();
            }
        } catch (Exception exx) {
            return false;
        }
    }

    private static void test_speed_prepared_and_not() {
        long start = System.currentTimeMillis();

        try (Connection connection = connectToDatabase()) {
            assert connection != null;
            String sql = "INSERT INTO events(activity,time_and_date) VALUES(?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "Event1");
                statement.setTimestamp(2, calendarToTimestamp(createTime(18, 00)));
                statement.execute();
            }
        } catch (Exception exx) {
        }

        long stop = System.currentTimeMillis();
        long elapsed = stop - start;

        System.out.println("Elapsed when using a prepared statement: " + elapsed);


        start = System.currentTimeMillis();
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {
                String insert = "INSERT INTO events(activity, time_and_date) VALUES " +
                        "('Event2', '2018-12-2 18:00')";
                statement.execute(insert);
            }
        } catch (Exception exx) {
        }
        stop = System.currentTimeMillis();
        elapsed = stop - start;

        System.out.println("Elapsed when using a statement: " + elapsed);
    }


    public static void main(String[] args) {
        test_speed_prepared_and_not();
    }

    static boolean addTask() {

        ArrayList<Object> attributes = Wizards.taskWizard().getAttributes();

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));

        //Don't be scared, it's just a cast operation.
        int priority = ((Task.Priority) attributes.get(4)).getImportance();

        try (Connection connection = connectToDatabase()) {
            assert connection != null;
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

        Timestamp timeOfEvent = calendarToTimestamp(attributes.get(2));
        Timestamp endOfEvent = calendarToTimestamp(attributes.get(4));

        try (Connection connection = connectToDatabase()) {
            assert connection != null;
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

            assert connection != null;

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

            assert connection != null;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet res = metaData.getTables(null, null, null, new String[]{"TABLE"});

            while (res.next())
                tables.add(res.getString("TABLE_NAME"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

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

    private static Connection connectToDatabase() {
        source.setServerName("localhost");
        source.setDatabaseName("calendar");

        if (logged || username != null) { // Reuses username and password previously inserted
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

    // Just to learn new things, metadata and other stuff.
    private static void getCols(@NotNull String table) {
        List<String> columns = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            assert connection != null;
            try (Statement statement = connection.createStatement()) {

                statement.executeQuery("SELECT * FROM " + table);

                ResultSet resultSetMetaData = statement.getResultSet();
                ResultSetMetaData r = resultSetMetaData.getMetaData();

                for (int i = 1; i <= r.getColumnCount(); i++)
                    columns.add(r.getColumnName(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(columns.stream().collect(Collectors.joining(", ", "{", "}")));
    }
//
//    private static void prepSt() {
//        try (Connection connection = connectToDatabase()) {
//            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM tasks")) {
//
//
//
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


}

