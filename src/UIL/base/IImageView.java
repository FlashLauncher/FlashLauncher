package UIL.base;

import UIL.ImagePosMode;
import UIL.ImageSizeMode;

public interface IImageView extends IComponent {
    IImageView image(final IImage img);
    IImageView imageSizeMode(final ImageSizeMode imageSizeMode);
    IImageView imagePosMode(final ImagePosMode imagePosMode);

    // IComponent
    @Override IImageView size(final int width, final int height);
    @Override IImageView pos(final int x, final int y);
    @Override IImageView visible(final boolean visible);
    @Override IImageView focus();
    @Override default IImageView borderRadius(final int borderRadius) { return this; }
    @Override default IImageView background(final IColor bg) { return this; }
    @Override default IImageView foreground(final IColor fg) { return this; }
    @Override default IImageView grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IImageView update() { return this; }
}
