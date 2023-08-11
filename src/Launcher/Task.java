package Launcher;

import Utils.ObjLocker;

import java.util.Map;

public abstract class Task {
    final Object po = new Object();
    boolean f = false, l = false;
    private long p = 0, m = -1;

    public abstract void run() throws Throwable;

    final void LRun() {
        try {
            run();
            synchronized (po) {
                f = true;
                l = false;
                po.notifyAll();
            }
            synchronized (FLCore.groups) {
                boolean n = false;
                for (final TaskGroup g : FLCore.groups.toArray(new TaskGroup[0])) {
                    final boolean pop, fg;
                    synchronized (g) {
                        synchronized (g.tasks) {
                            pop = g.tasks.remove(this);
                            fg = pop && g.tasks.isEmpty();
                            if (pop)
                                g.tc--;
                        }
                        if (fg) {
                            synchronized (FLCore.iml) {
                                for (final Map.Entry<String, TaskGroup> e : FLCore.iml.entrySet())
                                    if (g == e.getValue())
                                        FLCore.iml.remove(e.getKey());
                            }
                            g.notifyAll();
                            if (n)
                                FLCore.groups.remove(g);
                            else
                                n = FLCore.groups.remove(g);
                        }
                    }
                    if (pop)
                        synchronized (g.po) {
                            if (g instanceof TaskGroupAutoProgress)
                                g.p++;
                            if (fg)
                                g.f = true;
                            g.po.notifyAll();
                        }
                }
                if (n)
                    FLCore.groups.notifyAll();
            }
        } catch (final Throwable ex) {
            ex.printStackTrace();
            synchronized (po) {
                l = false;
                po.notifyAll();
            }
        }
    }

    public void setMaxProgress(final long maxProgress) {
        synchronized (po) {
            m = maxProgress;
            po.notifyAll();
        }
    }

    public void setProgress(final long progress, final long maxProgress) {
        synchronized (po) {
            m = maxProgress;
            p = progress;
            po.notifyAll();
        }
    }

    public void setProgress(final long progress) {
        synchronized (po) {
            p = progress;
            po.notifyAll();
        }
    }

    public void addMaxProgress(final long maxProgress) {
        synchronized (po) {
            m += maxProgress;
            po.notifyAll();
        }
    }

    public void addProgress(final long progress, final long maxProgress) {
        synchronized (po) {
            m += maxProgress;
            p += progress;
            po.notifyAll();
        }
    }

    public void addProgress(final long progress) {
        synchronized (po) {
            p += progress;
            po.notifyAll();
        }
    }

    public final boolean isFinished() { return f; }

    public final void waitFinish() throws InterruptedException {
        synchronized (po) {
            if (f)
                return;
            if (!FLCore.isTaskThread()) {
                while (!f)
                    po.wait();
                return;
            }
        }
        final ObjLocker lo = new ObjLocker(po);
        lo.lock();
        while (!f)
            if (l) {
                lo.unlock();
                FLCore.runAnyTask(lo);
                lo.lock();
            } else {
                l = true;
                lo.unlock();
                LRun();
                lo.lock();
            }
        lo.unlock();
    }
}
