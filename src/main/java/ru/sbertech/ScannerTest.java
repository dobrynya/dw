package ru.sbertech;

import java.io.*;

public class ScannerTest {
    public static void main(String[] args) throws IOException {
        File result = new File("result.lst");
        result.delete();
        FileOutputStream output = new FileOutputStream(result);
        new DirectoryWorm(output,
                new DirectoryNameParser(), new ExcludingDirectoryNameParser(), new ExcludingFileNameParser())
                .configure(new String[] {
                        "../frameworks/maven/repository", "--.sha1", "--.tmp", "--.lastUpdated"
                }).scan();
    }
}

