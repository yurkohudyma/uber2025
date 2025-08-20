package ua.hudyma.rideservice.util;

import ua.hudyma.rideservice.dto.RouteDto;

public class DistanceCalculator {
    private DistanceCalculator() {
    }

    private static final double EARTH_RADIUS_KM = 6371;

    public static double haversine(RouteDto dto) {
        var lat1 = dto.departure().latitude();
        var lon1 = dto.departure().longitude();
        var lat2 = dto.destination().latitude();
        var lon2 = dto.destination().longitude();
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
