package ua.hudyma.service;

import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Log4j2
public class GpxWriter {

    private static final String filePath = "S:\\TOOLS\\.Hudyma_projects\\track.gpx";

    public static void writeGpxFile(List<double[]> coordinates) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<gpx version=\"1.1\" creator=\"YourApp\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
            writer.write("  <trk>\n");
            writer.write("    <name>Generated Track</name>\n");
            writer.write("    <trkseg>\n");

            for (double[] coord : coordinates) {
                double lat = coord[0];
                double lon = coord[1];
                writer.write(String.format("      <trkpt lat=\"%f\" lon=\"%f\" />\n", lat, lon));
            }

            writer.write("    </trkseg>\n");
            writer.write("  </trk>\n");
            writer.write("</gpx>\n");

            log.info("✅ GPX-файл успішно записано: " + filePath);

        } catch (IOException e) {
            log.error("❌ Помилка при записі GPX-файлу: " + e.getMessage());
        }
    }

}

