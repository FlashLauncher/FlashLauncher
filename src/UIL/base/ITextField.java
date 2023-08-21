package UIL.base;

import UIL.HAlign;

public interface ITextField extends IText {
    interface InputListener {
        default boolean typed(final ITextField self, final char ch) { return false; }
        default boolean pressed(final ITextField self) { return false; }
        default boolean released(final ITextField self) { return false; }
    }

    interface ActionListener {
        void run(final ITextField self);
    }

    ITextField onAction(final ActionListener listener);
    ITextField onInput(final InputListener listener);

    ITextField hint(final Object hint);

    // IText
    ITextField text(final Object text);
    ITextField font(final IFont font);
    ITextField ha(final HAlign align);

    // IComponent
    @Override ITextField size(final int width, final int height);
    @Override ITextField pos(final int x, final int y);
    @Override ITextField visible(final boolean visible);
    @Override ITextField focus();
    @Override default ITextField background(final IColor bg) { return this; }
    @Override default ITextField foreground(final IColor fg) { return this; }
    @Override default ITextField grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default ITextField update() { return this; }
}