package UIL;

import UIL.base.IComponent;
import UIL.base.IContainer;
import UIL.base.IScrollPane;

public class ContainerListBuilder implements IContainer {
    private final IScrollPane sp;
    public final int o;
    private int w, h, wo, ho, x, y;

    public ContainerListBuilder(final IScrollPane scrollPane, final int size, final int offset) {
        sp = scrollPane;
        w = h = size;
        ho = wo = size + (y = x = o = offset);
    }

    public ContainerListBuilder(final IScrollPane scrollPane, final int width, final int height, final int offset) {
        sp = scrollPane;
        w = width;
        h = height;
        wo = w + (y = x = o = offset);
        ho = h + o;
    }

    public ContainerListBuilder childSize(final int width, final int height) {
        wo = (w = width) + o;
        ho = (h = height) + o;
        return this;
    }

    public int getChildWidth() { return w; }
    public int getChildHeight() { return h; }

    @Override
    public ContainerListBuilder add(final IComponent component) {
        int ox = x;
        x += wo;
        if (x > sp.width()) {
            x = wo + (ox = 8);
            y += ho;
        }
        sp.add(component.size(w, h).pos(ox, y));
        return this;
    }

    @Override
    public ContainerListBuilder add(final IComponent... components) {
        for (final IComponent component : components) {
            int ox = x;
            x += wo;
            if (x > sp.width()) {
                x = wo + (ox = 8);
                y += ho;
            }
            sp.add(component.size(w, h).pos(ox, y));
        }
        return this;
    }

    @Override
    public ContainerListBuilder remove(final IComponent component) {
        sp.remove(component);
        return this;
    }

    @Override
    public IComponent[] childs() {
        return sp.childs();
    }

    @Override
    public ContainerListBuilder clear() {
        sp.clear();
        x = y = o;
        return this;
    }

    @Override
    public ContainerListBuilder size(final int width, final int height) {
        sp.size(width, height);
        return this;
    }

    @Override
    public ContainerListBuilder pos(final int x, final int y) {
        sp.pos(x, y);
        return this;
    }

    @Override
    public ContainerListBuilder visible(final boolean visible) {
        sp.visible(visible);
        return this;
    }

    @Override
    public ContainerListBuilder focus() {
        sp.focus();
        return this;
    }

    public void y(final int newY) { y = newY; }

    @Override
    public ContainerListBuilder update() {
        final int w = sp.width(), h = sp.height(), yr = y + ho;
        if (yr > h)
            sp.content().size(w - 8, yr);
        else
            sp.content().size(w, h);
        sp.update();
        return this;
    }

    @Override public boolean visible() { return sp.visible(); }
    @Override public boolean isFocused() { return sp.isFocused(); }
    @Override public int width() { return sp.width(); }
    @Override public int height() { return sp.height(); }
    @Override public Object getComponent() { return sp.getComponent(); }
}
