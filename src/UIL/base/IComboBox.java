package UIL.base;

import UIL.HAlign;
import Utils.RRunnable;

public interface IComboBox extends IButton {
    interface OnListListener {
        IContainer run(final ListEvent<OnListListener> event);
    }

    interface CloseListListener {
        boolean run(final ListEvent<CloseListListener> event);
    }

    interface ListEvent<T> {
        T getSelfListener();
        IComboBox getSelf();
        IContainer getContainer();

        boolean isClosed();
        void close();
    }

    default IComboBox onList(final OnListListener listener) { return this; }
    default IComboBox onCloseList(final CloseListListener listener) { return this; }
    default IComboBox offCloseList(final CloseListListener listener) { return this; }

    // IButton
    @Override IComboBox image(final IImage image);
    @Override default IComboBox imageOffset(final int imageOffset) { return this; }

    /**
     * @since FlashLauncher 0.2.1
     */
    @Override IComboBox smooth(final boolean value);

    // IText
    @Override IComboBox text(final Object text);
    @Override IComboBox font(final IFont font);
    @Override IComboBox ha(final HAlign align);

    // IComponent
    @Override IComboBox size(final int width, final int height);
    @Override IComboBox pos(final int x, final int y);
    @Override IComboBox visible(final boolean visible);
    @Override IComboBox focus();
    @Override default IComboBox borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IComboBox background(final IColor bg) { return this; }
    @Override default IComboBox foreground(final IColor fg) { return this; }
    @Override default IComboBox grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IComboBox update() { return this; }
}
