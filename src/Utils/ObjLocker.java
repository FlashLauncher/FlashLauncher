package Utils;

import java.util.ConcurrentModificationException;

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
                } catch (final Throwable ex) {
                    if (ex instanceof InterruptedException)
                        return;
                    ex.printStackTrace();
                    ex.fillInStackTrace();
                    ex.printStackTrace();
                }
            }, "ObjectLocker of " + o.getClass().getName());
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            try {
                lo.wait();
            } catch (final Throwable ex) {
                l = false;
                lo.notify();
                t.interrupt();
                t = null;
                throw ex;
            }
        }
    }

    public boolean isLocked() { synchronized (lo) { return l; } }

    public void waitNotify() throws InterruptedException {
        synchronized (lo) {
            if (!l)
                lock();
            w = true;
            lo.notify();
            try {
                lo.wait();
                lo.notify();
            } catch (final InterruptedException ex) {
                w = false;
                throw ex;
            }
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

    public Object getLocker() { return lo; }
}