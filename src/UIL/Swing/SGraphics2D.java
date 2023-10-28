package UIL.Swing;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
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
    public void clipRect(int x, int y, int width, int height) {
        graphics.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        final Area a = new Area(r);
        a.intersect(new Area(new Rectangle(x, y, width, height)));
        graphics.setClip(a);
    }

    @Override
    public void setClip(Shape clip) {
        final Area a = new Area(r);
        a.intersect(new Area(clip));
        graphics.setClip(a);
    }

    @Override public void dispose() {
        graphics.dispose();
    }

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
