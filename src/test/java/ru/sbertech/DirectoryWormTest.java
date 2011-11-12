package ru.sbertech;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.reflection.Whitebox;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ExecutorService;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Тестирует DirectoryWorm.
 * @author Dmitry Dobrynin
 * Created 12.11.11 22:17
 */
@SuppressWarnings({"unchecked"})
public class DirectoryWormTest {
    ParameterParser p1 = mock(ParameterParser.class), p2 = mock(ParameterParser.class);
    OutputStream os = mock(OutputStream.class);
    DirectoryWorm dw = new DirectoryWorm(os, p1, p2);
    ExecutorService executor = mock(ExecutorService.class);
    FileFilter ff = mock(FileFilter.class);
    FileAppender a1 = mock(FileAppender.class), a2 = mock(FileAppender.class);

    @Before
    public void before() {
        Whitebox.setInternalState(dw, "executor", executor);
        when(a1.compareTo(a2)).thenReturn(-1);
        when(a1.compareTo(a1)).thenReturn(0);
        when(a2.compareTo(a1)).thenReturn(1);
        when(a2.compareTo(a2)).thenReturn(0);
    }

    @Test
    public void testParseParameter() {
        when(p1.parse(anyString(), eq(dw))).thenReturn(false);
        when(p1.parse(anyString(), eq(dw))).thenReturn(true, false);
        assertTrue(dw.parseParameter("parameter"));
        assertFalse(dw.parseParameter("parameter"));
    }

    @Test
    public void testConfigure() {
        when(p1.parse("parameter", dw)).thenReturn(false);
        when(p2.parse("parameter", dw)).thenReturn(true, false);
        dw.configure(new String[]{"parameter"});
        verify(p1).parse("parameter", dw);
        verify(p2).parse("parameter", dw);

        try { dw.configure(new String[] {"parameter"}); fail(); }
        catch (IllegalArgumentException ignored) {}
    }

    @Test
    public void testDirectory() {
        File f = new File("src");
        dw.directory(f);
        assertEquals(asList(f), Whitebox.getInternalState(dw, "roots")); // проверка на отложенный старт
        Whitebox.setInternalState(dw, "started", true);
        dw.directory(f);
        assertEquals(asList(f), Whitebox.getInternalState(dw, "roots")); // немедленный старт
    }

    @Test
    public void testScanDirectory() throws Exception {
        dw.directoryFilter(ff);
        when(ff.accept(any(File.class))).thenReturn(false, true);
        File f = new File(".");
        dw.scanDirectory(f);
        verifyZeroInteractions(executor);
        ArgumentCaptor<DirectoryBrowser> dbc = ArgumentCaptor.forClass(DirectoryBrowser.class);
        dw.scanDirectory(f);
        Set<Appender> wa = (Set<Appender>) Whitebox.getInternalState(dw, "workingAppenders");
        assertEquals(1, wa.size());
        verify(executor).execute(dbc.capture());
        DirectoryBrowser db = dbc.getValue();
        assertSame(f, Whitebox.getInternalState(db, "directory"));
        assertSame(wa.iterator().next(), Whitebox.getInternalState(db, "appender"));
    }

    @Test
    public void testFinish() throws Exception {
        ((SortedSet<Appender>) Whitebox.getInternalState(dw, "closedAppenders")).add(a1);
        ((SortedSet<Appender>) Whitebox.getInternalState(dw, "closedAppenders")).add(a2);
        doThrow(new RuntimeException("Generated for testing!")).when(a1).dump(os);
        doThrow(new IOException("Generated for testing!")).when(os).close();

        dw.finish();
        verify(a1).dump(os);
        verify(a2).dump(os);
        verify(executor).shutdown();
        verify(os).close();
    }

    @Test
    public void testClosed() throws Exception {
        ((Set<Appender>) Whitebox.getInternalState(dw, "workingAppenders")).add(a1);
        ((Set<Appender>) Whitebox.getInternalState(dw, "workingAppenders")).add(a2);
        dw.closed(a1);
        assertFalse(((Set<Appender>) Whitebox.getInternalState(dw, "workingAppenders")).contains(a1));
        assertEquals(1, ((SortedSet<Appender>) Whitebox.getInternalState(dw, "closedAppenders")).size());
        assertTrue(((SortedSet<Appender>) Whitebox.getInternalState(dw, "closedAppenders")).contains(a1));
    }
}
