package org.devopsfordefense.linerunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
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
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date());
            final File logFile = new File(outFile + "/output_" + timeStamp + ".txt");
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

            log.println(
                    "Step result: " + runner.getTimeSec() + ", " + runner.getPosition() + ", " + runner.getVelocity());
            
            // create some high CPU utilization to test load monitoring
            double val = 0.0;
            for (int r=1; r<150000000; r++) {
                double circumfrence = 2 * Math.PI * r;
                double area = Math.PI * r * r;
                val += area/circumfrence;
            }
            System.out.println("Completed Simulation Step: t=" + t + " a=" + a + " cpu_pusher=" + val);

        }

        log.flush();
        log.close();

    }

    public static void main(final String[] args) {

        final long start = System.currentTimeMillis();

        System.out.println("Running LineRunner Simulation");

        if (args.length != 1) {
            throw new RuntimeException(
                    "Invalid arguments. Should have 1 config properties file arg. args.length=" + args.length);
        }

        final File configFile = new File(args[0]);
        if (!configFile.exists() || !configFile.isFile()) {
            throw new RuntimeException("Arg 1 isn't a file. [" + args[0] + "]");
        }

        // read configuration
        Properties config = new Properties();
        try {
            config.load(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Configuration File not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read configuration file.", e);
        }

        final File in = new File(config.getProperty("scenario-data"));
        if (!in.exists() || !in.isFile()) {
            throw new RuntimeException("scenario-data config value isn't a file. [" + config.getProperty("scenario-data") + "]");
        }

        final File out = new File(config.getProperty("output-dir"));
        if (!out.exists() || !out.isDirectory()) {
            if (!out.mkdir()) {
                throw new RuntimeException("output-dir config value isn't a directory and couldn't be created [" + config.getProperty("output-dir") + "].");
            }   
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