package UIL.base;

import UIL.HAlign;

public interface IComboBox extends IButton {
    default IComboBox onList(final ListListener listener) { return this; }

    interface ListListener { boolean run(final IComboBox self, final IScrollPane sp); }

    // IButton
    @Override IComboBox image(IImage image);
    @Override default IComboBox imageOffset(int imageOffset) { return this; }

    // IText
    @Override IComboBox text(Object text);
    @Override IComboBox font(IFont font);
    @Override IComboBox ha(HAlign align);

    // IComponent
    @Override IComboBox size(int width, int height);
    @Override IComboBox pos(int x, int y);
    @Override IComboBox visible(boolean visible);
    @Override IComboBox focus();
    @Override default IComboBox borderRadius(int borderRadius) { return this; }
    @Override default IComboBox background(IColor bg) { return this; }
    @Override default IComboBox foreground(IColor fg) { return this; }
    @Override default IComboBox grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IComboBox on(String name, Runnable runnable) { return this; }
}
