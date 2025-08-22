package ua.hudyma.geolocationservice.graphhopper;

/*public class GraphHopperApp {

    public static boolean isPointPassable(GraphHopper hopper, double lat, double lon) {
        LocationIndex locationIndex = hopper.getLocationIndex();
        FlagEncoder carEncoder = hopper.getEncodingManager().getEncoder("car");

        // Знайти найближче ребро до координат
        Snap snap = locationIndex.findClosest(lat, lon, carEncoder.getAccessEnc());

        if (!snap.isValid()) {
            return false;  // Не знайдено підходяче ребро
        }

        // Отримати ребро дороги
        EdgeIteratorState edge = snap.getClosestEdge();

        // Перевірити, чи є доступ для авто (carEncoder)
        return edge.getFlags() != 0 && carEncoder.getAccess(edge.getFlags());
    }

    public static void main(String[] args) {
        GraphHopper hopper = new GraphHopper()
                .setOSMFile("your-osm-file.osm.pbf")
                .setGraphHopperLocation("graph-folder")
                .importOrLoad();

        double lat = 48.858844;  // приклад
        double lon = 2.294351;

        boolean passable = isPointPassable(hopper, lat, lon);
        System.out.println("Is point passable for car? " + passable);
    }
}*/
