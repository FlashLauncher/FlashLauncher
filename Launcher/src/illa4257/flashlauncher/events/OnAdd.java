package illa4257.flashlauncher.events;

import illa4257.flashlauncher.QueueTrigger;
import illa4257.i4Framework.base.events.Event;

public class OnAdd<T> extends Event {
    public final QueueTrigger<T> queue;
    public final T element;

    public OnAdd(final QueueTrigger<T> queue, final T element) {
        this.queue = queue;
        this.element = element;
    }
}