package UIL.Swing;

public abstract class STimer {
    private final Object o = new Object();
    private final float s;
    private Thread t = null;

    public STimer(final float sleep) { s = sleep; }

    public abstract void run();

    public STimer start() {
        synchronized (o) {
            if (t != null)
                return this;
            t = new Thread(() -> {
                try {
                    long l1 = System.currentTimeMillis(), l2;
                    while (true) {
                        run();
                        l2 = System.currentTimeMillis();
                        final float d = s - (l2 - l1);
                        l1 = l2;
                        if (d > 0)
                            Thread.sleep((int) Math.ceil(d));
                    }
                } catch (final InterruptedException ignored) {}
            });
            t.setPriority(Thread.MAX_PRIORITY);
            t.start();
        }
        return this;
    }

    public STimer stop() {
        synchronized (o) {
            if (t == null)
                return this;
            t.interrupt();
            t = null;
        }
        return this;
    }
}