package UIL.base;

import UIL.HAlign;

public interface ICheckBox extends IText {
    boolean checked();
    ICheckBox checked(final boolean value);

    // IText
    ICheckBox text(Object text);
    ICheckBox font(IFont font);
    ICheckBox ha(HAlign align);

    // IComponent
    @Override ICheckBox size(int width, int height);
    @Override ICheckBox pos(int x, int y);
    @Override ICheckBox visible(boolean visible);
    @Override ICheckBox focus();
    @Override default ICheckBox borderRadius(int borderRadius) { return this; }
    @Override default ICheckBox background(IColor bg) { return this; }
    @Override default ICheckBox foreground(IColor fg) { return this; }
    @Override default ICheckBox grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
}
