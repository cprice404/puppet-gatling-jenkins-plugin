package com.puppetlabs.jenkins.plugins.puppetgatling;

import com.puppetlabs.jenkins.plugins.puppetgatling.chart.Graph;
import com.puppetlabs.jenkins.plugins.puppetgatling.chart.RawDataGraph;
import hudson.FilePath;
import hudson.model.Run;
import io.gatling.jenkins.chart.Point;
import io.gatling.jenkins.chart.Serie;
import io.gatling.jenkins.chart.SerieName;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PuppetGatlingBuildMetrics {

    private static final int MAX_MEMORY_DATA_POINTS_TO_DISPLAY = 40;

    public PuppetGatlingBuildMetrics(Run<?, ?> run, PrintStream logger, FilePath workspace) {
        logger.println("Checking for existence of metrics.json.");
    }

    public Graph<Long> getMemoryUsage() {
        if (true) {
            List<Point<Integer, Long>> memoryData = new ArrayList<>();
            SerieName memSeriesName = new SerieName("memory");
            for (int i = 0; i < 1000; i++) {
                memoryData.add(new Point<Integer, Long>(i, (long) (2000 + (-100 + (Math.random() * 200)))));
            }
            Map<SerieName, Serie<Integer, Long>> fakeData = new TreeMap<>();
            fakeData.put(memSeriesName, RawDataGraph.filterDataToSeries(memoryData, MAX_MEMORY_DATA_POINTS_TO_DISPLAY));

            return new RawDataGraph<Long>(fakeData);
        } else {
            return null;
        }
    }
}
