package com.alexeyshurygin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Alexey Shurygin
 */
public class StatsDumper {
    public static final Logger log = LogManager.getLogger(StatsDumper.class);

    private final ScheduledExecutorService logDumperScheduler = Executors.newScheduledThreadPool(1/*, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            final Thread thread = new Thread();
            thread.setDaemon(true);
            return thread;
        }
    }*/);
    private static final int LOG_PERIOD_SECONDS = 60;
    private static StatsDumper instance = new StatsDumper();

    private StatsDumper() {
        final Runnable logDumper = new Runnable() {
            @Override
            public void run() {
                try {
                    final Map<String, Integer> stats24h = BannerStats.getInstance().getStats24h();
                    //TODO
                    log.info("Top 100 banners in 24h: {}", stats24h.keySet());
                } catch (Throwable e) {
                    try {
                        log.error("Error during statistics dumping", e);
                    } catch (Throwable e1) {
                        //Swallow it. There's no way to handle OOME gently here.
                    }
                }
            }
        };
        logDumperScheduler.scheduleAtFixedRate(logDumper, LOG_PERIOD_SECONDS, LOG_PERIOD_SECONDS, TimeUnit.SECONDS);
    }

    public static StatsDumper getInstance() {
        return instance;
    }

    public void start() {
        //NOP
        logDumperScheduler.toString();
    }
}
