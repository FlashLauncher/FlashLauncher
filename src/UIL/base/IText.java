package UIL.base;

import UIL.HAlign;

public interface IText extends IComponent {
    String text();
    IText text(final Object text);
    IText font(final IFont font);
    IText ha(final HAlign align);

    // IComponent
    @Override IText size(final int width, final int height);
    @Override IText pos(final int x, final int y);
    @Override IText visible(final boolean visible);
    @Override IText focus();
    @Override default IText background(final IColor bg) { return this; }
    @Override default IText foreground(final IColor fg) { return this; }
    @Override default IText grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IText update() { return this; }
}
