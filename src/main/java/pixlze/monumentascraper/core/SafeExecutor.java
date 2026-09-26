package pixlze.monumentascraper.core;

import java.util.Arrays;
import java.util.stream.Collectors;

import pixlze.monumentascraper.MonumentaScraper;

public final class SafeExecutor {
    public static void run(UnsafeRunnable action, String errorMessage) {
        try {
            action.run();
        } catch (Exception e) {
            log(e, errorMessage);
        }
    }

    public static <T> T run(UnsafeSupplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (Exception e) {
            log(e, errorMessage);
            return null;
        }
    }

    private static void log(Exception e, String errorMessage) {
        StackTraceElement origin = e.getStackTrace()[0]; // Gets where exception occurred
        String throwingClass = origin.getClassName();
        String stackTrace = Arrays.stream(
                e.getStackTrace()).map((el) -> "| " + el)
                .collect(Collectors.joining("\n"));

        // [errorClass]: errorMessage
        // | Exception: exception
        // | stackTrace1...
        // | stackTrace2...
        // | stackTrace3...
        MonumentaScraper.LOGGER.warn("[{}]: {}\n| {}: {}\n{}",
                throwingClass,
                errorMessage, e,
                e.getMessage(),
                stackTrace);
    }

    @FunctionalInterface
    public interface UnsafeRunnable {
        void run() throws Exception;
    }

    @FunctionalInterface
    public interface UnsafeSupplier<T> {
        T get() throws Exception;
    }

}
