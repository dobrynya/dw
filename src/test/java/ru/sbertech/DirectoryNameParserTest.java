package ru.sbertech;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Тестирует DirectoryNameParser.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 23:46
 */
public class DirectoryNameParserTest {
    DirectoryNameParser p = new DirectoryNameParser();
    Scanner s = mock(Scanner.class);

    @Test
    public void testParse() throws Exception {
        File f = new File("src/main/java");
        assertTrue(p.parse(f.getPath(), s));
        verify(s).directory(f);
        assertFalse(p.parse("non-existent directory", s));
        verifyZeroInteractions(s);
        assertFalse(p.parse("pom.xml", s));
        verifyZeroInteractions(s);
    }
}
