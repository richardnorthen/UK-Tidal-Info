package com.richardnorthen.uktidalinfo.api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.richardnorthen.uktidalinfo.json.Station;
import com.richardnorthen.uktidalinfo.json.Stations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

// TODO improve Exception handling, custom date ranges, specific unit types, etc.
public class TideGauge {
    private static final String LIST_STATIONS_URL = "https://environment.data.gov.uk/flood-monitoring/id/stations?type=TideGauge";
    private static final String GET_STATION_URL = "https://environment.data.gov.uk/flood-monitoring/id/stations/%s/readings?_sorted&_limit=%d";

    private static final int DEFAULT_TIMEOUT = 5000;

    private static final String NO_RESPONSE_ERROR = "No response from API server";
    private static final String INVALID_RESPONSE_ERROR = "Unexpected response from API server";
    private static final String INVALID_STATION_ID_ERROR = "Invalid station ID format";

    private static final String STATION_ID_FORMAT = "^E7\\d{4}(?:-anglian)?$";

    /**
     * Retrieves a list of monitoring stations currently available.
     * <p>
     * A custom {@link Stations} object is used to represent the data returned.
     *
     * @return a {@link Stations} object with a list of monitoring stations
     * @throws Exception with a specific error message
     * @see Stations
     */
    public static Stations listStations() throws Exception {
        // the API's ?search=x filter is case-sensitive, so we use our own
        String response = queryAPI(LIST_STATIONS_URL);
        if (response == null) {
            throw new Exception(NO_RESPONSE_ERROR);
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(response, Stations.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new Exception(INVALID_RESPONSE_ERROR);
        }
    }

    /**
     * Retrieves the details of a specified monitoring station. The stationId
     * argument specifies the station and is of the form E7XXXX.
     * <p>
     * A custom {@link Station} object is used to represent the data returned.
     *
     * @param stationId the ID of a monitoring station
     * @return a {@link Station} object with data from a station
     * @throws Exception with a specific error message
     * @see Station
     */
    public static Station getStation(String stationId, int readings) throws Exception {
        // check station ID format
        if (!Pattern.compile(STATION_ID_FORMAT).matcher(stationId).matches()) {
            throw new Exception(INVALID_STATION_ID_ERROR);
        }
        // build custom url for query
        String response = queryAPI(String.format(GET_STATION_URL, stationId, readings));
        if (response == null) {
            throw new Exception(NO_RESPONSE_ERROR);
        }
        try {
            Gson gson = new Gson();
            return gson.fromJson(response, Station.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            throw new Exception(INVALID_RESPONSE_ERROR);
        }
    }

    /**
     * Queries a specified Tide Gauge API. A {@link URL} object is built using
     * the provided url argument.
     * <p>
     * The response retrieved by the API is returned as a JSON-formatted string,
     * or <code>null</code> if there was an error.
     *
     * @param url the address of the API
     * @return the response as a JSON string, or <code>null</code>
     */
    private static String queryAPI(String url) {
        BufferedReader responseBuffer = null;
        StringBuilder responseBuilder;
        try {
            // setup the HTTP GET request
            URL api = new URL(url);
            HttpURLConnection request = (HttpURLConnection) api.openConnection();
            request.setRequestProperty("Content-Type", "application/json");
            request.setRequestMethod("GET");
            request.setReadTimeout(DEFAULT_TIMEOUT);
            request.connect();
            // get the response
            responseBuffer = new BufferedReader(new InputStreamReader(request.getInputStream()));
            responseBuilder = new StringBuilder();
            String line;
            while ((line = responseBuffer.readLine()) != null) {
                responseBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            responseBuilder = null;
        } finally {
            // ensure that the buffer is closed
            if (responseBuffer != null) {
                try {
                    responseBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    responseBuilder = null;
                }
            }
        }
        // return a response if there were no exceptions, otherwise null
        return responseBuilder != null ? responseBuilder.toString() : null;
    }
}