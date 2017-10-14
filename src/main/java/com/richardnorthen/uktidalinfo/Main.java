package com.richardnorthen.uktidalinfo;

import com.richardnorthen.uktidalinfo.api.TideGauge;
import com.richardnorthen.uktidalinfo.json.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// TODO add new commands, search by latlon/postcode, dynamic display
public class Main {
    public static final String LIST_CMD = "list";
    public static final String SEARCH_OPT = "search";
    public static final String GET_CMD = "station";

    public static final String USAGE = String.format("Usage:"
                    + "\n  program %s [%s <id | name | area>]"
                    + "\n  program %s <id>",
            LIST_CMD, SEARCH_OPT, GET_CMD);

    public static void main(String[] args) {
        List<String> cmds = Arrays.asList(args);

//      System.out.print("program");
//      for (String c : cmds) System.out.print(" " + c);
//      System.out.println();

        if (cmds.indexOf(LIST_CMD) == 0) {
            String filter = null;
            if (cmds.indexOf(SEARCH_OPT) == 1) {
                if (cmds.size() > 2) {
                    filter = cmds.get(2).toLowerCase();
                }
            }
            listStations(filter);
        } else if (cmds.indexOf(GET_CMD) == 0 && cmds.size() > 1) {
            getStation(cmds.get(1));
        } else {
            System.out.println(USAGE);
        }
    }

    public static void listStations(String filter) {
        Stations stations;
        try {
            stations = TideGauge.listStations();
            // apply filter is needed (tighter looping is better than a single foreach loop)
            if (filter == null) {
                for (Stations.Item i : stations.items) {
                    System.out.print(i.getFullName());
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

    public static void getStation(String id) {
        try {
            Station station = TideGauge.getStation(id);
            DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateFormat dateDisplay = new SimpleDateFormat("MMM dd yyyy");
            Date startingDay = dateParser.parse(station.items[station.items.length-1].dateTime);
            // build the virtual screen
            String lines[] = {String.format("Station: %-31s%40s", id, dateDisplay.format(startingDay)), "",
                    " 10 ", "  8 ", "  6 ", "  4 ", "  2 ", "m 0 ", " -2 ", " -4 ", " -6 ", " -8 ", "-10 ",
                    "    "};
            dateDisplay = new SimpleDateFormat("HHmm");
            // setup time labels
            for (int i = station.items.length-1; i > 0; i = i-4) {
                Date time = dateParser.parse(station.items[i].dateTime);
                String timeLabel = dateDisplay.format(time);
                if ((i-3) % 8 == 0) {
                    lines[1] += "    " + timeLabel;
                } else {
                    lines[13] += "    " + timeLabel;
                }
            }
            // draw graph
            for (Station.Item i : station.items) {
                int v = (int) Math.round(i.value) / 2;
                for (int j = -5; j < 6; j++) {
                    if (v == j) {
                        lines[j+7] += "~";
                    } else {
                        lines[j+7] += " ";
                    }
                }
            }
            // print display
            for (String line : lines) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}