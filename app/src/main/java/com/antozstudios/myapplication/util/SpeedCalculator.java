package com.antozstudios.myapplication.util;

public class SpeedCalculator {
    // Radius of the Earth in kilometers
    private static final double EARTH_RADIUS = 6371; // in kilometers

    // Method to calculate distance between two points using Haversine formula
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Calculate the distance
        return EARTH_RADIUS * c;
    }

    // Method to calculate speed based on distance and time
    public static double calculateSpeed(double lat1, double lon1, double lat2, double lon2, double timeInHours) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return (distance / timeInHours); // Speed = Distance / Time
    }

    public static void main(String[] args) {
        // Example usage
        double lat1 = 52.52; // Latitude of point 1
        double lon1 = 13.405; // Longitude of point 1
        double lat2 = 52.53; // Latitude of point 2
        double lon2 = 13.415; // Longitude of point 2
        double timeInHours = 1.5; // Time taken to travel from point 1 to point 2 in hours


        double speed = calculateSpeed(lat1, lon1, lat2, lon2, timeInHours);
        System.out.println("The speed is: " + speed + " km/h");
    }
}
