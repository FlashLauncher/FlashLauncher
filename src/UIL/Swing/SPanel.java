package UIL.Swing;

import UIL.*;
import UIL.base.IColor;
import UIL.base.IComponent;
import UIL.base.IContainer;
import Utils.Core;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SPanel extends JPanel implements IContainer {
    private RRunnable<Integer> borderRadius = Theme.BORDER_RADIUS;
    private IColor bg = Theme.BACKGROUND_COLOR;

    private RoundRectangle2D.Double area = null;

    public SPanel() { setOpaque(false); setLayout(null); }

    private long nano;

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = new SGraphics2D((Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create()));
        g.setRenderingHints(SSwing.RH);

        final int br = borderRadius.run(), cw = getWidth(), ch = getHeight();
        area = br > 0 ? new RoundRectangle2D.Double(0, 0, cw, ch, br, br) : null;
        g.setClip(area);
        g.setColor((Color) bg.get());
        g.fillRect(0, 0, cw, ch);

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override
    protected void paintChildren(final Graphics graphics) {
        final Graphics2D g = new SGraphics2D((Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create()));
        g.setRenderingHints(SSwing.RH);
        if (area != null)
            g.setClip(area);

        super.paintChildren(g);

        if (!(graphics instanceof Graphics2D))
            g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
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

    @Override public SPanel remove(final IComponent component) { super.remove((Component) component.getComponent()); return this; }
    @Override public SPanel clear() { super.removeAll(); return this; }
    @Override public SPanel background(final IColor bg) { this.bg = bg; return this; }
    @Override public SPanel borderRadius(final RRunnable<Integer> borderRadius) { this.borderRadius = borderRadius; return this; }

    private static class STE implements TransferEvent {
        private final TransferHandler.TransferSupport s;
        private static final DataFlavor nixFL;

        static {
            DataFlavor r = null;
            if (Core.IS_LINUX)
                try {
                    r = new DataFlavor("text/uri-list;class=java.lang.String");
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
            nixFL = r;
        }

        public STE(final TransferHandler.TransferSupport support) {
            s = support;
        }

        @Override public boolean isDrop() { return s.isDrop(); }

        @Override public boolean hasStringSupport() { return s.isDataFlavorSupported(DataFlavor.stringFlavor); }

        @Override
        public boolean hasFileListSupport() {
            return s.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || (nixFL != null && s.isDataFlavorSupported(nixFL));
        }

        @Override
        public String getString() throws Exception {
            return (String) s.getTransferable().getTransferData(DataFlavor.stringFlavor);
        }

        @Override
        public List<File> getFileList() throws Exception {
            if (s.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
                return (List<File>) s.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            if (nixFL != null) {
                final ArrayList<File> files = new ArrayList<>();
                final String data = (String) s.getTransferable().getTransferData(nixFL);
                for (final StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens(); ) {
                    final String token = st.nextToken().trim();
                    if (token.startsWith("#") || token.isEmpty())
                        continue;
                    try {
                        files.add(new File(new URI(token)));
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return files;
            }
            throw new Exception("Not implemented");
        }
    }

    /**
     * @since FlashLauncher 0.2.4
     */
    public SPanel onTransfer(final TransferListener listener) {
        setTransferHandler(new TransferHandler() {
            @Override public boolean canImport(final TransferSupport support) { return listener.canImport(new STE(support)); }
            @Override public boolean importData(final TransferSupport support) { return listener.onImport(new STE(support)); }
        });
        return this;
    }

    @Override
    public SPanel update() {
        final boolean o = bg.alpha() == 255 && borderRadius.run() <= 0;
        if (isOpaque() != o)
            setOpaque(o);
        repaint();
        return this;
    }
}
