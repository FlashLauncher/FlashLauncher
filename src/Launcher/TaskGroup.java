package Launcher;

import Utils.ObjLocker;

import java.util.ArrayList;

public class TaskGroup {
    final Object po = new Object();
    final ArrayList<Task> tasks = new ArrayList<>();
    final int tl;
    int tc = 0;
    long p = 0, m = -1;
    boolean f = false;

    public TaskGroup() { tl = -1; }
    public TaskGroup(final int threadLimit) { tl = threadLimit; }

    public Task[] getTasks() {
        synchronized (tasks) {
            return tasks.toArray(new Task[0]);
        }
    }

    public Task getTask(final int index) {
        synchronized (tasks) {
            return tasks.size() > index ? tasks.get(index) : null;
        }
    }

    public boolean containsTask(final Task task) {
        if (task == null)
            return false;
        synchronized (tasks) {
            return tasks.contains(task);
        }
    }

    public int indexOfTask(final Task task) {
        if (task == null)
            return -1;
        synchronized (tasks) {
            return tasks.indexOf(task);
        }
    }

    public int getTaskCount() {
        synchronized (tasks) {
            return tasks.size();
        }
    }

    public void addTask(final Task task) {
        if (task == null)
            return;
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    public void setProgress(final long progress, final long maxProgress) {
        synchronized (po) {
            p = progress;
            m = maxProgress;
            po.notifyAll();
        }
    }

    public void setProgress(final long progress) {
        synchronized (po) {
            p = progress;
            po.notifyAll();
        }
    }

    public void setMaxProgress(final long maxProgress) {
        synchronized (po) {
            m = maxProgress;
            po.notifyAll();
        }
    }

    final Task lockAny() {
        synchronized (tasks) {
            if (tl > -1 && tc >= tl)
                return null;
            for (final Task t : tasks)
                synchronized (t.po) {
                    if (!t.f && !t.l) {
                        t.l = true;
                        tc++;
                        return t;
                    }
                }
            return null;
        }
    }

    public void waitFinish() throws InterruptedException {
        synchronized (po) {
            if (f)
                return;
        }
        if (!FLCore.isTaskThread()) {
            synchronized (po) {
                while (!f)
                    po.wait();
            }
            return;
        }
        final ObjLocker l = new ObjLocker(po);
        l.lock();
        while (!f) {
            final Task t = lockAny();
            l.unlock();
            if (t == null)
                FLCore.runAnyTask(l);
            else
                t.LRun();
            l.lock();
        }
        l.unlock();
    }
}
