package UIL;

import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IScrollPane;

public class ContainerListBuilder {
    private final IScrollPane sp;
    private final IContainer c;
    public final int w, h, o, wo, ho;
    private int x, y;

    public ContainerListBuilder(final IContainer container, final IScrollPane scrollPane, final int size, final int offset) {
        c = container;
        sp = scrollPane;
        w = h = size;
        ho = wo = size + (y = x = o = offset);
    }

    public ContainerListBuilder(final IContainer container, final IScrollPane scrollPane, final int width, final int height, final int offset) {
        c = container;
        sp = scrollPane;
        w = width;
        h = height;
        wo = w + (y = x = o = offset);
        ho = h + o;
    }

    public ContainerListBuilder add(IComponent component) {
        int ox = x;
        x += wo;
        if (x > sp.width()) {
            x = wo + (ox = 8);
            y += ho;
        }
        c.add(component.size(w, h).pos(ox, y));
        return this;
    }

    public void clear() {
        c.clear();
        x = y = o;
    }

    public ContainerListBuilder reset() {
        x = y = o;
        return this;
    }

    public void y(int newY) { y = newY; }

    public void resize() {
        final int w = sp.width(), h = sp.height(), yr = y + ho;
        if (yr > h)
            c.size(w - 8, yr);
        else
            c.size(w, h);
        sp.update();
    }
}
