package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IContainer;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class SPanel extends JPanel implements IContainer {
    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND_COLOR;

    public SPanel() { setOpaque(false); setLayout(null); }

    @Override protected void paintComponent(final Graphics g) {}

    @Override
    protected void paintChildren(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);

        final int br = borderRadius.run();
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));

        g.setColor((Color) bg.get());
        g.fillRect(0, 0, getWidth(), getHeight());

        final BufferedImage img = g.getDeviceConfiguration().createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);
        final Graphics gi = img.createGraphics();
        super.paintChildren(gi);
        gi.dispose();
        g.drawImage(img, 0, 0, this);

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public int borderRadius() { return borderRadius.run(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override
    public SPanel size(final int width, final int height) {
        setPreferredSize(new Dimension(width, height));
        setSize(width, height);
        return this;
    }
    @Override public SPanel pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SPanel visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SPanel focus() { requestFocus(); return this; }
    @Override
    public IComponent[] childs() {
        final ArrayList<IComponent> r = new ArrayList<>();
        for (final Component c : getComponents())
            if (c instanceof IComponent)
                r.add((IComponent) c);
        return r.toArray(new IComponent[0]);
    }
    @Override public SPanel getComponent() { return this; }

    @Override
    public SPanel add(final IComponent component) {
        super.add((Component) component.getComponent(), 0);
        return this;
    }

    @Override
    public SPanel add(final IComponent... components) {
        for (final IComponent c : components)
            super.add((Component) c.getComponent(), 0);
        return this;
    }

    @Override
    public SPanel remove(final IComponent component) {
        super.remove((Component) component.getComponent());
        return this;
    }



    @Override
    public SPanel clear() {
        super.removeAll();
        return this;
    }

    @Override
    public SPanel background(final IColor bg) {
        this.bg = bg;
        return this;
    }



    @Override
    public SPanel borderRadius(final RRunnable<Integer> borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    @Override
    public SPanel borderRadius(final int borderRadius) {
        this.borderRadius = () -> borderRadius;
        return this;
    }

    @Override
    public SPanel update() {
        repaint();
        return this;
    }
}
