package UIL.Swing;

import UIL.base.IFont;

import java.awt.*;

public class SFont extends Font implements IFont {
    public SFont(String name, int style, int size) {
        super(name, style, size);
    }

    @Override
    public Object get() {
        return this;
    }
}
