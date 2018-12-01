package com.phoenix;

import com.phoenix.calendar.Task;
import jdk.jshell.execution.Util;
import jdk.jshell.spi.ExecutionControlProvider;
import org.jetbrains.annotations.NotNull;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.phoenix.Main.keyboard;

class DatabaseOperator {


    private static boolean DEBUG = true;

    // We want their scope to be the whole runtime.
    private static String username = null;
    private static String password = null;


    // Test here
    public static void main(String[] args) {
        Comparator<iEvent> comparator = Comparator.comparing(iEvent::getCalendar);
        Stream.of(fetchAll()).forEachOrdered(System.out::println);
    }
    //-------------------------------------Methods to get stuff --------------------------------------------------------

    static void upcoming() {
        if (countEvents() > 0) upcomingEvents();
        else System.out.println("No upcoming events");

        if (countTasks() > 0) upcomingTasks();
        else System.out.println("No upcoming tasks");

        if (countDurEvents() > 0) upcomingDurableEvents();
        else System.out.println("No upcoming events");

    }


    private static List<iEvent> fetchAll() {
        List<iEvent> eventList = new ArrayList<>();

        try (Connection connection = connectToDatabase()) {

            try (Statement statement = connection.createStatement()) {

                String select = "SELECT activity, place, time_and_date, details " +
                        "FROM events WHERE time_and_date > clock_timestamp()";

                statement.executeQuery(select);

                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    iEvent event = Utils.fetchEvent(resultSet);
                    eventList.add(event);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return eventList;
    }

    private static void upcomingEvents() {

        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String select = "SELECT activity, place, time_and_date, details " +
                        "FROM events WHERE time_and_date > clock_timestamp()";

                statement.executeQuery(select);
                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    iEvent fetchedEvent = Utils.fetchEvent(resultSet);
                    System.out.println(fetchedEvent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void upcomingTasks() {

        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String s = "SELECT activity, place, time_and_date, details, priority " +
                        "FROM tasks WHERE time_and_date > clock_timestamp()";
                statement.executeQuery(s);

                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    iTask fetchedTask = Utils.fetchTask(resultSet);
                    System.out.println(fetchedTask);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void upcomingDurableEvents() {
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String s = "SELECT activity, place, time_and_date, details, end_time " +
                        "FROM durable_events WHERE time_and_date > clock_timestamp()";
                statement.executeQuery(s);

                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    iDurableEvent fetchedDurableEvent = Utils.fetchDurableEvent(resultSet);
                    System.out.println(fetchedDurableEvent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // ---------------------------Counters of future elements-----------------------------------------------

    private static int countEvents() {
        int count = 0;
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String select = "SELECT activity FROM events WHERE time_and_date > clock_timestamp()";

                statement.executeQuery(select);
                count = (int) Stream.of(statement.getResultSet()).count();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static int countTasks() {
        int count = 0;
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String select = "SELECT activity FROM tasks WHERE time_and_date > clock_timestamp()";

                statement.executeQuery(select);
                count = (int) Stream.of(statement.getResultSet()).count();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private static int countDurEvents() {
        int count = 0;
        try (Connection connection = connectToDatabase()) {
            try (Statement statement = connection.createStatement()) {

                String select = "SELECT activity FROM durable_events WHERE time_and_date > clock_timestamp()";

                statement.executeQuery(select);
                count = (int) Stream.of(statement.getResultSet()).count();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;

    }
    //------------------------------------------------------------------------------------------------------------------


    //------------------------------Methods to insert into the database-------------------------------------------------

    static void addEvent() {

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
                boolean result = statement.execute(insert);

                if (!result) System.out.println("Added a new event");
            }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    static void addTask() {

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
                System.out.println("Added a new task");
            }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }

    static void addDurableEvent() {
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

                System.out.println("Added a new event");
            }
        } catch (Exception exx) {
            exx.printStackTrace();
        }
    }


    //-------------------------------------------------------------------------------------------------------------------
    private static Connection connectToDatabase() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setDatabaseName("calendar");
        source.setLoginTimeout(10);

        // The login should be asked only if credentials are not initialised.
        // TODO: 29/11/18 Improve logic, refactor.
        if (username == null && password == null)
            login(source);
        else {
            source.setUser(username);
            source.setPassword(password);
        }

        try {
            return source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
            return null;                // Dead code.
        }
    }

    // Auxiliary to connectToDatabase()
    // It changes user and password of the parameter object.

    private static void login(@NotNull PGSimpleDataSource dataSource) {
        keyboard.nextLine();

        System.out.println("Insert username: ");
        username = keyboard.nextLine();

        System.out.println("Insert password: ");
        password = keyboard.nextLine();

        dataSource.setUser(username);
        dataSource.setPassword(password);
    }


}

