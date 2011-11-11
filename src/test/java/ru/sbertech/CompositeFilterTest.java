package ru.sbertech;

import org.junit.Test;

import java.io.File;
import java.io.FileFilter;

import static org.junit.Assert.*;

/**
 * Тестирует CompositeFilter.
 * @author Dmitry Dobrynin
 *         Date: 11.11.11 time: 3:20
 */
public class CompositeFilterTest {
    CompositeFilter cf = new CompositeFilter();

    @Test
    public void testAddFilter() throws Exception {
        assertTrue(cf.getFilters().isEmpty());
        cf.addFilter(cf);
        assertEquals(1, cf.getFilters().size());
        assertSame(cf, cf.getFilters().get(0));
    }

    @Test
    public void testAccept() throws Exception {
        FileFilter accepting = new FileFilter() {
            public boolean accept(File pathname) {
                return true;
            }
        };
        cf.addFilter(accepting);
        assertTrue(cf.accept(null));
        cf.addFilter(accepting);
        assertTrue(cf.accept(null));
        cf.addFilter(new FileFilter() {
            public boolean accept(File pathname) {
                return false;
            }
        });
        assertFalse(cf.accept(null));
    }
}
