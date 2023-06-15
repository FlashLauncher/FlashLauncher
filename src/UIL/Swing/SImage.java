package UIL.Swing;

import UIL.base.IImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SImage implements IImage {
    private final Image image;

    public SImage(byte[] data) throws IOException {
        image = ImageIO.read(new ByteArrayInputStream(data));
    }

    @Override
    public Image getImage() {
        return image;
    }
}
