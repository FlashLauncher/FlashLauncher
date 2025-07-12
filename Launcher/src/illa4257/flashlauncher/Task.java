package illa4257.flashlauncher;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Task {
    protected final Object locker = new Object();
    protected volatile boolean start = false, finish = false;
    private volatile Throwable throwable;
    public volatile AtomicLong progress = new AtomicLong(0), maxProgress = new AtomicLong(100);

    protected abstract void run() throws Throwable;

    public final Throwable getThrowable() { return throwable; }

    public final void join() throws InterruptedException {
        if (finish)
            return;
        synchronized (locker) {
            if (finish)
                return;
            if (start || !(Thread.currentThread() instanceof TaskRunner)) {
                locker.wait();
                return;
            } else
                start = true;
        }
        try {
            run();
        } catch (final Throwable t) {
            throwable = t;
        }
        synchronized (locker) {
            finish = true;
            locker.notifyAll();
        }
    }
}