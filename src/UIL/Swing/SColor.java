package UIL.Swing;

import UIL.base.IColor;

import java.awt.*;
import java.awt.color.ColorSpace;

public class SColor extends Color implements IColor {
    public SColor(final int r, final int g, final int b) { super(r, g, b); }
    public SColor(final int r, final int g, final int b, final int a) { super(r, g, b, a); }
    public SColor(final int rgb) { super(rgb); }
    public SColor(final int rgba, final boolean hasAlpha) { super(rgba, hasAlpha); }
    public SColor(final float r, final float g, final float b) { super(r, g, b); }
    public SColor(final float r, final float g, final float b, final float a) { super(r, g, b, a); }
    public SColor(final ColorSpace colorSpace, final float[] components, final float alpha) { super(colorSpace, components, alpha); }

    @Override public SColor get() { return this; }
}
