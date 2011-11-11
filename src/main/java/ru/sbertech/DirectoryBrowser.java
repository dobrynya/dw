package ru.sbertech;

import java.io.File;
import java.util.TreeSet;

import static java.util.Arrays.asList;

/**
 * Производит обход директории.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:54
 */
public class DirectoryBrowser implements Runnable {
    private File directory;
    private Scanner scanner;
    private Appender appender;

    public DirectoryBrowser(File directory, Scanner scanner, Appender appender) {
        this.directory = directory;
        this.scanner = scanner;
        this.appender = appender;
    }

    public void browse() {
        try {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0)
                for (File f : new TreeSet<File>(asList(files)))
                    if (f.isDirectory()) scanner.directory(f); else appender.append(f);
        } catch (Exception e) { /* останавливаем обход */ } finally {
            appender.close();
        }
    }

    public void run() { browse(); }
}
