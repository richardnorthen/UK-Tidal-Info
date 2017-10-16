package com.richardnorthen.uktidalinfo;

import com.richardnorthen.uktidalinfo.api.TideGauge;
import com.richardnorthen.uktidalinfo.json.Station;
import com.richardnorthen.uktidalinfo.json.Stations;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// TODO add new commands and options
public class Main {
    private static final String HELP_OPT = "--help";

    private static final String LIST_CMD = "list";
    private static final String SEARCH_OPT = "--search";

    private static final String GET_CMD = "get";
    private static final String GRAPH_SIZE_OPT = "--graph-size";
    private static final String[] GRAPH_SIZES = {"small", "medium", "large"};
    private static final String DEFAULT_GRAPH_SIZE = GRAPH_SIZES[1];
    private static final String HOURS_OPT = "--hours";
    private static final int DEFAULT_HOURS = 36;

    private static final String USAGE_MESSAGE = String.format("\nUsage:\n"
                    + "  Main %1$s\n"
                    + "  Main %2$s [%3$s <id|name|area>]\n"
                    + "  Main %4$s <id> [options]\n\n"
                    + "Options:\n"
                    + "  %5$s=<size> set the size to be small, medium, or large\n"
                    + "%7$21s [default: medium]\n"
                    + "  %6$s=<hours>     get data from the past 1-72 hours\n"
                    + "%7$21s [default: 36]\n\n"
                    + "Examples\n"
                    + "  Main %2$s %3$s=\"port\"\n"
                    + "  Main %4$s E70124\n"
                    + "  Main %4$s E70124 %5$s=large %6$s=24\n",
            HELP_OPT, LIST_CMD, SEARCH_OPT, GET_CMD, GRAPH_SIZE_OPT, HOURS_OPT, "");

    private static final String UNRECOGNIZED_ARG_ERROR = "Unrecognized argument '%s'!\n" + USAGE_MESSAGE;
    private static final String INVALID_COMMAND_ERROR = "Invalid command!\n" + USAGE_MESSAGE;

    private static final String GRAPH_SIZE_INVALID = "'%s' is not a valid graph size. Using default value '" + DEFAULT_GRAPH_SIZE + "'.\n";
    private static final String HOUR_NAN_INVALID = "'%s' is not a number. Using default value: '" + DEFAULT_HOURS + "'.\n";
    private static final String HOURS_INVALID = "%d hours is not within the specified range (1-72). Using default value '" + DEFAULT_HOURS + "'.\n";

    public static void main(String[] args) {
        List<String> cmds = Arrays.asList(args);
        if (cmds.indexOf(HELP_OPT) == 0) {
            System.out.printf(USAGE_MESSAGE);
        } else if (cmds.indexOf(LIST_CMD) == 0) {
            // set default values
            String filter = null;
            // check options
            for (int i = 1; i < cmds.size(); i++) {
                // splits --option or --option="some value" into 1 or 2 strings
                String[] option = cmds.get(i).split("=",2);
                if (option.length == 1) {
                    switch (option[0]) {
                        // future boolean options go here
                        default:
                            System.out.printf(UNRECOGNIZED_ARG_ERROR, option[0]);
                            System.exit(1);
                    }
                } else if (option.length == 2){
                    switch (option[0]) {
                        case SEARCH_OPT:
                            filter = option[1].replace("\"", "");
                            break;
                        default:
                            System.out.printf(UNRECOGNIZED_ARG_ERROR, option[0]);
                            System.exit(1);
                    }
                } else {
                    System.out.printf(INVALID_COMMAND_ERROR);
                    System.exit(1);
                }
            }
            listStations(filter);
        } else if (cmds.indexOf(GET_CMD) == 0) {
            String id = cmds.get(1);
            String graphSize = DEFAULT_GRAPH_SIZE;
            int hours = DEFAULT_HOURS;
            for (int i = 2; i < cmds.size(); i++) {
                String[] option = cmds.get(i).split("=",2);
                if (option.length == 1) {
                    switch (option[0]) {
                        // future boolean options go here
                        default:
                            System.out.printf(UNRECOGNIZED_ARG_ERROR, option[0]);
                            System.exit(1);
                    }
                } else if (option.length == 2){
                    switch (option[0]) {
                        case GRAPH_SIZE_OPT:
                            graphSize = option[1];
                            break;
                        case HOURS_OPT:
                            try {
                                hours = Integer.parseInt(option[1]);
                            } catch (NumberFormatException e) {
                                System.out.printf(HOUR_NAN_INVALID, option[1]);
                            }
                            break;
                        default:
                            System.out.printf(UNRECOGNIZED_ARG_ERROR, option[0]);
                            System.exit(1);
                    }
                } else {
                    System.out.printf(INVALID_COMMAND_ERROR);
                    System.exit(1);
                }
            }
            getStation(id, graphSize, hours);
        } else {
            System.out.printf(INVALID_COMMAND_ERROR);
            System.exit(1);
        }
    }

    private static void listStations(String filter) {
        try {
            Stations stations = TideGauge.listStations();
            // print title
            System.out.printf("%-20s[%-15s (Town, Region)\n", "Station Name", "Station ID]");
            // apply filter only if needed (tighter looping)
            if (filter == null) {
                for (Stations.Item i : stations.items) {
                    System.out.println(i.getFullName());
                }
            } else {
                for (Stations.Item i : stations.items) {
                    if (i.matchesFilter(filter)) {
                        System.out.println(i.getFullName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getStation(String id, String graphSize, int hours) {
        try {
            // validate inputs and replace with defaults if needed
            if (!Arrays.asList(GRAPH_SIZES).contains(graphSize)) {
                System.out.printf(GRAPH_SIZE_INVALID, graphSize);
                graphSize = DEFAULT_GRAPH_SIZE;
            }
            if (hours > 72 || hours < 1) {
                System.out.printf(HOURS_INVALID, hours);
                hours = DEFAULT_HOURS;
            }

            // get station data
            int readings = hours * 4;
            Station station = TideGauge.getStation(id, readings);

            // setup graph title
            DateFormat readingsDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateFormat headerDate = new SimpleDateFormat("HH:mm, MMM dd yyyy");
            Date startDate = readingsDate.parse(station.items[station.items.length-1].dateTime);
            String title = String.format("Station: %s | Starting date: %s\n", id, headerDate.format(startDate));
            // setup header and footer of graph
            DateFormat timestamp = new SimpleDateFormat("HHmm");
            StringBuilder header = new StringBuilder(readings);
            StringBuilder footer = new StringBuilder(readings);

            // iterate over all readings
            double maxValue = Double.MIN_VALUE, minValue = Double.MAX_VALUE;
            for (int i = 0; i < station.items.length; i++) {
                // calculate max and min tides
                double value = station.items[i].value;
                if (value > maxValue) {
                    maxValue = value;
                } else if (value < minValue) {
                    minValue = value;
                }
                // collect timestamps every hour
                Date time = readingsDate.parse(station.items[i].dateTime);
                if ((i % 8) == 0) {
                    header.insert(0,"    " + timestamp.format(time));
                } else if ((i % 8) == 4) {
                    footer.insert(0,"    " + timestamp.format(time));
                }
            }

            // setup graph according to specified size
            int max = (int) Math.ceil(maxValue);
            int min = (int) Math.floor(minValue);
            int steps;
            if (graphSize.equals(GRAPH_SIZES[0])) {
                // round to even numbers
                if ((max % 2) != 0) {
                    max++;
                }
                if ((min % 2) != 0) {
                    min--;
                }
                steps = (max - min) / 2;
            } else if (graphSize.equals(GRAPH_SIZES[2])) {
                steps = (max - min) * 2;
            } else { // "medium"
                steps = max - min;
            }
            steps++; // include the 0 label on all axes

            // set the graph labels (note: it's upside-down)
            String[] graph = new String[steps];
            if (graphSize.equals(GRAPH_SIZES[0])) {
                // only even numbers
                for (int i = 0; i < graph.length; i++) {
                    graph[i] = String.format("%+3.1f", (double) min + (i * 2));
                }
            } else if (graphSize.equals(GRAPH_SIZES[2])) {
                // 0.5 increments
                for (int i = 0; i < graph.length; i++) {
                    graph[i] = String.format("%+3.1f", (double) min + ((double) i / 2));
                }
            } else { // "medium"
                // all integers
                for (int i = 0; i < graph.length; i++) {
                    graph[i] = String.format("%+3.1f", (double) min + i);
                }
            }

            // populate the graph with data
            for (int i = station.items.length-1; i > -1; i--) {
                double value = station.items[i].value;
                if (graphSize.equals(GRAPH_SIZES[0])) {
                    value = Math.round(value / 2) - (min/2);
                    for (int j = 0; j < graph.length; j++) {
                        if (value == j) {
                            graph[j] += "~";
                        } else {
                            graph[j] += " ";
                        }
                    }
                } else if (graphSize.equals(GRAPH_SIZES[2])) {
                    value = Math.round(value * 2) - (min*2);
                    for (int j = 0; j < graph.length; j++) {
                        if (value == j) {
                            graph[j] += "~";
                        } else {
                            graph[j] += " ";
                        }
                    }
                } else { // "medium"
                    value = Math.round(value) - min;
                    for (int j = 0; j < graph.length; j++) {
                        if (value == j) {
                            graph[j] += "~";
                        } else {
                            graph[j] += " ";
                        }
                    }
                }
            }

            // print the graph
            System.out.printf(title);
            // adjust spacing depending on start time
            if ((hours % 2) == 0) {
                header.insert(0, "    ");
            } else {
                footer.insert(0, "    ");
            }
            System.out.println(header);
            // graph was upside-down
            for (int i = graph.length-1; i > -1; i--) {
                System.out.println(graph[i]);
            }
            System.out.println(footer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}