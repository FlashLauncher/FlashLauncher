package UIL.base;

import UIL.HAlign;

public interface IComboBox extends IButton {
    interface ListListener {
        boolean run(final IComboBox self, final IScrollPane sp);
    }

    default IComboBox onList(final ListListener listener) { return this; }

    // IButton
    @Override IComboBox image(final IImage image);
    @Override default IComboBox imageOffset(final int imageOffset) { return this; }

    // IText
    @Override IComboBox text(final Object text);
    @Override IComboBox font(final IFont font);
    @Override IComboBox ha(final HAlign align);

    // IComponent
    @Override IComboBox size(final int width, final int height);
    @Override IComboBox pos(final int x, final int y);
    @Override IComboBox visible(final boolean visible);
    @Override IComboBox focus();
    @Override default IComboBox borderRadius(final int borderRadius) { return this; }
    @Override default IComboBox background(final IColor bg) { return this; }
    @Override default IComboBox foreground(final IColor fg) { return this; }
    @Override default IComboBox grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IComboBox update() { return this; }
}
