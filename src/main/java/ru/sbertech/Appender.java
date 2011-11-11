package ru.sbertech;

import java.io.File;
import java.io.OutputStream;

/**
 * Сохраняет информацию о файлах. Реализация Comparable позволит всегда отсортировать множество Appender'ов для вывода
 * в итоговый список.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:25
 */
interface Appender {
    void append(File file);

    void close();

    void dump(OutputStream stream);
}