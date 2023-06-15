package UIL.base;

import UIL.ImagePosMode;
import UIL.ImageSizeMode;

public interface IImageView extends IComponent {
    IImageView image(IImage img);
    IImageView imageSizeMode(ImageSizeMode imageSizeMode);
    IImageView imagePosMode(ImagePosMode imagePosMode);

    // IComponent
    @Override IImageView size(int width, int height);
    @Override IImageView pos(int x, int y);
    @Override IImageView visible(boolean visible);
    @Override IImageView focus();
    @Override default IImageView borderRadius(int borderRadius) { return this; }
    @Override default IImageView background(IColor bg) { return this; }
    @Override default IImageView foreground(IColor fg) { return this; }
    @Override default IImageView grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IImageView on(String name, Runnable runnable) { return this; }
}
