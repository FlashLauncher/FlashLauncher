package UIL.base;

import UIL.HAlign;

public interface IText extends IComponent {
    String text();
    IText text(Object text);
    IText font(IFont font);
    IText ha(HAlign align);

    // IComponent
    @Override IText size(int width, int height);
    @Override IText pos(int x, int y);
    @Override IText visible(boolean visible);
    @Override IText focus();
    @Override default IText borderRadius(int borderRadius) { return this; }
    @Override default IText background(IColor bg) { return this; }
    @Override default IText foreground(IColor fg) { return this; }
    @Override default IText grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
}
