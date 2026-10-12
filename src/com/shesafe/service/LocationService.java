package com.shesafe.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LocationService
 * Handles emergency geolocation detection, formatting, and Google Maps link generation.
 *
 * NOTE ON DESKTOP JAVA GPS LIMITATIONS:
 * Desktop personal computers (Windows/macOS/Linux) typically lack hardware GPS chips
 * and cellular base-station triangulation. Standard Java SE does not provide a native
 * satellite GPS provider. Therefore, this service provides:
 * 1. IP-based Geolocation estimation (city, region, lat/lon via public lookup).
 * 2. Manual location confirmation or override for precise local landmarks.
 * 3. Clearly tags whether the coordinates are IP-estimated or manually entered.
 * 4. Strictly one-shot on-demand execution (NO continuous background tracking).
 */
public class LocationService {

    public static class LocationData {
        public final String displayLocation;
        public final String locationType; // 'IP_ESTIMATED', 'MANUAL_ENTRY', 'NONE'
        public final String mapLink;
        public final boolean isValid;

        public LocationData(String displayLocation, String locationType, String mapLink, boolean isValid) {
            this.displayLocation = displayLocation;
            this.locationType = locationType;
            this.mapLink = mapLink;
            this.isValid = isValid;
        }

        public static LocationData none() {
            return new LocationData("Location sharing disabled by user", "NONE", null, false);
        }
    }

    /**
     * Attempts a quick IP-based geolocation lookup with a short timeout.
     * Falls back gracefully if offline.
     */
    public LocationData detectEstimatedLocation() {
        try {
            // Use public, lightweight IP geolocation API with 2500ms timeout
            URL url = new URL("http://ip-api.com/json/?fields=status,message,country,regionName,city,lat,lon");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2500);
            conn.setReadTimeout(2500);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();

                String json = sb.toString();
                if (json.contains("\"status\":\"success\"")) {
                    String city = extractJsonField(json, "city");
                    String region = extractJsonField(json, "regionName");
                    String country = extractJsonField(json, "country");
                    String lat = extractJsonField(json, "lat");
                    String lon = extractJsonField(json, "lon");

                    String display = city + ", " + region + ", " + country + " [IP Estimated]";
                    String mapLink = "https://maps.google.com/?q=" + lat + "," + lon;

                    return new LocationData(display, "IP_ESTIMATED", mapLink, true);
                }
            }
        } catch (Exception ex) {
            System.out.println("[LocationService] IP Geolocation lookup bypassed/offline: " + ex.getMessage());
        }

        // Default fallback if offline or lookup fails
        return new LocationData("Approximate Local Area (Offline)", "IP_ESTIMATED", "https://maps.google.com", false);
    }

    /**
     * Generates a location object from a manually specified landmark or address.
     */
    public LocationData createManualLocation(String addressOrLandmark) {
        if (addressOrLandmark == null || addressOrLandmark.trim().isEmpty()) {
            return LocationData.none();
        }

        String clean = addressOrLandmark.trim();
        String display = clean + " [Manual Entry]";
        String mapLink;
        try {
            mapLink = "https://maps.google.com/?q=" + URLEncoder.encode(clean, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            mapLink = "https://maps.google.com/?q=" + clean.replace(" ", "+");
        }

        return new LocationData(display, "MANUAL_ENTRY", mapLink, true);
    }

    private String extractJsonField(String json, String field) {
        try {
            String target = "\"" + field + "\":";
            int idx = json.indexOf(target);
            if (idx == -1) return "";
            int start = idx + target.length();
            if (json.charAt(start) == '"') {
                int end = json.indexOf('"', start + 1);
                return json.substring(start + 1, end);
            } else {
                int end = json.indexOf(',', start);
                if (end == -1) end = json.indexOf('}', start);
                return json.substring(start, end).trim();
            }
        } catch (Exception e) {
            return "";
        }
    }
}
