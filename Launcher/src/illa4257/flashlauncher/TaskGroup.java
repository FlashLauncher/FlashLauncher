package illa4257.flashlauncher;

import java.util.concurrent.ConcurrentLinkedQueue;

public final class TaskGroup extends Task {
    final ConcurrentLinkedQueue<Task> tasks;

    public TaskGroup(final Task... tasks) {
        this.tasks = new ConcurrentLinkedQueue<>();
        for (final Task t : tasks)
            this.tasks.offer(t);
        maxProgress.set(this.tasks.size());
    }

    @Override
    protected void run() throws Throwable {
        while (true) {
            final Task t = tasks.poll();
            if (t == null)
                break;
            t.join();
            final Throwable ex = t.getThrowable();
            if (ex != null)
                throw ex;
            progress.incrementAndGet();
        }
    }
}