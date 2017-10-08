package com.richardnorthen.uktidalinfo.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.richardnorthen.uktidalinfo.json.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO cleanup exception handling, add new methods
public class TideGauge {
    private static final String LIST_STATIONS_URL = "https://environment.data.gov.uk/flood-monitoring/id/stations?type=TideGauge";
    private static final String GET_STATION_URL = "https://environment.data.gov.uk/flood-monitoring/id/stations/%s/readings?_sorted&_limit=76";

    private static final int DEFAULT_TIMEOUT = 5000;

    public static Stations listStations() throws Exception {
        // ?search=x is case-sensitive!
        String response = queryAPI(LIST_STATIONS_URL);
        if (response != null) {
            try {
                Gson gson = new Gson();
                return gson.fromJson(response, Stations.class);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                throw new Exception(e);
            }
        }
        throw new Exception("No response");
    }

    public static Station getStation(String id) throws Exception {
        Pattern p = Pattern.compile("^E7\\d{4}(?:-anglian)?");
        Matcher m = p.matcher(id);

        if (m.matches()) {
            String response = queryAPI(String.format(GET_STATION_URL, id));
            if (response != null) {
                try {
                    Gson gson = new Gson();
                    return gson.fromJson(response, Station.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    throw new Exception(e);
                }
            }
            throw new Exception("No response");
        }
        throw new Exception("Invalid station ID format");
    }

    private static String queryAPI(String url) {
        BufferedReader responseBuffer = null;
        try {
            URL api = new URL(url);
            HttpURLConnection request = (HttpURLConnection) api.openConnection();
            request.setRequestProperty("Content-Type", "application/json");
            request.setRequestMethod("GET");
            request.setReadTimeout(DEFAULT_TIMEOUT);
            request.connect();

            responseBuffer = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = responseBuffer.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (responseBuffer != null) {
                try {
                    responseBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}