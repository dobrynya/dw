package ru.sbertech;

import java.io.*;
import java.util.*;

/**
 * Представляет связку фильтров: все accepted или не-accepted.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:27
 */
public class CompositeFilter implements FileFilter {
    private List<FileFilter> filters = new ArrayList<FileFilter>();

    public List<FileFilter> getFilters() { return Collections.unmodifiableList(filters); }

    public void addFilter(FileFilter filter) {
        filters.add(filter);
    }

    public boolean accept(File file) {
        for (FileFilter f : filters) if (!f.accept(file)) return false;
        return true;
    }
}

