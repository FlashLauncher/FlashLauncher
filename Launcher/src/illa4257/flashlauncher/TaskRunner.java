package illa4257.flashlauncher;

import java.util.concurrent.atomic.AtomicInteger;

import static illa4257.flashlauncher.FlashLauncher.L;

public class TaskRunner extends Thread {
    private static final AtomicInteger i = new AtomicInteger(0);

    {
        setName("FlashLauncher Tasks Runner " + i.getAndIncrement());
    }

    @Override
    public void run() {
        while (true)
            try {
                final Task t = FlashLauncher.tasks.take();
                t.join();
                final Throwable throwable = t.getThrowable();
                if (throwable == null)
                    continue;
                if (throwable instanceof InterruptedException && Thread.currentThread().isInterrupted())
                    break;
                L.log(throwable);
            } catch (final InterruptedException ex) {
                if (Thread.currentThread().isInterrupted())
                    break;
                L.log(ex);
            }
    }
}