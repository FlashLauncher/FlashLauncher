package UIL.base;

import UIL.HAlign;

public interface ICheckBox extends IText {
    boolean checked();
    ICheckBox checked(final boolean value);

    ICheckBox backgroundAccent(final IColor color);
    ICheckBox foregroundAccent(final IColor color);

    // IText
    ICheckBox text(final Object text);
    ICheckBox font(final IFont font);
    ICheckBox ha(final HAlign align);

    // IComponent
    @Override ICheckBox size(final int width, final int height);
    @Override ICheckBox pos(final int x, final int y);
    @Override ICheckBox visible(final boolean visible);
    @Override ICheckBox focus();
    @Override default ICheckBox background(final IColor bg) { return this; }
    @Override default ICheckBox foreground(final IColor fg) { return this; }
    @Override default ICheckBox grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default ICheckBox update() { return this; }
}
