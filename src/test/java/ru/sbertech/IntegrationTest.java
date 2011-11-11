package ru.sbertech;

import java.io.*;
import org.junit.Test;

/**
 * Тестирует в сканнер сборе.
 * @author Dmitry Dobrynin
 *         Date: 11/11/11 time: 15:59
 */
public class IntegrationTest {
    @Test
    public void test() throws FileNotFoundException {
        new File("result.lst").delete();
        new DirectoryWorm("result.lst",
                new DirectoryNameParser(), new ExcludingDirectoryNameParser(), new ExcludingFileNameParser())
                .configure(new String[] {
                        "c:/work"
                }).scan();
    }
}
