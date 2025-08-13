package ua.hudyma.service;

import ua.hudyma.dto.RouteRandomPoint;

import java.util.concurrent.ThreadLocalRandom;

public class LocationRandomizer {

    static final double EARTH_RADIUS = 6378137;
    static final double DEGREE_TO_RAD = Math.PI / 180.0;
    static final double METERS_PER_DEGREE_LAT = 111_320.0;

    public static RouteRandomPoint randomizeLocation(RouteRandomPoint routePoint) {
        double cityRadius = Math.sqrt(routePoint.citySquare() / Math.PI) * 1000.0;
        double[] offset = generateRandomOffsetInCircle(cityRadius);
        double dx = offset[0];
        double dy = offset[1];
        double deltaLat = dy / METERS_PER_DEGREE_LAT;
        double deltaLon = dx / metersPerDegreeLon(routePoint.latitude());
        double newLat = routePoint.latitude() + deltaLat;
        double newLon = routePoint.longitude() + deltaLon;
        return new RouteRandomPoint(newLat, newLon, routePoint.citySquare(), cityRadius/1000);
    }

    private static double[] generateRandomOffsetInCircle(double radiusMeters) {
        double t = 2 * Math.PI * getaRandom();
        double u = getaRandom() + getaRandom();
        double r = (u > 1) ? 2 - u : u;
        r *= radiusMeters;
        return new double[]{r * Math.cos(t), r * Math.sin(t)};
    }

    private static double getaRandom() {
        return ThreadLocalRandom.current().nextDouble();
    }

    private static double metersPerDegreeLon(double latitudeDegrees) {
        return EARTH_RADIUS * Math.cos(latitudeDegrees * DEGREE_TO_RAD) * DEGREE_TO_RAD;
    }
}

