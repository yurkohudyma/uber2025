package ua.hudyma.graphhopper;

import com.graphhopper.GraphHopper;
import com.graphhopper.util.shapes.GHPoint;
import com.graphhopper.routing.util.EdgeFilter;
import com.mysql.cj.QueryResult;

/*public class GraphHopperUtils {

    public static boolean isCarAccessible(GraphHopper hopper, double lat, double lon) {
        GHPoint point = new GHPoint(lat, lon);

        // Фільтр: перевірити тільки доступні для "car" дороги
        EdgeFilter edgeFilter = hopper.createEdgeFilter("car");

        // Знайти найближчу точку в графі, яка відповідає фільтру
        QueryResult result = hopper.getLocationIndex().findClosest(lat, lon, edgeFilter);

        return result.isValid();  // true, якщо така точка знайдена
    }
}*/

