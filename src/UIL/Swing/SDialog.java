package UIL.Swing;

import UIL.base.*;
import Utils.Runnable1arg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SDialog implements IDialog {
    private final JDialog d;
    private final Container cont;
    private boolean packed = false;

    public SDialog(final IFrame owner, final String title) {
        d = new JDialog(owner == null ? null : (JFrame) owner.getComponent(), title);
        d.setModal(true);
        cont = d.getContentPane();
        cont.setLayout(null);
    }

    @Override public int width() { return cont.getWidth(); }
    @Override public int height() { return cont.getHeight(); }
    @Override public boolean visible() { return d.isVisible(); }
    @Override public boolean isFocused() { return d.isFocused(); }
    @Override public IComponent[] childs() { return (IComponent[]) cont.getComponents(); }
    @Override public Object getComponent() { return this; }

    @Override
    public SDialog icon(final IImage icon) {
        d.setIconImage((Image) icon.getImage());
        return this;
    }

    @Override
    public SDialog resizable(final boolean resizable) {
        d.setResizable(resizable);
        return this;
    }

    @Override
    public SDialog add(final IComponent component) {
        cont.add((Component) component.getComponent(), 0);
        return this;
    }

    @Override
    public SDialog add(final IComponent... components) {
        for (final IComponent c : components)
            cont.add((Component) c.getComponent(), 0);
        return this;
    }

    @Override
    public SDialog remove(final IComponent component) {
        cont.remove((Component) component.getComponent());
        return this;
    }

    @Override
    public SDialog clear() {
        cont.removeAll();
        return this;
    }

    @Override
    public SDialog size(final int width, final int height) {
        cont.setPreferredSize(new Dimension(width, height));
        d.setSize(width + (d.getInsets()).left + (d.getInsets()).right, height + (d.getInsets()).top + (d.getInsets()).bottom);
        return this;
    }

    @Override public SDialog pos(final int x, final int y) { d.setLocation(x, y); return this; }

    @Override
    public SDialog pack() {
        if (!packed) {
            packed = true;
            d.pack();
        }
        return this;
    }

    @Override public SDialog center(final IComponent component) { d.setLocationRelativeTo(component == null ? null : (Component) component.getComponent()); return this; }
    @Override public SDialog visible(final boolean visible) {
        if (!packed) {
            packed = true;
            d.pack();
        }
        d.setVisible(visible);
        return this;
    }
    @Override public SDialog focus() { d.requestFocus(); return this; }

    @Override
    public SDialog background(final IColor bg) {
        d.setBackground((Color) bg.get());
        return this;
    }

    @Override
    public SDialog onClose(final Runnable1arg<IFrame> listener) {
        d.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                listener.run(SDialog.this);
            }
        });
        return this;
    }

    @Override
    public SDialog update() {
        d.repaint();
        return this;
    }

    @Override
    public SDialog dispose() {
        d.dispose();
        return this;
    }
}
