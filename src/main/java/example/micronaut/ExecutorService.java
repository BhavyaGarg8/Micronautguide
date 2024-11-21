package example.micronaut;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorService {
//
    private static final Semaphore semaphore = new Semaphore(4);

    private static final ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.acquire();
                    System.out.println("Task started by: " + Thread.currentThread().getName());
                    Thread.sleep(600);
                    System.out.println("Task completed by: " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        lock.lock();
                        scheduler.submit(task);
                    } finally {
                        lock.unlock();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(65, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                }
            }
        }, 65, TimeUnit.SECONDS);
    }
}
