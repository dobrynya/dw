package ru.sbertech;

import java.io.File;
import java.io.FileFilter;

/**
 * Предоставляет операции по конфигурированию и запуску сканирования.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:23
 */
public interface Scanner {
    void directory(File directory);

    void directoryFilter(FileFilter filter);

    void fileFilter(FileFilter filter);

    void scan();

    void closed(Appender appender);
}
