package ru.sbertech;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import java.io.File;
import java.io.FileFilter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Тестирует ExcludingFileNameParser.
 * @author Dmitry Dobrynin
 *         Date: 11.11.11 time: 0:12
 */
public class ExcludingFileNameParserTest {
    ExcludingFileNameParser p = new ExcludingFileNameParser();
    Scanner s = mock(Scanner.class);

    @Test
    public void testParse() throws Exception {
        ArgumentCaptor<FileFilter> captor = ArgumentCaptor.forClass(FileFilter.class);
        assertTrue(p.parse("--pom.xml", s));
        verify(s).fileFilter(captor.capture());
        FileFilter f = captor.getValue();
        assertFalse(f.accept(new File("./pom.xml")));
        assertTrue(f.accept(new File("src/main/java/ru/sbertech/Appender.java")));
        assertTrue(f.accept(new File("src/main/java/ru/sbertech/Filter.java")));

        assertFalse(p.parse("unrecognized_parameter", s));
        verifyZeroInteractions(s);
    }
}
