package cn.kungreat.fxgamemap.util;

import cn.kungreat.fxgamemap.Configuration;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * 日志服务类 单独的一个线程处理
 * */
public class LogService extends Configuration {

    private static final BlockingQueue<String> LOG_QUEUE = new LinkedBlockingQueue<>();
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String LOG_PREFIX = "fxgamemap";
    public static final String LOG_TYPE = ".txt";
    public static PrintWriter errorPrint;

    static {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Configuration.errorPrint,true);
            errorPrint = new PrintWriter(fileOutputStream);
        } catch (Exception ignored) {}
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    String line = LOG_QUEUE.take() + System.lineSeparator();
                    String currentFileName = Configuration.logDirectory + LOG_PREFIX +
                            LocalDate.now().format(DATE_FORMATTER) + LOG_TYPE;
                    Path path = Paths.get(currentFileName);
                    Files.writeString(path, line, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                }
            } catch (Exception e) {
                e.printStackTrace(errorPrint);
                errorPrint.flush();
            }
        }, "LogService");
        thread.setDaemon(true);
        thread.start();
    }

    public static void writerLog(LogLevel logLevel, Class<?> cls, String msg, Object... args) {
        String log = LocalTime.now().format(TIME_FORMATTER) + " " + logLevel.name() + " "
                + cls.getTypeName() + String.format(msg, args);
        LOG_QUEUE.add(log);
    }

    public static void printLog(LogLevel logLevel, Class<?> cls, String msg, Throwable throwable) {
        StringBuilder log = new StringBuilder(LocalTime.now().format(TIME_FORMATTER) + " " + logLevel.name() + " "
                + cls.getTypeName() + msg);
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        for (StackTraceElement traceElement : stackTrace) {
            log.append(traceElement.getClassName()).append(".").append(traceElement.getMethodName()).append("方法第").
                    append(traceElement.getLineNumber()).append("行出错 ").append(throwable.getClass()).append(" ").append(throwable.getMessage());
        }
        LOG_QUEUE.add(log.toString());
    }

    public enum LogLevel {
        INFO, WARNING, ERROR
    }
}
