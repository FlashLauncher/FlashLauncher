package UIL.Swing;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class SFPSTimer {
    static final ConcurrentLinkedQueue<SFPSTimer> timers = new ConcurrentLinkedQueue<>();

    public abstract void run();

    public SFPSTimer start() {
        synchronized (timers) {
            if (timers.contains(this))
                return this;
            timers.add(this);
            timers.notify();
        }
        return this;
    }

    public SFPSTimer stop() {
        synchronized (timers) {
            timers.remove(this);
        }
        return this;
    }
}
