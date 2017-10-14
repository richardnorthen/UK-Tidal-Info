package com.richardnorthen.uktidalinfo.json;

public class Stations {
    public Item[] items;
    public Stations() {}

    public class Item {
        public String catchmentName;
        public String label;
        public String notation;
        public String town;
        public Item() {}

        public String getFullName() {
            // stations will always have a label and notation
            String name = String.format("%-20s[%-15s", label, notation + "]");
            if (town != null) {
                name += " (" + town;
                if (catchmentName != null) {
                    // remove 'England - '
                    name += ", " + catchmentName.substring(10, catchmentName.length());
                }
                name += ")";
            } else if (catchmentName != null) {
                name += "(" + catchmentName.substring(10, catchmentName.length()) + ")";
            }
            return name;
        }

        public boolean matchesFilter(String filter) {
            boolean result = false;
            // check every field for a match
            if ((catchmentName != null && catchmentName.toLowerCase().contains(filter))
                || (label != null && label.toLowerCase().contains(filter))
                || (notation != null && notation.toLowerCase().contains(filter))
                || (town != null && town.toLowerCase().contains(filter))) {
                result = true;
            }
            return result;
        }
    }
}