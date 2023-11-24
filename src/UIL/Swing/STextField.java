package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IFont;
import UIL.base.ITextField;
import Utils.RRunnable;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class STextField extends JComponent implements ITextField {
    private static final float AM = SSwing.ANIMATION * 200;

    private IColor bg = Theme.BACKGROUND_COLOR, fg = Theme.FOREGROUND_COLOR;
    private IFont font = Theme.FONT;
    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS;
    private String text;
    private Object hint = null;

    private boolean focused = false, dragging = false;
    private int i = 0, si = -1;
    private float ca = 0, ci = 0, csi = -1, ti = 0, tsi = -1, ox = 0, cx = 0;


    private void setIndex(final int ni) {
        i = ni;
        final Font f = (Font) font.get();
        ti = getFontMetrics(f).stringWidth(text.substring(0, i));
        if (ti >= ox + getWidth() - getHeight() + f.getSize())
            ox = ti - getWidth() + f.getSize() * 2;
        else if (ti < ox)
            ox = Math.max(ti - getWidth() + f.getSize() * 2, 0);
    }

    private void setSelIndex(final int ni) {
        tsi = (si = ni) == -1 ? -1 : getFontMetrics((Font) font.get()).stringWidth(text.substring(0, si));
    }

    private int getIndexByX(final int x) {
        final Font f = (Font) font.get();
        final FontMetrics m = getFontMetrics(f);
        final int o = (getHeight() - f.getSize()) / 2;
        final String t = text;
        if (m.stringWidth(t) + o - ox <= x)
            return t.length();
        else {
            int i = 0;
            for (float r = o - ox; r < x; r += m.stringWidth(t.substring(i, i + 1))) {
                i++;
                if (i == t.length())
                    break;
            }
            return i;
        }
    }

    private final SFPSTimer timer = new SFPSTimer() {
        @Override
        public void run() {
            ca += SSwing.ANIMATION;
            if (cx != ox)
                cx += cx > ox ? -Math.min(cx - ox, AM) : Math.min(ox - cx, AM);
            if (ci != ti)
                ci += ci > ti ? -Math.min(ci - ti, AM) : Math.min(ti - ci, AM);
            if (tsi == -1) {
                if (csi != -1)
                    if (csi != ti)
                        csi += csi > ti ? -Math.min(csi - ti, AM) : Math.min(ti - csi, AM);
                    else
                        csi = -1;
            } else if (csi != tsi) {
                if (csi == -1)
                    csi = ci;
                csi += csi > tsi ? -Math.min(csi - tsi, AM) : Math.min(tsi - csi, AM);
            }
            if (ca >= 2) {
                ca -= 2;
                if (!focused) {
                    stop();
                    ca = 0;
                    csi = tsi;
                    ci = ti;
                    cx = ox;
                }
            }
            repaint();
        }
    };

    public STextField() {
        setOpaque(false);
        setFocusable(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        addMouseListener(new MouseListener() {
            private long l;

            @Override public void mouseClicked(final MouseEvent e) {
                final long c = System.currentTimeMillis();
                if (c - l < 250 && !text.isEmpty()) {
                    setSelIndex(0);
                    setIndex(text.length());
                }
                l = c;
            }

            @Override public void mousePressed(final MouseEvent e) {
                requestFocus();
                if (e.getButton() != MouseEvent.BUTTON1)
                    return;
                dragging = true;
                setIndex(getIndexByX(e.getX()));
                setSelIndex(getIndexByX(e.getX()));
            }

            @Override public void mouseReleased(final MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1)
                    return;
                dragging = false;
                if (si == i)
                    setSelIndex(-1);
            }

            @Override public void mouseEntered(final MouseEvent e) {}
            @Override public void mouseExited(final MouseEvent e) {}
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(final MouseEvent e) {
                if (dragging)
                    setIndex(getIndexByX(e.getX()));
            }
        });
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent e) {
                focused = true;
                timer.start();
            }

            @Override
            public void focusLost(final FocusEvent e) { focused = false; }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                if (e.isControlDown())
                    return;
                switch (e.getKeyChar()) {
                    case KeyEvent.CHAR_UNDEFINED:
                        return;
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_ESCAPE:
                        getParent().requestFocus();
                        break;
                    case KeyEvent.VK_DELETE:
                        if (si > -1) {
                            text = text.substring(0, Math.min(si, i)) + text.substring(Math.max(si, i));
                            setIndex(Math.min(si, i));
                            setSelIndex(-1);
                        } else if (i < text.length())
                            text = text.substring(0, i) + text.substring(i + 1);
                        break;
                    case KeyEvent.VK_BACK_SPACE:
                        if (si > -1) {
                            text = text.substring(0, Math.min(si, i)) + text.substring(Math.max(si, i));
                            if (text.isEmpty()) {
                                cx = 0;
                                ci = 0;
                                csi = tsi = -1;
                            }
                            setIndex(Math.min(si, i));
                            setSelIndex(-1);

                        } else if (i > 0) {
                            text = text.substring(0, i - 1) + text.substring(i);
                            setIndex(i - 1);
                        }
                        break;
                    default:
                        if (si > -1) {
                            text = text.substring(0, Math.min(si, i)) + e.getKeyChar() + text.substring(Math.max(si, i));
                            setIndex(Math.min(si, i) + 1);
                            setSelIndex(-1);
                        } else {
                            text = text.substring(0, i) + e.getKeyChar() + text.substring(i);
                            setIndex(i + 1);
                        }
                        break;
                }
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_HOME:
                    case KeyEvent.VK_UP:
                        if (e.isShiftDown()) {
                            if (si == -1)
                                setSelIndex(i);
                            setIndex(0);
                            if (si == i)
                                setSelIndex(-1);
                            return;
                        }
                        if (si > -1)
                            setSelIndex(-1);
                        else
                            setIndex(0);
                        break;
                    case KeyEvent.VK_END:
                    case KeyEvent.VK_DOWN:
                        if (e.isShiftDown()) {
                            if (si == -1)
                                setSelIndex(i);
                            setIndex(text.length());
                            if (si == i)
                                setSelIndex(-1);
                            return;
                        }
                        if (si > -1)
                            setSelIndex(-1);
                        else
                            setIndex(text.length());
                        break;
                    case KeyEvent.VK_LEFT:
                        if (si > -1 && !e.isShiftDown())
                            setSelIndex(-1);
                        else if (i > 0) {
                            if (e.isShiftDown() && si == -1)
                                setSelIndex(i);
                            setIndex(i - 1);
                            if (si == i)
                                setSelIndex(-1);
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (si > -1 && !e.isShiftDown())
                            setSelIndex(-1);
                        else if (i < text.length()) {
                            if (e.isShiftDown() && si == -1)
                                setSelIndex(i);
                            setIndex(i + 1);
                            if (si == i)
                                setSelIndex(-1);
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (e.isControlDown() && !text.isEmpty()) {
                            setSelIndex(0);
                            setIndex(text.length());
                        }
                        break;
                    case KeyEvent.VK_V:
                        if (e.isControlDown()) {
                            String s;
                            try {
                                s = ((String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor)).replaceAll("\r", "").replaceAll("\n", "");
                            } catch (Exception ignored) {
                                s = "";
                            }
                            if (si > -1) {
                                text = text.substring(0, Math.min(si, i)) + s + text.substring(Math.max(si, i));
                                setIndex(Math.min(si, i) + s.length());
                                //ox = i = Math.min(si, i) + s.length();
                                setSelIndex(-1);
                            } else {
                                text = text.substring(0, i) + s + text.substring(i);
                                setIndex(i + s.length());
                                //ox = i = i + s.length();
                            }
                        }
                        break;
                    case KeyEvent.VK_C:
                        if (e.isControlDown() && si > -1) {
                            final StringSelection sel = new StringSelection(text.substring(Math.min(si, i), Math.max(si, i)));
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
                        }
                        break;
                    case KeyEvent.VK_X:
                        if (e.isControlDown() && si > -1) {
                            final int min = Math.min(si, i), max = Math.max(si, i);
                            final StringSelection sel = new StringSelection(text.substring(min, Math.max(si, i)));
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
                            text = text.substring(0, min) + text.substring(max);
                            setSelIndex(-1);
                            setIndex(min);
                        }
                        break;
                }
            }

            @Override public void keyReleased(final KeyEvent e) {}
        });
        addAncestorListener(new AncestorListener() {
            @Override public void ancestorAdded(final AncestorEvent event) {}

            @Override
            public void ancestorRemoved(final AncestorEvent event) {
                if (event.getComponent() == STextField.this)
                    timer.stop();
            }

            @Override public void ancestorMoved(final AncestorEvent event) {}
        });
    }

    public STextField(final String string) {
        this();
        text = string == null ? "" : string;
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        g.setRenderingHints(SSwing.RH);

        g.setColor((Color) bg.get());
        final int br = borderRadius.run();
        if (br > 0)
            g.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), br, br));
        g.fillRect(0, 0, getWidth(), getHeight());

        final Color tc = (Color) fg.get();
        g.setFont((Font) font.get());
        final FontMetrics metrics = g.getFontMetrics();
        final String t = text;
        final boolean ht = t != null && !t.isEmpty();
        final int c = (getHeight() - metrics.getHeight()) / 2, ox = Math.round(cx), x = c + Math.round(ci) - ox, h = getHeight() - c;
        if (csi > -1) {
            g.setColor((Color) Theme.TEXT_SELECTION_COLOR.get());
            final int x2 = c + Math.round(csi) - ox;
            if (x2 > x)
                g.fillRect(x, c, x2 - x, h - c);
            else
                g.fillRect(x2, c, x - x2, h - c);
        }
        if (ht) {
            g.setColor(tc);
            g.drawString(t, c - ox, c + metrics.getLeading() + metrics.getAscent());
        } else {
            final Object ho = hint;
            final String hint = ho == null ? null : ho.toString();
            if (hint != null && !hint.isEmpty()) {
                g.setColor((Color) Theme.TEXT_HINT_COLOR.get());
                g.drawString(hint, c, c + metrics.getLeading() + metrics.getAscent());
            }
        }
        if (ca > 0) {
            g.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), Math.round(255 * (ca > 1 ? 1 - (ca - 1) : ca))));
            g.drawLine(x, c, x, h);
        }

        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public String text() { return text; }
    @Override public STextField getComponent() { return this; }

    @Override public STextField size(int width, int height) { setSize(width, height); setIndex(i); return this; }
    @Override public STextField pos(int x, int y) { setLocation(x, y); return this; }
    @Override public STextField visible(boolean visible) { setVisible(visible); return this; }
    @Override public STextField focus() { requestFocus(); return this; }

    @Override
    public STextField onInput(final InputListener listener) {
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                if (listener.typed(STextField.this, e.getKeyChar()))
                    e.consume();
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                if (listener.pressed(STextField.this))
                    e.consume();
            }

            @Override
            public void keyReleased(final KeyEvent e) {
                if (listener.released(STextField.this))
                    e.consume();
            }
        });
        return this;
    }

    @Override
    public STextField onAction(final ActionListener actionListener) {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    e.consume();
                    actionListener.run(STextField.this);
                }
            }
        });
        return this;
    }

    @Override
    public STextField hint(final Object hint) {
        this.hint = hint;
        return this;
    }

    @Override
    public STextField borderRadius(final RRunnable<Integer> borderRadius) {
        this.borderRadius = borderRadius;
        return this;
    }

    @Override
    public STextField text(final Object text) {
        this.text = text.toString();
        setIndex(this.text.length());
        return this;
    }

    @Override
    public STextField font(final IFont font) {
        this.font = font;
        return this;
    }

    @Override public STextField ha(final HAlign align) { return this; }

    @Override
    public STextField background(final IColor bg) {
        this.bg = bg;
        return this;
    }

    @Override
    public STextField foreground(final IColor color) {
        fg = color;
        return this;
    }

    @Override
    public ITextField grounds(final IColor bg, final IColor fg) {
        this.bg = bg;
        this.fg = fg;
        return this;
    }

    @Override
    public ITextField update() {
        repaint();
        return this;
    }
}