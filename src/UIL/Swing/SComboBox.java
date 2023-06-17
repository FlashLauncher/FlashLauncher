package UIL.Swing;

import UIL.*;
import UIL.base.*;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

public class SComboBox extends JComponent implements IComboBox {
    private static final float d = SSwing.DELTA / 300;
    private final STimer animator = new STimer(SSwing.DELTA) {
        @Override
        public void run() {
            if (content == null) {
                a = Math.max(a - d, 0);
                repaint();
                if (a == 0) stop();
            } else {
                a = Math.min(a + d, 1);
                repaint();
                if (a == 1) stop();
            }
        }
    };
    private final ArrayList<ListListener> actions = new ArrayList<>();

    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS, imageOffset = UI.ZERO, imageTextDist = imageOffset;
    private IColor bg = Theme.BACKGROUND, fg = Theme.FOREGROUND;
    private IFont font = Theme.FONT;
    private HAlign ha = HAlign.LEFT;

    private float a = 0;

    private Object text = null;
    private IImage img = null;

    private SScrollPane content = null;

    public SComboBox() {
        setOpaque(false);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                    showMenu();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(final MouseEvent e) {
                if (content != null) {
                    requestFocus();
                    return;
                }
                showMenu();
            }
        });
    }

    private class SCBSP extends SScrollPane {
        private JRootPane r;
        private Component f;

        private final FocusAdapter fa = new FocusAdapter() {
            @Override public void focusLost(final FocusEvent e) {
                if (SCBSP.this == e.getOppositeComponent() || containsComponent(SCBSP.this, e.getOppositeComponent())) return;
                r.remove(SCBSP.this);
                SComboBox.this.content = null;
                if (f != null) f.repaint();
                animator.start();
            }
        };

        private final ContainerListener cl = new ContainerListener() {
            @Override
            public void componentAdded(ContainerEvent e) {
                if (e.getChild() instanceof Container)
                    ((Container) e.getChild()).addContainerListener(cl);
                e.getChild().addFocusListener(fa);
            }

            @Override
            public void componentRemoved(ContainerEvent e) {
                if (e.getChild() instanceof Container)
                    ((Container) e.getChild()).removeContainerListener(cl);
                e.getChild().removeFocusListener(fa);
            }
        };

        public SCBSP() {
            super();
            addFocusListener(fa);
            addContainerListener(cl);
        }
    }

    private static boolean containsComponent(JComponent container, Component component) {
        final Component[] cl = container.getComponents();
        for (final Component c : cl)
            if (c == component)
                return true;
        for (final Component c : cl)
            if (containsComponent((JComponent) c, component))
                return true;
        return false;
    }

    private void showMenu() {
        final SCBSP sp = new SCBSP();
        sp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    SComboBox.this.requestFocus();
            }
        });
        synchronized (actions) {
            for (final ListListener al : actions)
                if (!al.run(SComboBox.this, sp))
                    return;
        }
        Component p = getParent();
        while (p != null) {
            if (p instanceof JRootPane) {
                requestFocus();
                sp.r = (JRootPane) p;
                sp.f = p.getParent();
                sp.r.add(sp, 0);
                if (sp.f != null) sp.f.repaint();
                (content = sp).requestFocus();
                animator.start();
                break;
            }
            p = p.getParent();
        }
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);
        g.setFont((Font) font.get());
        final FontMetrics m = g.getFontMetrics();
        final String t;
        final Image i;
        final int w = getWidth(), h = getHeight(), fh = m.getHeight(), oi, ot;
        final Area area;
        {
            final Object to = text;
            t = to == null ? null : to.toString();

            final IImage io = img;
            i = io == null ? null : (Image) io.getImage();

            final int br = borderRadius.run();
            area = br > 0 ? new Area(new RoundRectangle2D.Double(0, 0, w, h, br, br)) : new Area();

            oi = imageOffset.run();
            ot = imageTextDist.run();
        }
        final int rb = w - h, asw = h - 20, ash = asw / 2, v = Math.round(a * ash), aoy = 14, ax1 = rb + 10, ax = ax1 + asw / 2, ay1 = aoy + v, ay2 = aoy + ash - v;
        g.setClip(area);
        g.setColor((Color) bg.get());
        g.fillRect(0, 0, w, h);
        g.setColor((Color) fg.get());

        g.drawLine(rb, 4, rb, h - 4);
        g.drawLine(ax1, ay1, ax, ay2);
        g.drawLine(ax, ay2, ax1 + asw, ay1);

        area.subtract(new Area(new Rectangle(rb, 0, h, h)));
        g.setClip(area);

        if (i == null) {
            if (t != null && t.length() > 0)
                g.drawString(t, (h - fh) / 2 + ot, (h - fh) / 2 + m.getLeading() + m.getAscent());
        } else {
            final int is = h - oi * 2;
            g.drawImage(i, oi, oi, is, is, this);
            if (t != null && t.length() > 0)
                g.drawString(t, h + ot, (h - fh) / 2 + m.getLeading() + m.getAscent());
        }

        g.dispose();
    }

    @Override public int borderRadius() { return borderRadius.run(); }

    @Override
    public SComboBox image(IImage image) {
        img = image;
        repaint();
        return this;
    }

    @Override public String text() { return text.toString(); }

    @Override
    public SComboBox text(Object text) {
        this.text = text;
        repaint();
        return this;
    }

    @Override
    public SComboBox font(IFont font) {
        this.font = font;
        repaint();
        return this;
    }

    @Override
    public SComboBox ha(HAlign align) {
        ha = align;
        repaint();
        return this;
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SComboBox size(int width, int height) { setSize(width, height); return this; }
    @Override public SComboBox pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SComboBox visible(boolean visible) { setVisible(visible); return this; }
    @Override public SComboBox focus() { requestFocus(); return this; }

    @Override
    public SComboBox borderRadius(final RRunnable<Integer> borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
        return this;
    }

    @Override
    public SComboBox imageTextDist(final int imageTextDist) {
        this.imageTextDist = () -> imageTextDist;
        return this;
    }

    @Override
    public SComboBox borderRadius(final int borderRadius) {
        this.borderRadius = () -> borderRadius;
        repaint();
        return this;
    }

    @Override
    public IComboBox imageOffset(final int imageOffset) {
        this.imageOffset = () -> imageOffset;
        repaint();
        return this;
    }

    @Override
    public SComboBox background(IColor bg) {
        this.bg = bg;
        repaint();
        return this;
    }

    @Override
    public SComboBox foreground(IColor fg) {
        this.fg = fg;
        repaint();
        return this;
    }

    @Override public SComboBox grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }

    @Override public SComboBox on(String name, Runnable runnable) { return this; }
    @Override
    public SComboBox onList(final ListListener listener) {
        synchronized (actions) { actions.add(listener); }
        return this;
    }

    @Override public Object getComponent() { return this; }
}
