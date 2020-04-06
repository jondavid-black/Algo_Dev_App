package org.devopsfordefense.linerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class Simulation {

    private final String dataFile;
    private final String outFile;
    private final Map<Double, Double> simData; 

    public Simulation(final String dataFilePath, final String outputFilePath) {
        dataFile = dataFilePath;
        outFile = outputFilePath;
        simData = new TreeMap<Double, Double>();
    }

    private void readSimData() {

        String line = "";
        final String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {

            boolean isFirst = true;

            while ((line = br.readLine()) != null) {

                // skip the first line
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                // use comma as separator
                final String[] data = line.split(cvsSplitBy);
                final double timeSec = Double.parseDouble(data[0]);
                final double accel = Double.parseDouble(data[1]);

                simData.put(timeSec, accel);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    private void run() {

        final LineRunner runner = new LineRunner();

        // setup output
        PrintStream log = null;

        try {
            final File logFile = new File(outFile + "/output.txt");
            System.out.println("Created output file: " + logFile.getAbsolutePath());
            log = new PrintStream(logFile);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to open arg output file.", e);
        }

        log.println("time, position, velocity");

        for (final Entry<Double, Double> data : simData.entrySet()) {
            final double t = data.getKey();
            final double a = data.getValue();

            runner.step(t, a);

            log.println(runner.getTimeSec() + ", " + runner.getPosition() + ", " + runner.getVelocity());

        }

        log.flush();
        log.close();

    }

    public static void main(final String[] args) {

        final long start = System.currentTimeMillis();

        System.out.println("Running LineRunner Simulation");

        if (args.length != 2) {
            throw new RuntimeException(
                    "Invalid arguments. args.length=" + args.length + " args[0]=" + args[0] + " args[1]=" + args[1]);
        }

        final File in = new File(args[0]);
        if (!in.exists() || !in.isFile()) {
            throw new RuntimeException("Arg 1 isn't a file.");
        }

        final File out = new File(args[1]);
        if (!out.exists() || !out.isDirectory()) {
            throw new RuntimeException("Arg 2 isn't a directory.");
        }

        // read inputs
        final Simulation sim = new Simulation(in.getAbsolutePath(), out.getAbsolutePath());
        sim.readSimData();

        // run the simulation
        sim.run();

        final long end = System.currentTimeMillis();
        System.out.println("Runtime = " + (end - start) + " ms");

        System.out.println("Finished LineRunner Simulation");

    }
}