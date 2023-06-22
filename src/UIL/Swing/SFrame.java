package UIL.Swing;

import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IFrame;
import UIL.base.IImage;
import Utils.Runnable1arg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SFrame implements IFrame {
    private boolean packed = false;
    private final JFrame frame;
    private final Container root;

    public SFrame(final String title) {
        frame = new JFrame(title);
        root = frame.getContentPane();
        root.setLayout(null);
    }

    @Override public int width() { return root.getWidth(); }
    @Override public int height() { return root.getHeight(); }
    @Override public boolean visible() { return frame.isVisible(); }
    @Override public boolean isFocused() { return frame.hasFocus(); }
    @Override public IComponent[] childs() { return (IComponent[]) root.getComponents(); }
    @Override public JFrame getComponent() { return frame; }

    @Override
    public SFrame size(final int width, final int height) {
        root.setPreferredSize(new Dimension(width, height));
        final Insets insets = frame.getInsets();
        frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
        return this;
    }

    @Override public SFrame pos(final int x, final int y) { frame.setLocation(x, y); return this; }

    @Override
    public SFrame pack() {
        if (!packed) {
            packed = true;
            frame.pack();
        }
        return this;
    }

    @Override public SFrame center(final IComponent component) { frame.setLocationRelativeTo(component == null ? null : (Component) component.getComponent()); return this; }
    @Override public SFrame visible(final boolean visible) {
        if (!packed) {
            packed = true;
            frame.pack();
        }
        frame.setVisible(visible);
        return this;
    }
    @Override public SFrame focus() { frame.requestFocus(); return this; }

    @Override
    public IFrame onClose(final Runnable1arg<IFrame> listener) {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent windowEvent) {
                listener.run(SFrame.this);
            }
        });
        return this;
    }

    @Override
    public SFrame add(final IComponent component) {
        root.add((Component) component.getComponent(), 0);
        return this;
    }

    @Override
    public SFrame add(final IComponent... components) {
        for (final IComponent c : components)
            root.add((Component) c.getComponent(), 0);
        return this;
    }

    @Override
    public SFrame remove(final IComponent component) {
        root.remove((Component) component);
        return this;
    }

    @Override
    public SFrame clear() {
        root.removeAll();
        return this;
    }

    @Override
    public SFrame icon(final IImage icon) {
        frame.setIconImage((Image) icon.getImage());
        return this;
    }

    @Override
    public SFrame resizable(final boolean resizable) {
        frame.setResizable(resizable);
        return this;
    }

    @Override
    public SFrame background(final IColor bg) {
        frame.setBackground((Color) bg.get());
        return this;
    }

    @Override
    public SFrame update() {
        frame.repaint();
        return this;
    }

    @Override
    public SFrame dispose() {
        frame.dispose();
        return this;
    }
}
