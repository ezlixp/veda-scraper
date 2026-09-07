package pixlze.monumentascraper.core;

import pixlze.monumentascraper.MonumentaScraper;

public final class SafeExecutor {
    public static void run(UnsafeRunnable action, String errorMessage) {
        try {
            action.run();
        } catch (Exception e) {
            var origin = e.getStackTrace()[0]; // Gets where exception occurred
            String throwingClass = origin.getClassName();

            MonumentaScraper.LOGGER.warn("[{}]: {}", throwingClass, errorMessage);
        }
    }

    @FunctionalInterface
    public interface UnsafeRunnable {
        void run() throws Exception;
    }
}
