package example.micronaut;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Executor {

    private static final int MAX_CONCURRENT_TASKS = 4;
    private static final int TASK_DURATION_MS = 6000;

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    private static int runningTasks = 0;

    public static void main(String[] args) {

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10); // We can schedule 10 tasks per second

        Runnable task = new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {

                    while (runningTasks >= MAX_CONCURRENT_TASKS) {
                        condition.await();
                    }

                    runningTasks++;
                    System.out.println("Task started by: " + Thread.currentThread().getName() + " | Running tasks: " + runningTasks);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }

                try {
                    Thread.sleep(TASK_DURATION_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                lock.lock();
                try {
                    runningTasks--;
                    System.out.println("Task completed by: " + Thread.currentThread().getName() + " | Running tasks: " + runningTasks);

                    condition.signalAll();
                } finally {
                    lock.unlock();
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            scheduler.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    scheduler.submit(task);
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
