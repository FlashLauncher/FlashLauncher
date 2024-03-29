package UIL.Swing;

import UIL.*;
import UIL.base.*;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private final ConcurrentLinkedQueue<OnListListener> actions = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<CloseListListener> closeListListeners = new ConcurrentLinkedQueue<>();

    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS, imageOffset = UI.ZERO, imageTextDist = imageOffset;
    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;
    private IFont font = Theme.FONT;

    private float a = 0;

    private Object text = null;
    private IImage img = null;
    private boolean smooth = true;

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
        private final Object lc = new Object();
        private boolean closed = false;

        private IContainer p;

        private final ContainerListener containerListener = new ContainerListener() {
            @Override public void componentAdded(ContainerEvent e) {}

            @Override
            public void componentRemoved(ContainerEvent e) {
                if (e.getChild() == CBP.this)
                    close();
            }
        },  cl = new ContainerListener() {
            @Override public void componentAdded(final ContainerEvent e) { scan(e.getChild()); }

            @Override
            public void componentRemoved(final ContainerEvent e) {
                if (e.getChild() instanceof Container)
                    ((Container) e.getChild()).removeContainerListener(cl);
                e.getChild().removeFocusListener(fa);
            }
        };

        private final FocusAdapter fa = new FocusAdapter() {
            @Override public void focusLost(final FocusEvent e) {
                if (CBP.this == e.getOppositeComponent() || containsComponent(CBP.this, e.getOppositeComponent()))
                    return;
                close();
            }
        };

        public void close() {
            synchronized (lc) {
                if (closed)
                    return;
                closed = true;
            }
            ((Container) p.remove(CBP.this).update()).removeContainerListener(containerListener);
            SComboBox.this.content = null;
            animator.start();
            closeListListeners.removeIf(l -> l.run(new ListEvent<CloseListListener>() {
                @Override public CloseListListener getSelfListener() { return l; }
                @Override public IComboBox getSelf() { return SComboBox.this; }
                @Override public IContainer getContainer() { return CBP.this; }
                @Override public boolean isClosed() { return true; }
                @Override public void close() {}
            }));
        }

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
        IContainer c = null, cu;
        if (actions.isEmpty())
            return;
        for (final OnListListener al : actions)
            if ((cu = al.run(new ListEvent<OnListListener>() {
                @Override public OnListListener getSelfListener() { return al; }
                @Override public IComboBox getSelf() { return SComboBox.this; }
                @Override public IContainer getContainer() { return sp; }
                @Override public boolean isClosed() { return sp.closed; }
                @Override public void close() { sp.close(); }
            })) != null)
                c = cu;
        if (c == null)
            return;
        requestFocus();
        sp.p = c.add(sp).update();
        ((Container) c).addContainerListener(sp.containerListener);
        (content = sp).requestFocus();
        animator.start();
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        g.setRenderingHints(SSwing.RH);
        g.setFont((Font) font.get());

        final FontMetrics m = g.getFontMetrics();
        final int br = borderRadius.run(), w = getWidth(), h = getHeight(), fh = m.getHeight(), oi = imageOffset.run(), ot = imageTextDist.run(),
                rb = w - h, xc = rb + h / 2, o = h / 3, hc = h / 5, ho = (h - hc) / 2 + Math.round(a * hc), y1 = h - ho;

        final Object to = text;
        final String t = to == null ? null : to.toString();

        final IImage io = img;
        final Image i = io == null ? null : (Image) io.getImage();

        final Area area = br > 0 ? new Area(new RoundRectangle2D.Double(0, 0, w, h, br, br)) : new Area();
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
            if (t != null && !t.isEmpty())
                g.drawString(t, (h - fh) / 2 + ot, (h - fh) / 2 + m.getLeading() + m.getAscent());
        } else {
            final int is = h - oi * 2;
            if (!smooth)
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(i, oi, oi, is, is, this);
            if (t != null && !t.isEmpty()) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawString(t, h + ot, (h - fh) / 2 + m.getLeading() + m.getAscent());
            }
        }

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public String text() { return text.toString(); }
    @Override public Object getComponent() { return this; }

    @Override public SComboBox image(final IImage image) { img = image; return this; }
    @Override public SComboBox text(final Object text) { this.text = text; return this; }
    @Override public SComboBox font(final IFont font) { this.font = font; return this; }
    @Override public SComboBox ha(final HAlign align) { return this; }
    @Override public SComboBox size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SComboBox pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SComboBox visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SComboBox focus() { requestFocus(); return this; }
    @Override public SComboBox borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }
    @Override public SComboBox imageTextDist(final int imageTextDist) { this.imageTextDist = () -> imageTextDist; return this; }
    @Override public SComboBox imageOffset(final int imageOffset) { this.imageOffset = () -> imageOffset; return this; }
    @Override public SComboBox smooth(final boolean value) { smooth = value; return this; }
    @Override public SComboBox background(final IColor bg) { this.bg = bg; return this; }
    @Override public SComboBox foreground(final IColor fg) { this.fg = fg; return this; }
    @Override public SComboBox grounds(final IColor bg, final IColor fg) { this.bg = bg; this.fg = fg; return this; }
    @Override public SComboBox onList(final OnListListener listener) { actions.add(listener); return this; }
    @Override public SComboBox onCloseList(final CloseListListener listener) { closeListListeners.add(listener); return this; }
    @Override public SComboBox offCloseList(final CloseListListener listener) { closeListListeners.remove(listener); return this; }
    @Override public SComboBox update() { repaint(); return this; }
}
