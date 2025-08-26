package ua.hudyma.geolocationservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.routing.ev.VehicleAccess;
import com.graphhopper.routing.util.DefaultSnapFilter;
import com.graphhopper.storage.index.Snap;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;
import com.graphhopper.util.details.PathDetail;
import com.graphhopper.util.shapes.GHPoint3D;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.hudyma.geolocationservice.dto.RouteDto;
import ua.hudyma.geolocationservice.dto.RoutePoint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.MULTIPLY;

@Service
@Log4j2
public class GraphHopperService {

    @Value("${graphhopper.api.key}")
    private String key;

    private static final int roadVicinity = 20;

    private final GraphHopper hopper;

    public GraphHopperService() {
        hopper = new GraphHopper();
        hopper.setOSMFile("geolocation-service/gh/ukraine-latest.osm.pbf");
        hopper.setGraphHopperLocation("geolocation-service/gh/graphFolderPath");
        var carProfile = new Profile("car")
                .setVehicle("car")
                .setWeighting("custom");
        hopper.setProfiles(carProfile);
        hopper.importOrLoad();
    }

    public boolean isPointAtRoad (RoutePoint routePoint){
        var em = hopper.getEncodingManager();
        var carAccess = em.getBooleanEncodedValue(VehicleAccess.key("car"));
        var snap = hopper
                .getLocationIndex()
                .findClosest(routePoint.latitude(),
                        routePoint.longitude(),
                        DefaultSnapFilter.ALL_EDGES);
        log.info("Snap valid: {}",snap.isValid());
        log.info("Query distance: {}", snap.getQueryDistance());
        if (snap.isValid()) {
            int edgeId = snap.getClosestEdge().getEdge();
            var edge = hopper.getBaseGraph().getEdgeIteratorState(edgeId, Integer.MIN_VALUE);
            log.info("Edge ID: " + edgeId);
            log.info("Car access: {}",edge.get(carAccess));
            GHPoint3D snappedPoint = snap.getSnappedPoint();
            log.info("Snapped to: {}, {}", snappedPoint.getLat(), snappedPoint.getLon());
        }
        return snap.isValid() && snap.getQueryDistance() < routePoint.vicinity();
    }

    @Deprecated
    public boolean isPointAccessibleForVehicle(RouteDto routeDto) {
        var customModel = new CustomModel();
        customModel.addToPriority(
                If("road_access == NO || road_access == PRIVATE", MULTIPLY, "0.0")
        );
        var request = new GHRequest(
                routeDto.departure().latitude(),
                routeDto.departure().longitude(),
                routeDto.destination().latitude(),
                routeDto.destination().longitude()
        )
                .setProfile("car")
                .setCustomModel(customModel)
                .putHint("ch.disable", true)
                .putHint("pass_through", true)
                .putHint("snap_preventions",
                        List.of("ferry", "tunnel", "railway"))
                .setPathDetails(List.of("road_access"));
        var response = hopper.route(request);
        if (response.hasErrors()) {
            log.warn("Routing error: {}", response.getErrors());
            return false;
        }
        var details = response
                .getBest()
                .getPathDetails()
                .get("road_access");
        if (details != null) {
            return details.stream()
                    .map(detail -> detail.getValue().toString())
                    .noneMatch(val -> val.equals("NO") || val.equals("PRIVATE"));
        }
        return true;
    }

    @Deprecated
    public StringBuilder isRoutePassableAPI(RouteDto routeDto) {
        try {
            double fromLon = routeDto.departure().longitude();
            double fromLat = routeDto.departure().latitude();
            double toLon = routeDto.destination().longitude();
            double toLat = routeDto.destination().latitude();

            var urlStr = String.format(
                    Locale.US,
                    "https://graphhopper.com/api/1/route?point=%f,%f&point=%f,%f&vehicle=car&instructions=false&calc_points=false&key=%s",
                    fromLat, fromLon, toLat, toLon, key);
            log.info("Generated URL: {}", urlStr);
            URL url = new URL(urlStr);
            var conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                System.err.println("HTTP error code: " + responseCode);
                return new StringBuilder("error");
            }
            var in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            var content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            var mapper = new ObjectMapper();
            var root = mapper.readTree(content.toString());
            boolean passable = root.has("paths") &&
                    root.get("paths").isArray() && root.get("paths").size() > 0;
            if (root instanceof ObjectNode) {
                ((ObjectNode) root).put("passable", passable);
            }
            var prettyJson = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);
            content.setLength(0);
            content.append(prettyJson);
            log.info("Response with passable: {}", content);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return new StringBuilder("error");
        }
    }
}
