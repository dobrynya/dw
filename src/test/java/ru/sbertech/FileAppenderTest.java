package ru.sbertech;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Тестирует FileAppender.
 * @author Dmitry Dobrynin
 *         Date: 11/11/11 time: 13:51
 */
public class FileAppenderTest {
    Scanner s = mock(Scanner.class);
    FileFilter f = mock(FileFilter.class);
    Writer w = mock(Writer.class);
    FileAppender fa = new FileAppender(new File("src/main"), f, s);

    @Test
    public void testAppend() throws Exception {
        File na = new File("not-accepted.ext");
        when(f.accept(na)).thenReturn(false);
        fa.append(na);

        // проверка на отложенное создание временного файла
        assertNull(Whitebox.getInternalState(fa, "output"));
        assertNull(Whitebox.getInternalState(fa, "writer"));

        when(f.accept(any(File.class))).thenReturn(true);
        File real = new File("pom.xml");
        fa.append(real);

        File o = (File) Whitebox.getInternalState(fa, "output");
        assertNotNull(o);
        Writer w = (Writer) Whitebox.getInternalState(fa, "writer");
        assertNotNull(w);
        w.close();
        String written = format("[\nfile = %s\ndate = %s\nsize = %s]", real.getCanonicalPath(),
                new SimpleDateFormat("yyyy.MM.dd").format(new Date(real.lastModified())), real.length());
        assertEquals(written, FileUtils.readFileToString(o));

        // проверка при ошибке
        try { fa.append(real); }
        catch (RuntimeException e) { assertEquals("Could not write into file!", e.getMessage()); }
        verify(s).closed(fa);
        assertFalse(o.exists());
    }

    @Test
    public void testClose() throws Exception {
        fa.close();

        Whitebox.setInternalState(fa, "writer", w);
        fa.close();
        verify(w).close();
        doThrow(new RuntimeException("Generated for testing!")).when(w).close();
        fa.close(); // исключение должно быть проигнорировано

        verify(s, times(3)).closed(fa);
    }

    @Test
    public void testDumpSucceeds() throws Exception {
        OutputStream os = mock(OutputStream.class);
        fa.dump(os);
        verifyZeroInteractions(os);

        File pom = new File("pom.xml");
        File copy = new File("target/pom.xml");
        FileUtils.copyFile(pom, copy);

        Whitebox.setInternalState(fa, "output", copy);
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) copy.length());
        fa.dump(baos);
        assertArrayEquals(FileUtils.readFileToByteArray(pom), baos.toByteArray());
        assertFalse(copy.exists());
    }

    @Test
    public void testDumpFails() throws Exception {
        Whitebox.setInternalState(fa, "output", new File("non-existent"));

        OutputStream os = mock(OutputStream.class);
        doThrow(new IOException("Generated for testing!")).when(os).write(any(byte[].class), anyInt(), anyInt());
        try { fa.dump(os); fail(); }
        catch (RuntimeException e) { assertTrue(e.getMessage().startsWith("Could not dump file: ")); }
    }

    @Test
    public void testDeleteFile() throws Exception {
        fa.deleteFile();
        Whitebox.setInternalState(fa, "output", new File("non-existent"));
        fa.deleteFile();
        File copy = new File("target/pom.xml");
        FileUtils.copyFile(new File("pom.xml"), copy);
        File mock = mock(File.class);
        when(mock.delete()).thenReturn(false);
        when(mock.exists()).thenReturn(true);
        Whitebox.setInternalState(fa, "output", mock);
        fa.deleteFile();
    }

    @Test
    public void testSorting() {
        FileAppender a1 = new FileAppender(new File("src/main"), f, s),
                a2 = new FileAppender(new File("src/test"), f, s);
        assertTrue(a1.compareTo(a2) < 0);
    }
}
