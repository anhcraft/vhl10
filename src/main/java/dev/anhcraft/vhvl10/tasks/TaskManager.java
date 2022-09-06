package dev.anhcraft.vhvl10.tasks;

import javafx.application.Platform;

import java.util.concurrent.*;

public class TaskManager {
    private static final ExecutorService asyncPool = Executors.newFixedThreadPool(3);
    private static final ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(3);

    public static void runSync(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public static Future<?> runAsync(Runnable runnable) {
        return asyncPool.submit(runnable);
    }

    public static ScheduledFuture<?> runDelayAsync(Runnable runnable, long delay, TimeUnit timeUnit) {
        return scheduledPool.schedule(runnable, delay, timeUnit);
    }

    public static ScheduledFuture<?> runRepeatAsync(Runnable runnable, long init, long interval, TimeUnit timeUnit) {
        return scheduledPool.scheduleAtFixedRate(runnable, init, interval, timeUnit);
    }

    public static void destroy() {
        asyncPool.shutdown();
        scheduledPool.shutdown();
    }
}
