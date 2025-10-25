package com.alok.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private static PrintWriter writer;
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void init(String outDir) {
        try {
            Files.createDirectories(Path.of(outDir));
            writer = new PrintWriter(new FileWriter(Path.of(outDir, "backtest.log").toFile(), false), true);
        } catch (IOException e) {
            System.err.println("Failed to open log file: " + e.getMessage());
        }
    }

    public static void info(String msg) {
        String line = "[" + LocalDateTime.now().format(TS) + "] " + msg;
        System.out.println(line);
        if (writer != null) writer.println(line);
    }

    public static void close() {
        if (writer != null) writer.close();
    }
}
