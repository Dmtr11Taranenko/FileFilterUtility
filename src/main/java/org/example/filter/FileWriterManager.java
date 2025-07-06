package org.example.filter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;

/**
 * @author Dmitrii Taranenko
 */
public class FileWriterManager {
    private final EnumMap<DataType, BufferedWriter> writers = new EnumMap<>(DataType.class);
    private final String outDir;
    private final String prefix;
    private final boolean append;

    public FileWriterManager(String outDir, String prefix, boolean append) {
        this.outDir = outDir;
        this.prefix = prefix;
        this.append = append;
    }

    public void write(DataType type, String line) throws IOException {
        if (!writers.containsKey(type)) {
            File dir = new File(outDir);
            if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot create output directory: " + outDir);
            String name = prefix + type.name().toLowerCase() + "s.txt";
            File f = new File(dir, name);
            writers.put(type, new BufferedWriter(new FileWriter(f, append)));
        }
        BufferedWriter w = writers.get(type);
        w.write(line);
        w.newLine();
    }

    public void closeAll() {
        writers.values().forEach(w -> {
            try { w.close(); } catch (IOException ignored) {}
        });
    }
}
