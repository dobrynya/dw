package ru.sbertech;

import java.io.File;
import java.io.FileFilter;

/**
 * Позволяет исключать имена файлов или файлы с заданным расширением.
 * Формат: --.tmp
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 23:41
 */
public class ExcludingFileNameParser implements ParameterParser {
    public boolean parse(String parameter, Scanner scanner) {
        if (parameter.startsWith("--")) {
            final String fileNameToExclude = parameter.substring(2);
            scanner.fileFilter(new FileFilter() {
                public boolean accept(File file) {
                    return !file.getAbsolutePath().endsWith(fileNameToExclude);
                }
            });
            return true;
        }
        return false;
    }
}
