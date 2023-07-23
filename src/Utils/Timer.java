package Utils;

public abstract class Timer {
    private final Object o = new Object();
    private Thread t = null;
    private boolean f;
    private float s;

    public Timer(final float sleep) { s = sleep; f = true; }
    public Timer(final float sleep, final boolean firstRun) { s = sleep; f = firstRun; }

    public abstract void run();

    public Timer start() {
        synchronized (o) {
            if (t != null)
                return this;
            t = new Thread(() -> {
                try {
                    long l1 = System.currentTimeMillis(), l2;
                    if (f)
                        run();
                    while (true) {
                        l2 = System.currentTimeMillis();
                        final float d;
                        synchronized (o) {
                            d = s - (l2 - l1);
                        }
                        l1 = l2;
                        if (d > 0)
                            Thread.sleep((int) Math.ceil(d));
                        run();
                    }
                } catch (final InterruptedException ignored) {}
            });
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
        return this;
    }

    public Timer stop() {
        synchronized (o) {
            if (t == null)
                return this;
            t.interrupt();
            t = null;
        }
        return this;
    }

    public Timer setInterval(final float sleep) {
        synchronized (o) {
            s = sleep;
        }
        return this;
    }

    public Timer firstRun(final boolean firstRun) {
        f = firstRun;
        return this;
    }
}