package com.puppetlabs.jenkins.plugins.puppetgatling;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppetlabs.jenkins.plugins.puppetgatling.chart.Graph;
import com.puppetlabs.jenkins.plugins.puppetgatling.chart.RawDataGraph;
import hudson.FilePath;
import hudson.model.Run;
import io.gatling.jenkins.chart.Point;
import io.gatling.jenkins.chart.Serie;
import io.gatling.jenkins.chart.SerieName;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimulationMetrics {

    private static final int MAX_MEMORY_DATA_POINTS_TO_DISPLAY = 40;
    private static final int BYTES_PER_MEGABYTE = (1024 * 1024);

    private final Graph<Long> memoryUsage;
//    private final ObjectMapper mapper;
    private final JsonFactory jsonFactory = new JsonFactory();

    public SimulationMetrics(Run<?, ?> run, PrintStream logger, FilePath workspace,
                             String simulationId) throws IOException, InterruptedException {
//        mapper = new ObjectMapper();
//        mapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);


        logger.println("Checking for existence of metrics.json.");
        FilePath metricsFilePath = workspace.child("puppet-gatling").
                child(simulationId).child("sut_archive_files").
                child("metrics.json");
        if (metricsFilePath.exists()) {
            logger.println("Found metrics.json, parsing.");
            List<Point<Integer, Long>> memoryData = new ArrayList<>();

            InputStream metricsInput = metricsFilePath.read();
            JsonParser jsonParser = jsonFactory.createParser(metricsInput);
            jsonParser.setCodec(new ObjectMapper());
            jsonParser.nextToken();

            int i = 0;
            while (jsonParser.hasCurrentToken()) {
                Map metricsEntry = jsonParser.readValueAs(Map.class);
                Long usedHeapValue = getValueFromNestedMap(metricsEntry,
                        new String[] {"status-service", "status", "experimental", "jvm-metrics", "heap-memory", "used"}
                );
//            firstEntry.get("status-service").get("status").get("experimental").get("jvm-metrics").get("heap-memory").get("used")
//                logger.println("Found heap value: " + usedHeapValue);
                i++;
                memoryData.add(new Point<Integer, Long>(i, usedHeapValue / BYTES_PER_MEGABYTE));
                jsonParser.nextToken();
//                    metricsEntry = mapper.readValue(metricsInput, Map.class);
            }

//                Map metricsEntry = mapper.readValue(metricsInput, Map.class);
//            while (metricsEntry != null) {
//                Long usedHeapValue = getValueFromNestedMap(metricsEntry,
//                        new String[] {"status-service", "status", "experimental", "jvm-metrics", "heap-memory", "used"}
//                );
////            firstEntry.get("status-service").get("status").get("experimental").get("jvm-metrics").get("heap-memory").get("used")
//                logger.println("Found heap value: " + usedHeapValue);
//                metricsEntry = mapper.readValue(metricsInput, Map.class);
//            }

            SerieName memSeriesName = new SerieName("memory");
//            for (int i = 0; i < 1000; i++) {
//                memoryData.add(new Point<Integer, Long>(i, (long) (2000 + (-100 + (Math.random() * 200)))));
//            }
            Map<SerieName, Serie<Integer, Long>> fakeData = new TreeMap<>();
            logger.println("Found " + memoryData.size() + " memory data points.");
            fakeData.put(memSeriesName, RawDataGraph.filterDataToSeries(memoryData, MAX_MEMORY_DATA_POINTS_TO_DISPLAY));

            memoryUsage = new RawDataGraph<Long>(fakeData);
        } else {
            logger.println("No metrics.json found; memory data will not be visible.");
            memoryUsage = null;
        }
    }

    private static Long getValueFromNestedMap(Map firstEntry, String[] strings) {
        Map next = firstEntry;
        for (int i = 0; i < strings.length - 1; i++) {
            String key = strings[i];
            System.out.println("GETTING NESTED VALUE FOR '" + key + "'");
            next = (Map) next.get(key);
        }
        System.out.println("GETTING FINAL VALUE FROM MAP '" + next + "', key will be: '" + strings[strings.length - 1] + "'");
        return (Long) next.get(strings[strings.length - 1]);
    }

    public Graph<Long> getMemoryUsage() {
        return memoryUsage;
    }
}
