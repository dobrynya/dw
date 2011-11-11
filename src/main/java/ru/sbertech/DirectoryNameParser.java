package ru.sbertech;

import java.io.File;

/**
 * Проверяет параметр как название директории.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:53
 */
public class DirectoryNameParser implements ParameterParser {
    public boolean parse(String parameter, Scanner scanner) {
        File f = new File(parameter);
        if (f.exists() && f.isDirectory()) {
            scanner.directory(f);
            return true;
        }
        return false;
    }
}
