package UIL.Swing;

import javax.swing.*;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public abstract class STimer {
    private float s;
    private final ReentrantLock locker = new ReentrantLock();
    private long id;

    public STimer(float sleep) { s = sleep; }

    public abstract void run();

    public STimer start() {
        if (id != 0)
            return this;
        locker.lock();
        try {
            if (id != 0)
                return this;
            Random r = new Random();
            long nid;
            do {
                nid = r.nextLong();
            } while (nid == id || nid == 0);
            id = nid;
            final long fid = nid;
            new Thread(() -> {
                long l = System.currentTimeMillis();
                while (id == fid)
                    try {
                        SwingUtilities.invokeAndWait(STimer.this::run);
                        final long c = System.currentTimeMillis();
                        final float d = s - (c - l);
                        l = c;
                        if (d > 0)
                            Thread.sleep((int) Math.ceil(d));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        break;
                    }
                locker.lock();
                if (id == fid)
                    id = 0;
                locker.unlock();
            }).start();
        } finally {
            locker.unlock();
        }
        return this;
    }

    public STimer stop() {
        if (id == 0)
            return this;
        locker.lock();
        try {
            if (id == 0)
                return this;
            id = 0;
        } finally {
            locker.unlock();
        }
        return this;
    }
}