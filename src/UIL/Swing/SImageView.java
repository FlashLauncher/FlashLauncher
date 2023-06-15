package UIL.Swing;

import UIL.*;
import UIL.base.IImage;
import UIL.base.IImageView;

import javax.swing.*;
import java.awt.*;

public class SImageView extends JComponent implements IImageView {
    private ImageSizeMode imageSizeMode;
    private ImagePosMode imagePosMode;
    private IImage image = null;

    public SImageView(ImagePosMode imagePosMode, ImageSizeMode imageSizeMode) {
        this.imagePosMode = imagePosMode;
        this.imageSizeMode = imageSizeMode;
        setOpaque(false);
    }

    @Override public int width() { return getWidth(); }
    @Override public int height() { return getHeight(); }
    @Override public boolean visible() { return isVisible(); }
    @Override public boolean isFocused() { return hasFocus(); }

    @Override public SImageView size(int width, int height) { setSize(width, height); return this; }
    @Override public SImageView pos(int x, int y) { setLocation(x, y); return this; }
    @Override public SImageView visible(boolean visible) { setVisible(visible); return this; }
    @Override public SImageView focus() { requestFocus(); return this; }

    @Override
    public SImageView image(IImage img) {
        image = img;
        repaint();
        return this;
    }

    @Override
    public SImageView imageSizeMode(ImageSizeMode imageSizeMode) {
        this.imageSizeMode = imageSizeMode;
        repaint();
        return this;
    }

    @Override
    public SImageView imagePosMode(ImagePosMode imagePosMode) {
        this.imagePosMode = imagePosMode;
        repaint();
        return this;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
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
            iw = img.getWidth(this);
            ih = img.getHeight(this);
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

        int x = 0, y = 0;
        if (iam == ImagePosMode.CENTER) {
            x = Math.round((cw - iw) / 2);
            y = Math.round((ch - ih) / 2);
        }

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHints(SSwing.RH);
        g.drawImage(img, x, y, Math.round(iw), Math.round(ih), this);
        g.dispose();
    }

    @Override public SImageView getComponent() { return this; }
}