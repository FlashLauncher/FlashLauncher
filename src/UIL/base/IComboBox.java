package UIL.base;

import UIL.HAlign;

public interface IComboBox extends IButton {
    interface OnListListener {
        IContainer run(final IComboBox self, final IContainer container);
    }

    interface CloseListListener {
        boolean run(final IComboBox selfObject, final IContainer container, final CloseListListener selfListener);
    }

    default IComboBox onList(final OnListListener listener) { return this; }
    default IComboBox onCloseList(final CloseListListener listener) { return this; }
    default IComboBox offCloseList(final CloseListListener listener) { return this; }

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
