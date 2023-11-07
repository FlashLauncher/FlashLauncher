package UIL.Swing;

import UIL.*;
import UIL.base.IImage;
import UIL.base.IImageView;
import Utils.Core;
import Utils.RRunnable;

import javax.swing.*;
import java.awt.*;

public class SImageView extends JComponent implements IImageView {
    private ImageSizeMode imageSizeMode;
    private ImagePosMode imagePosMode;
    private IImage image = null;
    private boolean smooth = true;

    public SImageView(final ImagePosMode imagePosMode, final ImageSizeMode imageSizeMode) {
        this.imagePosMode = imagePosMode;
        this.imageSizeMode = imageSizeMode;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final IImage ii = image;
        final Image img = ii == null ? null : (Image) image.getImage();
        if (img == null)
            return;

        final ImageSizeMode ism = imageSizeMode;
        final ImagePosMode iam = imagePosMode;
        final int cw = getWidth(), ch = getHeight();
        float iw, ih;

        if (ism == ImageSizeMode.SCALE) {
            iw = cw;
            ih = ch;
        } else {
            iw = img.getWidth(null);
            ih = img.getHeight(null);
            switch (ism) {
                case OUTSIDE:
                case INSIDE: {
                    if (iw != cw) {
                        ih = ih / iw * cw;
                        iw = cw;
                    }
                    if (ism == ImageSizeMode.INSIDE ? ih < ch : ih > ch) {
                        iw = iw / ih * ch;
                        ih = ch;
                    }
                    break;
                }
                default:
                    break;
            }
        }

        final int x, y;
        if (iam == ImagePosMode.CENTER) {
            x = Math.round((cw - iw) / 2);
            y = Math.round((ch - ih) / 2);
        } else {
            x = 0;
            y = 0;
        }

        final Graphics2D g = (Graphics2D) (graphics instanceof Graphics2D ? graphics : graphics.create());
        g.setRenderingHints(SSwing.RH);
        if (!smooth)
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, x, y, Math.round(iw), Math.round(ih), this);
        g.dispose();
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }
    @Override public SImageView getComponent() { return this; }

    @Override public SImageView size(final int width, final int height) { setSize(width, height); return this; }
    @Override public SImageView pos(final int x, final int y) { setLocation(x, y); return this; }
    @Override public SImageView visible(final boolean visible) { setVisible(visible); return this; }
    @Override public SImageView focus() { requestFocus(); return this; }

    @Override public SImageView image(final IImage img) { image = img; return this; }
    @Override public SImageView imageSizeMode(final ImageSizeMode imageSizeMode) { this.imageSizeMode = imageSizeMode; return this; }
    @Override public SImageView imagePosMode(final ImagePosMode imagePosMode) { this.imagePosMode = imagePosMode; return this; }
    @Override public SImageView smooth(boolean value) { smooth = value; return this; }

    @Override
    public SImageView update() {
        repaint();
        return this;
    }
}