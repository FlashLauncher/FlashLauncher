package illa4257.flashlauncher;

import illa4257.flashlauncher.events.OnAdd;

import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTrigger<T> extends ConcurrentLinkedQueue<T> {
    @Override
    public boolean add(final T e) {
        final boolean r = super.add(e);
        if (r) FlashLauncher.framework.fireAllWindows(new OnAdd<>(this, e));
        return r;
    }
}