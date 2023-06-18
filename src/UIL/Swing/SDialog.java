package UIL.Swing;

import UIL.base.*;
import Utils.Runnable1arg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SDialog implements IDialog {
    final JDialog d;
    private final Container cont;
    private boolean packed = false;

    public SDialog(IFrame owner, String title) {
        d = new JDialog(owner == null ? null : (JFrame) owner.getComponent(), title);
        d.setModal(true);
        cont = d.getContentPane();
        cont.setLayout(null);
    }

    @Override public int width() { return cont.getWidth(); }
    @Override public int height() { return cont.getHeight(); }
    @Override public boolean visible() { return d.isVisible(); }
    @Override public boolean isFocused() { return d.isFocused(); }

    @Override public Object getComponent() { return this; }

    @Override
    public SDialog icon(IImage icon) {
        d.setIconImage((Image) icon.getImage());
        return this;
    }

    @Override
    public SDialog resizable(boolean resizable) {
        d.setResizable(resizable);
        return this;
    }

    @Override public SDialog icon(String path) throws Exception { IDialog.super.icon(path); return this; }

    @Override
    public SDialog add(IComponent component) {
        cont.add((Component) component.getComponent(), 0);
        return this;
    }

    @Override
    public SDialog remove(IComponent component) {
        cont.remove((Component) component.getComponent());
        return this;
    }

    @Override
    public IComponent[] childs() {
        return (IComponent[]) cont.getComponents();
    }

    @Override
    public SDialog clear() {
        cont.removeAll();
        return this;
    }

    @Override
    public SDialog size(int width, int height) {
        //if (isUndecorated())
            cont.setPreferredSize(new Dimension(width, height));
        d.setSize(width + (d.getInsets()).left + (d.getInsets()).right, height + (d.getInsets()).top + (d.getInsets()).bottom);
        return this;
    }

    @Override public SDialog pos(int x, int y) { d.setLocation(x, y); return this; }
    @Override public SDialog center(IComponent component) { d.setLocationRelativeTo(component == null ? null : (Component) component.getComponent()); return this; }
    @Override public SDialog visible(boolean visible) {
        if (!packed) {
            packed = true;
            d.pack();
        }
        d.setVisible(visible);
        return this;
    }
    @Override public SDialog focus() { d.requestFocus(); return this; }

    @Override
    public SDialog background(IColor bg) {
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
    public SDialog dispose() {
        d.dispose();
        return this;
    }
}
