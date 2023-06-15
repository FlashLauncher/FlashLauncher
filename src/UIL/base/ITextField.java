package UIL.base;

import UIL.HAlign;

public interface ITextField extends IText {
    interface InputListener {
        boolean typed(final ITextField self, final char ch);
        boolean pressed(final ITextField self);
        boolean released(final ITextField self);
    }

    class InputAdapter implements InputListener {
        @Override public boolean typed(final ITextField self, final char ch) { return false; };
        @Override public boolean pressed(final ITextField self) { return false; };
        @Override public boolean released(final ITextField self) { return false; };
    }

    interface ActionListener { void run(final ITextField self); }

    default ITextField on(final InputListener listener) { return this; }
    default ITextField on(final ActionListener listener) { return this; }

    // IText
    ITextField text(Object text);
    ITextField font(IFont font);
    ITextField ha(HAlign align);

    // IComponent
    @Override ITextField size(final int width, final int height);
    @Override ITextField pos(final int x, final int y);
    @Override ITextField visible(final boolean visible);
    @Override ITextField focus();
    @Override default ITextField on(final String name, final Runnable action) { return this; }
    @Override default ITextField borderRadius(final int borderRadius) { return this; }
    @Override default ITextField background(final IColor bg) { return this; }
    @Override default ITextField foreground(final IColor fg) { return this; }
    @Override default ITextField grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
}