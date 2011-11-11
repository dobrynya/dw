package ru.sbertech;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;

/**
 * Сохраняет информацию о файлах во временном файле.
 * @author Dmitry Dobrynin
 *         Date: 10.11.11 time: 22:31
 */
public class FileAppender implements Appender, Comparable<FileAppender> {
    private File directory;
    private File output;
    private FileFilter filter;
    private FileWriter writer;
    private Scanner scanner;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

    public FileAppender(File directory, FileFilter filter, Scanner scanner) {
            this.scanner = scanner;
            this.directory = directory;
            this.filter = filter;
    }

    public void append(File file) {
        try {
            if (filter.accept(file)) {
                if (writer == null) writer = new FileWriter(output = File.createTempFile("directory_worm_", ".lst"));
                writer.write(format("[\nfile = %s\ndate = %s\nsize = %s]",
                        file.getCanonicalPath(), sdf.format(new Date(file.lastModified())), file.length()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            close();
            deleteFile();
            throw new RuntimeException("Could not write into file!", e);
        }
    }

    public void close() {
        try { if (writer != null) writer.close(); }
        catch (Exception ignored) { /* не катастрофично, если случится ошибка при закрытии потока */
            ignored.printStackTrace();
        } finally { scanner.closed(this); }
    }

    public void dump(OutputStream stream) {
        FileInputStream is = null;
        if (output != null) try {
            byte[] buffer = new byte[1024 * 4 * 2]; // 2 NTFS кластера
            is = new FileInputStream(output);
            int readBytes;
            while ((readBytes = is.read(buffer)) > 0) stream.write(buffer, 0, readBytes);
        } catch (Exception e) {
            throw new RuntimeException(format("Could not dump file: %s!", output));
        } finally {
            if (is != null) try { is.close(); }
            catch (IOException ignored) { /* не катастрофично, если случится ошибка при закрытии потока */ }
            deleteFile();
        }
    }

    private void deleteFile() {
        if (output != null && !output.delete()) System.out.println(format("\nFile % has not been deleted!\n", output));
    }

    public int compareTo(FileAppender another) {
        return directory.compareTo(another.directory);
    }
}
