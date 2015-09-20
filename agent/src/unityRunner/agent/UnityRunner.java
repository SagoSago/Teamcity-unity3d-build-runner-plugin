package unityRunner.agent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.Tailer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: clement.dagneau
 * Date: 13/12/2011
 * Time: 14:36
 */
public class UnityRunner {
    final UnityRunnerConfiguration configuration;
    private volatile boolean stop = false;
    private final LogParser logParser;
    private Thread runnerThread;

    UnityRunner(UnityRunnerConfiguration configuration, LogParser logParser) {
        this.configuration = configuration;
        this.logParser = logParser;
    }


    /**
     * @return executable name/path
     */
    @NotNull
    String getExecutable() {
        logMessage(String.format("Unity version requested: %s ", configuration.unityVersion));
        logMessage(String.format("Unity executable path: %s ", configuration.getUnityPath()));

        return configuration.getUnityPath();
    }

    /**
     * @return get arguments for executable
     */
    @NotNull
    List<String> getArgs() {
        List<String> args = new ArrayList<String>();

        if (configuration.batchMode)
            args.add("-batchmode");

        if (configuration.noGraphics)
            args.add("-nographics");

        if (configuration.quit)
            args.add("-quit");

        if (!configuration.buildPlayer.equals("")) {
            args.add(String.format("-%s", configuration.buildPlayer));
            args.add(String.format("%s", configuration.buildPath));
        } else {
            args.add(String.format("--build_path=%s", configuration.buildPath));
        }

        if (!configuration.projectPath.equals("")) {
            args.add("-projectPath");
            args.add(configuration.projectPath);
        }

        if (!configuration.executeMethod.equals("")) {
            args.add("-executeMethod");
            args.add(configuration.executeMethod);
        }

        if (configuration.createCleanedLog) {
            args.add("-cleanedLogFile");
            args.add(configuration.getCleanedLogPath());
        }

        if(!configuration.overrideLogPath.equals("")) {
            args.add("-logFile");
            args.add(configuration.overrideLogPath);
        }

        for(int i = 0; i < configuration.extraOpts.length; ++i)
        {
            args.add(configuration.extraOpts[i]);
        }

        return args;
    }


    /**
     * start the unity runner
     */
    public void start() {

        //logMessage("[Starting UnityRunner]");

        //runnerThread = new Thread(new Runnable() {
        //    @Override
        //    public void run() {
        //        tailLogFile();
        //    }
        //});
        //runnerThread.start();

        if (configuration.clearBefore) {
            clearBefore();
        }

        ensureOutputDirectoryExists();
    }

    /**
     * tail the log file during running
     */
    private void tailLogFile() {
        initialise();
        logMessage("[tailing log file: " + configuration.getLogPathToTail() + "]");

        File file = new File(configuration.getLogPathToTail());
        TailerListener listener = new TailerListener(this);
        Tailer tailer = Tailer.create(file, listener);

        while (!stop) {
            // sleep so we don't busy-wait
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        tailer.stop();
        logMessage("[log tail process end]");
    }

    /**
     * cat the log file instead of tailing it
     */
    private void catLogFile() {
        logMessage("[Catting log file]");
        if ( configuration.ignoreLogBefore) {
            logMessage("[Ignoring lines before text " + configuration.ignoreLogBeforeText + "]");
        }

        File file = new File(configuration.getLogPathToTail());

        // for each line
        try {
            LineIterator iterator = FileUtils.lineIterator(file);
            List<String> ignoredLines = new ArrayList<String>();
            boolean stillIgnoringLines = configuration.ignoreLogBefore;
            try {
                while (iterator.hasNext()) {
                    String line = iterator.nextLine();
                    if (stillIgnoringLines && line.contentEquals(configuration.ignoreLogBeforeText)){
                        stillIgnoringLines = false;
                    }

                    if (line.length() > 0) {
                        if ( stillIgnoringLines ) {
                            // add the message to the ignored group
                            ignoredLines.add(line);
                        } else {
                            // log the message
                            logMessage(line);
                        }
                    }
                }
                if (stillIgnoringLines) {
                    // we have finished processing the log and we've ignored everything
                    logMessage("[The configured text has not been found: "+configuration.ignoreLogBeforeText +"]");
                    // we better output all these lines
                    logMessages(ignoredLines);
                }
            } finally {
                iterator.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logMessages(List<String> lines) {
        for (String line : lines){
            logMessage(line);
        }
    }

    /**
     * stop the runner
     */
    public void stop() {
        //if(stop == false)
        //{
        //    return;
        //}

        catLogFile();

        //stop = true;
        //logMessage("[Stop UnityRunner]");

        // Wait for tailer to finish!
        //try {
        //    runnerThread.join();
        //} catch(InterruptedException e) {
        //    logMessage("Interrupted whilst waiting for log tailer thread to finish");
        //    logParser.logException(e);
        //}

        //logMessage("[Stop UnityRunner] - joined log tailer");
    }


    /**
     * cleanup after runner
     */
    public void optionallyCleanupAfter() {
        if (configuration.cleanAfter) {
            cleanAfter();
        }
    }


    private void initialise() {
        deleteLogFile(configuration.getLogPathToTail());
    }

    private void deleteLogFile(String path) {

        File logFile = new File(path);

        if (logFile.exists()) {
            logMessage("[delete old log file]");

            if (!logFile.delete()) {
                logMessage("[FAILED TO DELETE OLD LOG FILE]");
            }
        }
    }

    void logMessage(String message) {
        logParser.log(message);
    }

    /**
     * clear the output directory before running
     */
    private void clearBefore() {
        if(configuration.buildPath.equals("")) {
            logMessage("Output path is not set, or set to an empty string; not going to try deleting it.");
            return;
        }

        File outputDir = new File(configuration.buildPath);
        logMessage("Removing output path: " + outputDir.getPath());

        if (!outputDir.exists()) {
            logMessage("Output path doesn't exist; not going to try deleting it.");
            return;
        }

        if (outputDir.isDirectory()) {
            logMessage("Output path is a directory");
            // only delete directory if it is a directory!
            try {
                FileUtils.deleteDirectory(outputDir);
            } catch(IOException e) {
                logParser.logException(e);
            }
        } else if (outputDir.isFile()) {
            logMessage("Output path is a file");
            if(!outputDir.delete()) {
                logParser.log("Failed to delete file: " + outputDir.getPath());
            }
        }
    }

    /**
     * remove .svn and .meta files from the output directory after running
     */
    private void cleanAfter() {
        new OutputDirectoryCleaner(logParser).clean(new File(configuration.buildPath));
    }

    private void ensureOutputDirectoryExists() {
        if(configuration.buildPath.equals("")) {
            logMessage("Output build path is not set, or set to an empty string; not creating parent directory.");
            return;
        }

        File buildPath = new File(configuration.buildPath);
        File parent = buildPath.getParentFile();

        if(parent == null) {
            logMessage("Build output path doesn't have a parent; not creating parent directory.");
            return;
        }

        logMessage("Creating parent directory (" + parent.getPath() + ") for output path (" + buildPath.getPath() + ")");
        try {
            FileUtils.forceMkdir(parent);
            logMessage("Successfully created parent directory");
        } catch (IOException e) {
            logMessage("Failed to create parent directory");
            logParser.logException(e);
        }
    }
}
