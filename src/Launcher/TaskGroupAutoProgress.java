package Launcher;

public class TaskGroupAutoProgress extends TaskGroup {
    public TaskGroupAutoProgress() { m = 0; }
    public TaskGroupAutoProgress(final int threadLimit) { super(threadLimit); m = 0; }

    @Override
    public void addTask(final Task task) {
        if (task == null)
            return;
        synchronized (po) {
            m++;
            po.notifyAll();
        }
        synchronized (tasks) {
            tasks.add(task);
        }
    }

    @Override public final void setProgress(final long progress, final long maxProgress) {}
    @Override public final void setProgress(final long progress) {}
    @Override public final void setMaxProgress(final long maxProgress) {}
}
