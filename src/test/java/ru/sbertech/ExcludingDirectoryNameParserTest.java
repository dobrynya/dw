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
 * Тестирует ExcludingDirectoryNameParser.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 23:56
 */
public class ExcludingDirectoryNameParserTest {
    ExcludingDirectoryNameParser p = new ExcludingDirectoryNameParser();
    Scanner s = mock(Scanner.class);

    @Test
    public void testParse() throws Exception {
        ArgumentCaptor<FileFilter> captor = ArgumentCaptor.forClass(FileFilter.class);
        assertTrue(p.parse("-src/main/java", s));
        verify(s).directoryFilter(captor.capture());
        FileFilter f = captor.getValue();
        assertFalse(f.accept(new File("src/main/java")));
        assertTrue(f.accept(new File("src/main")));
        assertTrue(f.accept(new File("src/test")));

        assertFalse(p.parse("-non-existent directory", s));
        verifyZeroInteractions(s);
        assertFalse(p.parse("src/main/java", s));
        verifyZeroInteractions(s);
    }
}
