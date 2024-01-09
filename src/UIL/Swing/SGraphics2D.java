package UIL.Swing;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class SGraphics2D extends Graphics2D {
    private final Shape r;
    public final Graphics2D graphics;

    public SGraphics2D(final Graphics2D g) {
        graphics = (Graphics2D) g.create();
        r = g.getClip();
    }

    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        graphics.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
        if (r == null) {
            graphics.setClip(x, y, width, height);
            return;
        }
        final Area a = new Area(r);
        a.intersect(new Area(new Rectangle(x, y, width, height)));
        graphics.setClip(a);
    }

    @Override
    public void setClip(final Shape clip) {
        if (clip == null) {
            graphics.setClip(r);
            return;
        }
        if (r == null) {
            graphics.setClip(clip);
            return;
        }

        final Rectangle
                rb = r.getBounds(),
                cb = clip.getBounds();

        if (rb.getWidth() == 0 || rb.getHeight() == 0) {
            graphics.setClip(r);
            return;
        } else if (cb.getWidth() == 0 || cb.getHeight() == 0) {
            graphics.setClip(clip);
            return;
        }

        if (rb.equals(cb))
            if (r instanceof RoundRectangle2D) {
                if (clip instanceof RoundRectangle2D)
                    if (
                            ((RoundRectangle2D) r).getArcWidth() == ((RoundRectangle2D) clip).getArcWidth() &&
                                    ((RoundRectangle2D) r).getArcHeight() == ((RoundRectangle2D) clip).getArcHeight()
                    )
                        graphics.setClip(r);
                    else
                        graphics.setClip(new RoundRectangle2D.Double(
                                Math.max(((RoundRectangle2D) r).getX(), ((RoundRectangle2D) clip).getX()),
                                Math.max(((RoundRectangle2D) r).getY(), ((RoundRectangle2D) clip).getY()),

                                Math.max(((RoundRectangle2D) r).getWidth(), ((RoundRectangle2D) clip).getWidth()),
                                Math.max(((RoundRectangle2D) r).getHeight(), ((RoundRectangle2D) clip).getHeight()),

                                Math.max(((RoundRectangle2D) r).getArcWidth(), ((RoundRectangle2D) clip).getArcWidth()),
                                Math.max(((RoundRectangle2D) r).getArcHeight(), ((RoundRectangle2D) clip).getArcHeight())
                        ));
                else
                    graphics.setClip(r);
                return;
            } else if (clip instanceof RoundRectangle2D) {
                graphics.setClip(clip);
                return;
            }

        if (r instanceof Rectangle && clip instanceof Rectangle) {
            if (rb.equals(cb)) {
                graphics.setClip(r);
                return;
            }
            System.out.println(r + " vs " + clip);
        }

        /*if (r instanceof Rectangle && clip instanceof RoundRectangle2D) {
            final double
                    arcW = ((RoundRectangle2D) clip).getArcWidth(),
                    arcH = ((RoundRectangle2D) clip).getArcHeight(),
                    cx = ((RoundRectangle2D) clip).getX(),
                    cy = ((RoundRectangle2D) clip).getY(),
                    cw = ((RoundRectangle2D) clip).getWidth(),
                    ch = ((RoundRectangle2D) clip).getHeight(),
                    x = ((Rectangle) r).getX(),
                    y = ((Rectangle) r).getY(),
                    w = ((Rectangle) r).getWidth(),
                    h = ((Rectangle) r).getHeight();



            if (x >= arcW + cx && w <= cw - arcW)
                if (y >= arcH + cy && h <= ch - arcH) {
                    graphics.setClip(r);
                    return;
                } else {
                    graphics.setClip(new RoundRectangle2D.Double(
                            x, Math.min(y, cy),
                            w, Math.max(h, ch),
                            0, 0
                    ));
                    return;
                }

            if (y >= arcH + cy && h <= ch - arcH) {
                // java.awt.Rectangle[x=0,y=156,width=128,height=4] vs java.awt.Rectangle[x=0,y=0,width=128,height=160], 16.0, 16.0
                // 0 156 128   4 |
                // 0   0 128 160 | 16 16

                graphics.setClip(new RoundRectangle2D.Double(
                        Math.min(x, cx), y,
                        Math.max(w, cw), h,
                        0, 0
                ));
                return;

                graphics.setClip(new RoundRectangle2D.Double(
                        Math.max(x, cx), cy,
                        cw, Math.min(h, ch),
                        arcW, 0
                ));
                return;
            }

            // 0 0 680 60 / 0 0 680 72 , 16 , 16
            // 8 220 632 32 / 0 0 648 308 16 16

            //System.out.println(r + " vs " + clip.getBounds() + ", " + ((RoundRectangle2D) clip).getArcWidth() + ", " + ((RoundRectangle2D) clip).getArcHeight());
        }*/

        if (r instanceof Rectangle && clip instanceof RoundRectangle2D) {
            final double
                    x = ((Rectangle) r).getX(),
                    y = ((Rectangle) r).getY(),
                    w = ((Rectangle) r).getWidth(),
                    h = ((Rectangle) r).getHeight(),
                    cx = ((RoundRectangle2D) clip).getX(),
                    cy = ((RoundRectangle2D) clip).getY(),
                    cw = ((RoundRectangle2D) clip).getWidth(),
                    ch = ((RoundRectangle2D) clip).getHeight(),
                    aw = ((RoundRectangle2D) clip).getArcWidth(),
                    ah = ((RoundRectangle2D) clip).getArcHeight(),
                    awh = aw / 2,
                    ahh = ah / 2;

            if (x == cx + aw && x + w == cx + cw - aw) {
                graphics.setClip(new Rectangle(
                        (int) Math.round(x), (int) Math.round(y),
                        (int) Math.round(w), (int) Math.round(h)
                ));
                return;
            }

            if (x <= cx && y <= cy && cw <= w && ch <= h) {
                graphics.setClip(clip);
                return;
            }

            if (
                    x >= cx + aw && y >= cy + ah &&
                            w <= cw - aw && h <= ch - ah
            ) {
                graphics.setClip(r);
                return;
            }

            if (
                    x >= cx + awh && y >= cy + ahh &&
                            w <= cw - awh && h <= ch - awh
            ) {
                graphics.setClip(r);
                return;
            }

            if (
                x     >= cx      && y     >= cy      + ah &&
                x + w <= cx + cw && y + h <= cy + ch - ah
            ) {
                graphics.setClip(r);
                return;
            }

            final double
                    sx = Math.max(x, cx), sy = Math.max(y, cy),
                    ox = Math.max(x, cx + aw), oy = Math.max(y, cy + ah),
                    ew = Math.min(x + w, cx + cw), eh = Math.min(y + h, cy + ch),
                    ow = Math.min(x + w, cx + cw - aw), oh = Math.min(y + h, cy + ch - ah)
            ;

            /*graphics.setClip(new Path2D.Double() {{
                if (sx == ox || sy == oy)
                    moveTo(sx, sy);
                else {
                    moveTo(sx, oy);
                    curveTo(sx, sy, sx, sy, ox, sy);
                }

                if (ew == ow || sy == oy)
                    lineTo(ew, sy);
                else {
                    lineTo(ow, sy);
                    curveTo(ew, sy, ew, sy, ew, oy);
                }

                if (ew == ow || eh == oh)
                    lineTo(ew, eh);
                else {
                    lineTo(ew, oh);
                    curveTo(ew, eh, ew, eh, ow, eh);
                }

                if (sx == ox || eh == oh)
                    lineTo(sx, eh);
                else {
                    lineTo(ox, eh);
                    curveTo(sx, eh, sx, eh, sx, oh);
                }

                lineTo(sx, oy);

                closePath();
            }});
            return;*/

            graphics.setClip(new Path2D.Double() {{
                if (eh <= oh) {
                    moveTo(ox, sy);
                    lineTo(ow, sy);
                    curveTo(ew, sy, ew, sy, ew, oy);
                    lineTo(ew, oh);
                    lineTo(sx, oh);
                    lineTo(sx, oy);
                    curveTo(sx, sy, sx, sy, ox, sy);
                } else if (sy >= oy) {
                    moveTo(sx, sy);
                    lineTo(ew, sy);
                    lineTo(ew, oh);
                    curveTo(ew, eh, ew, eh, ow, eh);
                    lineTo(ox, eh);
                    curveTo(sx, eh, sx, eh, sx, oh);
                    lineTo(sx, sy);
                } else if (oy > oh) {
                    final double hy = (oy + oh) / 2;
                    moveTo(sx, hy);
                    curveTo(sx, sy, sx, sy, ox, sy);
                    lineTo(ow, sy);
                    curveTo(ew, sy, ew, sy, ew, hy);
                    curveTo(ew, eh, ew, eh, ow, eh);
                    lineTo(ox, eh);
                    curveTo(sx, eh, sx, eh, sx, hy);
                } else {
                    moveTo(sx, oy);
                    curveTo(sx, sy, sx, sy, ox, sy);
                    lineTo(ow, sy);
                    curveTo(ew, sy, ew, sy, ew, oy);
                    lineTo(ew, oh);
                    curveTo(ew, eh, ew, eh, ow, eh);
                    lineTo(ox, eh);
                    curveTo(sx, eh, sx, eh, sx, oh);
                    lineTo(sx, oy);
                }
                closePath();
            }});
            return;
        }

        final Area a = new Area(r);
        a.intersect(new Area(clip));
        graphics.setClip(a);
    }

    @Override public void dispose() { graphics.dispose(); }

    @Override public void draw(final Shape s) { graphics.draw(s); }
    @Override public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) { return graphics.drawImage(img, xform, obs); }
    @Override public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) { graphics.drawImage(img, op, x, y); }
    @Override public void drawRenderedImage(RenderedImage img, AffineTransform xform) { graphics.drawRenderedImage(img, xform); }
    @Override public void drawRenderableImage(RenderableImage img, AffineTransform xform) { graphics.drawRenderableImage(img, xform); }
    @Override public void drawString(String str, int x, int y) { graphics.drawString(str, x, y); }
    @Override public void drawString(String str, float x, float y) { graphics.drawString(str, x, y); }
    @Override public void drawString(AttributedCharacterIterator iterator, int x, int y) { graphics.drawString(iterator, x, y); }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return graphics.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return graphics.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return graphics.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return graphics.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return graphics.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        graphics.drawString(iterator, x, y);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        graphics.drawGlyphVector(g, x, y);
    }

    @Override
    public void fill(Shape s) {
        graphics.fill(s);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return graphics.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return graphics.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        graphics.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        graphics.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        graphics.setStroke(s);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        graphics.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return graphics.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        graphics.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        graphics.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return graphics.getRenderingHints();
    }

    @Override
    public SGraphics2D create() {
        return new SGraphics2D(graphics);
    }

    @Override
    public void translate(int x, int y) {
        graphics.translate(x, y);
    }

    @Override
    public Color getColor() {
        return graphics.getColor();
    }

    @Override
    public void setColor(Color c) {
        graphics.setColor(c);
    }

    @Override
    public void setPaintMode() {
        graphics.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        graphics.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        return graphics.getFont();
    }

    @Override
    public void setFont(Font font) {
        graphics.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return graphics.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        return graphics.getClipBounds();
    }

    @Override
    public Shape getClip() {
        return graphics.getClip();
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        graphics.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        graphics.fillRect(x, y, width, height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        graphics.clearRect(x, y, width, height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        graphics.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        graphics.drawOval(x, y, width, height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        graphics.fillOval(x, y, width, height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        graphics.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        graphics.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void translate(double tx, double ty) {
        System.out.println("Translate " + tx + ", " + ty);
        graphics.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        graphics.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        graphics.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        graphics.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        graphics.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        graphics.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        graphics.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return graphics.getTransform();
    }

    @Override
    public Paint getPaint() {
        return graphics.getPaint();
    }

    @Override
    public Composite getComposite() {
        return graphics.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        graphics.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return graphics.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return graphics.getStroke();
    }

    @Override
    public void clip(Shape s) {
        graphics.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return graphics.getFontRenderContext();
    }
}