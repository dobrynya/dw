package ru.sbertech;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;
import static org.junit.Assert.assertTrue;

/**
 * Тестирует в сканнер сборе.
 * @author Dmitry Dobrynin
 *         Date: 11/11/11 time: 15:59
 */
public class IntegrationTest {
    @Test
    public void test() throws IOException, InterruptedException {
        File result = new File("target/result.lst");
        result.delete();
        DirectoryWorm dw = new DirectoryWorm("target/result.lst",
                new DirectoryNameParser(), new ExcludingDirectoryNameParser(), new ExcludingFileNameParser())
                .configure(new String[]{".", "-target", "-src/test", "--.iml", "--.ipr", "--.iws", "--pom.xml"});
        dw.scan();

        ((ExecutorService) Whitebox.getInternalState(dw, "executor")).awaitTermination(20L, TimeUnit.SECONDS);
        String text = FileUtils.readFileToString(result);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

        for (File f : new File("src/main/java/ru/sbertech").listFiles())
            assertTrue(text.contains(format("[\nfile = %s\ndate = %s\nsize = %s]",
                        f.getCanonicalPath(), sdf.format(new Date(f.lastModified())), f.length())));
    }
}
