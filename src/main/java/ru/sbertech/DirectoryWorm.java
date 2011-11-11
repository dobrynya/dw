package ru.sbertech;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import static java.lang.String.format;

/**
 * Реализация сканнера.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 23:03
 */
public class DirectoryWorm implements Scanner {
    private OutputStream output;
    private ParameterParser[] parsers;
    private List<File> roots = new LinkedList<File>();
    private CompositeFilter fileFilter = new CompositeFilter();
    private CompositeFilter directoryFilter = new CompositeFilter();
    // создать пул потоков по количеству процессоров/ядер
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private boolean started;
    private Set<Appender> workingAppenders = new CopyOnWriteArraySet<Appender>();
    private SortedSet<Appender> closedAppenders = new ConcurrentSkipListSet<Appender>();
    private Timer timer = new Timer(true);

    public DirectoryWorm(OutputStream stream, ParameterParser... parsers) {
        this.output = stream;
        this.parsers = parsers;
    }

    public Scanner configure(String[] parameters) {
        for (String parameter : parameters) if (!parseParameter(parameter))
            throw new IllegalArgumentException(format("Could not parse parameter: %s!", parameter));
        return this;
    }

    protected boolean parseParameter(String parameter) {
        for (ParameterParser parser : parsers) if (parser.parse(parameter, this)) return true;
        return false;
    }

    public void directory(File directory) {
        if (started) scanDirectory(directory); else roots.add(directory); // отложенный старт
    }

    protected void scanDirectory(File directory) {
        Appender appender;
        try {
            if (directoryFilter.accept(directory)) {
                workingAppenders.add(appender = new FileAppender(directory, fileFilter, this));
                executor.submit(new DirectoryBrowser(directory, this, appender));
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not start directory browser!", e);
        }
    }

    public void closed(Appender appender) {
        workingAppenders.remove(appender);
        closedAppenders.add(appender);
        if (workingAppenders.isEmpty()) finish();
    }

    public void directoryFilter(FileFilter filter) { directoryFilter.addFilter(filter); }

    public void fileFilter(FileFilter filter) { fileFilter.addFilter(filter); }

    public void scan() {
        started = true;
        for (File root : roots) scanDirectory(root);
        timer.schedule(new Indicator(), 0, 1000);
    }

    protected void finish() {
        try { for (Appender a : closedAppenders) a.dump(output); }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            try { output.close(); } catch (IOException ignored) {}
            executor.shutdown();
            timer.cancel();
            System.out.println("\nScanning has been completed");
        }
    }

    private static class Indicator extends TimerTask {
        int counter;
        public void run() {
            counter ++;
            if (counter % 60 == 0) System.out.print('|'); else if (counter % 6 == 0) System.out.print('.');
        }
    }
}
