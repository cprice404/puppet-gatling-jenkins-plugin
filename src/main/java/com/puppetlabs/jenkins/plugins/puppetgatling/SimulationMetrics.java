package com.puppetlabs.jenkins.plugins.puppetgatling;

import com.puppetlabs.jenkins.plugins.puppetgatling.chart.Graph;
import com.puppetlabs.jenkins.plugins.puppetgatling.chart.RawDataGraph;
import hudson.FilePath;
import hudson.model.Run;
import io.gatling.jenkins.chart.Point;
import io.gatling.jenkins.chart.Serie;
import io.gatling.jenkins.chart.SerieName;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SimulationMetrics {

    private static final int MAX_MEMORY_DATA_POINTS_TO_DISPLAY = 40;

    private final Graph<Long> memoryUsage;

    public SimulationMetrics(Run<?, ?> run, PrintStream logger, FilePath workspace,
                             String simulationId) throws IOException, InterruptedException {
        logger.println("Checking for existence of metrics.json.");
        FilePath metricsFilePath = workspace.child("puppet-gatling").
                child(simulationId).child("sut_archive_files").
                child("metrics.json");
        if (metricsFilePath.exists()) {
            logger.println("Found metrics.json, parsing.");
            List<Point<Integer, Long>> memoryData = new ArrayList<>();
            SerieName memSeriesName = new SerieName("memory");
            for (int i = 0; i < 1000; i++) {
                memoryData.add(new Point<Integer, Long>(i, (long) (2000 + (-100 + (Math.random() * 200)))));
            }
            Map<SerieName, Serie<Integer, Long>> fakeData = new TreeMap<>();
            fakeData.put(memSeriesName, RawDataGraph.filterDataToSeries(memoryData, MAX_MEMORY_DATA_POINTS_TO_DISPLAY));

            memoryUsage = new RawDataGraph<Long>(fakeData);
        } else {
            logger.println("No metrics.json found; memory data will not be visible.");
            memoryUsage = null;
        }
    }

    public Graph<Long> getMemoryUsage() {
        return memoryUsage;
    }
}
