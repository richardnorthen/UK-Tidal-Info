package com.richardnorthen.uktidalinfo.json;

public class Station {
    public Item[] items;
    public Station() {}

    public class Item {
        public String dateTime;
        public double value;
        public Item() {}
    }
}