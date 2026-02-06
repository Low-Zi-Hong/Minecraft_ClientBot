package net.wtf.AutoRun;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaitToReact {


    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public static void waitAwhile(Wocao.Callback callback) {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                callback.onComplete(true);
            } catch (InterruptedException e) {
                callback.onComplete(false);
                throw new RuntimeException(e);
            }
        }).start();
    }
}
