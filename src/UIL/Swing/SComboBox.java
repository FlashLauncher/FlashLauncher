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
    private final SFPSTimer animator = new SFPSTimer() {
        @Override
        public void run() {
            if (content == null) {
                a = Math.max(a - d, 0);
                repaint();
                if (a == 0)
                    stop();
            } else {
                a = Math.min(a + d, 1);
                repaint();
                if (a == 1)
                    stop();
            }
        }
    };
    private final ArrayList<OnListListener> actions = new ArrayList<>();
    private final ArrayList<CloseListListener> closeListListeners = new ArrayList<>();

    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS, imageOffset = UI.ZERO, imageTextDist = imageOffset;
    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;
    private IFont font = Theme.FONT;

    private float a = 0;

    private Object text = null;
    private IImage img = null;

    private SPanel content = null;

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

    private final class CBP extends SPanel {
        private IContainer p;

        private final FocusAdapter fa = new FocusAdapter() {
            @Override public void focusLost(final FocusEvent e) {
                if (CBP.this == e.getOppositeComponent() || containsComponent(CBP.this, e.getOppositeComponent()))
                    return;
                p.remove(CBP.this).update();
                SComboBox.this.content = null;
                animator.start();
                final CloseListListener[] listeners;
                synchronized (closeListListeners) { listeners = closeListListeners.toArray(new CloseListListener[0]); }
                for (final CloseListListener l : listeners)
                    try {
                        l.run(SComboBox.this, CBP.this, l);
                    } catch (final Throwable ex) {
                        ex.printStackTrace();
                    }
            }
        };

        private final ContainerListener cl = new ContainerListener() {
            @Override
            public void componentAdded(final ContainerEvent e) {
                scan(e.getChild());
            }

            @Override
            public void componentRemoved(final ContainerEvent e) {
                if (e.getChild() instanceof Container)
                    ((Container) e.getChild()).removeContainerListener(cl);
                e.getChild().removeFocusListener(fa);
            }
        };

        private void scan(final Component co) {
            co.addFocusListener(fa);
            if (co instanceof Container) {
                ((Container) co).addContainerListener(cl);
                for (final Component c : ((Container) co).getComponents())
                    scan(c);
            }
        }

        public CBP() {
            super();
            addFocusListener(fa);
            addContainerListener(cl);
        }
    }

    private static boolean containsComponent(final JComponent container, final Component component) {
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
        final CBP sp = new CBP();
        sp.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    SComboBox.this.requestFocus();
            }
        });
        IContainer c = null;
        synchronized (actions) {
            if (actions.size() == 0)
                return;
            IContainer cu;
            for (final OnListListener al : actions)
                if ((cu = al.run(SComboBox.this, sp)) != null)
                    c = cu;
            if (c == null)
                return;
        }
        requestFocus();
        sp.p = c.add(sp).update();
        (content = sp).requestFocus();
        animator.start();
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
        final int rb = w - h, xc = rb + h / 2, o = h / 3, hc = h / 5, ho = (h - hc) / 2 + Math.round(a * hc), y1 = h - ho;
        g.setClip(area);
        g.setColor((Color) bg.get());
        g.fillRect(0, 0, w, h);
        g.setColor((Color) fg.get());

        g.drawLine(rb, 4, rb, h - 4);
        g.drawLine(rb + o, ho, xc, y1);
        g.drawLine(xc, y1, w - o, ho);

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
    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public int borderRadius() { return borderRadius.run(); }

    @Override
    public SComboBox image(final IImage image) {
        img = image;
        return this;
    }

    @Override public String text() { return text.toString(); }

    @Override
    public SComboBox text(final Object text) {
        this.text = text;
        return this;
    }

    @Override
    public SComboBox font(final IFont font) {
        this.font = font;
        return this;
    }

    @Override
    public SComboBox ha(final HAlign align) {
        return this;
    }

    @Override public SComboBox size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SComboBox pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SComboBox visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SComboBox focus() { requestFocus(); return this; }

    @Override
    public SComboBox borderRadius(final RRunnable<Integer> borderRadius) {
        this.borderRadius = borderRadius;
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
        return this;
    }

    @Override
    public IComboBox imageOffset(final int imageOffset) {
        this.imageOffset = () -> imageOffset;
        return this;
    }

    @Override
    public SComboBox background(final IColor bg) {
        this.bg = bg;
        return this;
    }

    @Override
    public SComboBox foreground(final IColor fg) {
        this.fg = fg;
        return this;
    }

    @Override public SComboBox grounds(final IColor bg, final IColor fg) {
        this.bg = bg;
        this.fg = fg;
        return this;
    }

    @Override
    public SComboBox onList(final OnListListener listener) {
        synchronized (actions) { actions.add(listener); }
        return this;
    }

    @Override
    public IComboBox onCloseList(final CloseListListener listener) {
        synchronized (closeListListeners) {
            closeListListeners.add(listener);
        }
        return this;
    }

    @Override
    public IComboBox offCloseList(final CloseListListener listener) {
        synchronized (closeListListeners) {
            closeListListeners.remove(listener);
        }
        return this;
    }

    @Override
    public SComboBox update() {
        repaint();
        return this;
    }

    @Override public Object getComponent() { return this; }
}
