package com.logistique.gestiontournees.util;

public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0;

    private DistanceCalculator() {
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lont1 = Math.toRadians(lon1);
        double lont2 = Math.toRadians(lon2);

        double dlat = lat1Rad - lat2Rad;
        double dlon = lont1 - lont2;

        double a = Math.pow(Math.sin(dlat / 2), 2)+
                   Math.cos(lat1Rad) * Math.cos(lat2Rad)*
                           Math.pow(Math.sin(dlon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance finale
        return EARTH_RADIUS_KM * c;
    }
}

