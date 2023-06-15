package UIL.Swing;

import UIL.base.IColor;

import java.awt.*;
import java.awt.color.ColorSpace;

public class SColor extends Color implements IColor {
    public SColor(int r, int g, int b) {
        super(r, g, b);
    }

    public SColor(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public SColor(int rgb) {
        super(rgb);
    }

    public SColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
    }

    public SColor(float r, float g, float b) {
        super(r, g, b);
    }

    public SColor(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public SColor(ColorSpace cspace, float[] components, float alpha) {
        super(cspace, components, alpha);
    }

    @Override
    public SColor get() {
        return this;
    }
}
