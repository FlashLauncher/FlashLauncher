package UIL.Swing;

import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IFrame;
import UIL.base.IImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SFrame implements IFrame {
    private boolean packed = false;
    private final JFrame frame;
    private final Container root;

    public SFrame(String title) {
        frame = new JFrame(title);
        root = frame.getContentPane();
        root.setLayout(null);
    }

    @Override public int width() { return root.getWidth(); }
    @Override public int height() { return root.getHeight(); }
    @Override public boolean visible() { return frame.isVisible(); }
    @Override public boolean isFocused() { return frame.hasFocus(); }

    @Override
    public SFrame size(final int width, final int height) {
        root.setPreferredSize(new Dimension(width, height));
        final Insets insets = frame.getInsets();
        frame.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
        return this;
    }

    @Override public SFrame pos(final int x, final int y) { frame.setLocation(x, y); return this; }
    @Override public SFrame center(IComponent component) { frame.setLocationRelativeTo(component == null ? null : (Component) component.getComponent()); return this; }
    @Override public SFrame visible(boolean visible) {
        if (!packed) {
            packed = true;
            frame.pack();
        }
        frame.setVisible(visible);
        return this;
    }
    @Override public SFrame focus() { frame.requestFocus(); return this; }

    @Override
    public SFrame on(String name, Runnable runnable) {
        if (name.equals("close"))
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    runnable.run();
                }
            });
        return this;
    }

    @Override
    public JFrame getComponent() { return frame; }

    @Override
    public SFrame add(final IComponent component) {
        root.add((Component) component.getComponent(), 0);
        frame.repaint();
        return this;
    }

    @Override
    public SFrame remove(final IComponent component) {
        root.remove((Component) component);
        frame.repaint();
        return this;
    }

    @Override
    public IComponent[] childs() {
        return (IComponent[]) root.getComponents();
    }

    @Override
    public SFrame clear() {
        root.removeAll();
        frame.repaint();
        return this;
    }

    @Override
    public SFrame icon(IImage icon) {
        frame.setIconImage((Image) icon.getImage());
        return this;
    }

    @Override
    public SFrame resizable(boolean resizable) {
        frame.setResizable(resizable);
        return this;
    }

    @Override
    public SFrame background(IColor bg) {
        frame.setBackground((Color) bg.get());
        return this;
    }

    @Override
    public SFrame dispose() {
        frame.dispose();
        return this;
    }
}
