package ru.sbertech;

import org.junit.Test;
import org.mockito.Matchers;

import java.io.File;

import static org.mockito.Mockito.*;

/**
 * Тестирует DirectoryBrowser.
 * @author Dmitry Dobrynin
 *         Date: 11.11.11 time: 0:20
 */
public class DirectoryBrowserTest {
    Scanner s = mock(Scanner.class);
    Appender a = mock(Appender.class);
    DirectoryBrowser db;

    @Test
    public void testBrowse1() throws Exception {
        doThrow(new RuntimeException("Generated!")).when(a).append(Matchers.<File>any());
        db = new DirectoryBrowser(new File("src"), s, a);
        db.run();
        verify(s).directory(new File("src/main"));
        verify(s).directory(new File("src/test"));
        verify(a).close();
    }

    @Test
    public void testBrowse2() throws Exception {
        db = new DirectoryBrowser(new File("src/main/java/ru/sbertech"), s, a);
        db.run();
        for (File f : new File("src/main/java/ru/sbertech").listFiles())
            verify(a).append(f);
        verify(a).close();
        verifyZeroInteractions(s);
    }

    @Test
    public void testFailure() {
        File dir = mock(File.class);
        db = new DirectoryBrowser(dir, s, a);
        doThrow(new RuntimeException("Generated for testing!")).when(dir).listFiles();
        db.run();
        verify(a).close();
        verifyZeroInteractions(a, s);
    }

    @Test
    public void testEmptyDirectory() {
        File dir = mock(File.class);
        when(dir.listFiles()).thenReturn(null, new File[0]);
        db = new DirectoryBrowser(dir, s, a);
        db.run();
        db.run();
        verify(a, never()).append(any(File.class));
        verify(s, never()).directory(any(File.class));
        verify(a, times(2)).close();
    }
}
