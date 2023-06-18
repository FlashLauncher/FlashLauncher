package Utils;

public class ObjLocker {
    private final Object lo = new Object(), o;
    private boolean l = false, w = false;
    private Thread t = null;

    public ObjLocker(final Object object) { o = object; }

    public void lock() throws InterruptedException {
        synchronized (lo) {
            if (l)
                return;
            l = true;
            t = new Thread(() -> {
                try {
                    synchronized (o) {
                        synchronized (lo) {
                            lo.notify();
                        }
                        while (l)
                            if (w) {
                                o.wait();
                                synchronized (lo) {
                                    if (!l || !w)
                                        continue;
                                    lo.notify();
                                }
                            } else
                                synchronized (lo) {
                                    if (w || !l)
                                        continue;
                                    lo.wait();
                                }
                    }
                } catch (final InterruptedException ignored) {}
            }) {{
                setPriority(MIN_PRIORITY);
                start();
            }};
            lo.wait();
        }
    }

    public void waitNotify() throws InterruptedException {
        synchronized (lo) {
            if (!l)
                lock();
            w = true;
            lo.notify();
            lo.wait();
            w = false;
        }
    }

    public void unlock() {
        synchronized (lo) {
            w = l = false;
            lo.notify();
            if (t != null) {
                t.interrupt();
                t = null;
            }
        }
    }
}