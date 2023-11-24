package UIL.base;

import UIL.HAlign;
import UIL.ImgAlign;
import Utils.RRunnable;

public interface IToggleButton extends IButton {
    interface IToggleButtonListener {
        void run(final IToggleButton self, final boolean newValue);
    }

    default IToggleButton onChange(final IToggleButtonListener listener) { return this; }
    IToggleButton backgroundAccent(final IColor color);
    IToggleButton foregroundAccent(final IColor color);

    // IButton
    @Override IToggleButton image(final IImage image);
    @Override IToggleButton imageTextDist(final int imageTextDist);

    /**
     * @since FlashLauncher 0.2.1
     */
    @Override IToggleButton smooth(final boolean value);

    @Override default IToggleButton imageOffset(final int imageOffset) { return this; }
    @Override default IToggleButton imageAlign(final ImgAlign align) { return this; }
    @Override default IToggleButton onAction(final IButton.IButtonAction runnable) { return this; }

    // IText
    @Override IToggleButton text(final Object text);
    @Override IToggleButton font(final IFont font);
    @Override IToggleButton ha(final HAlign align);

    // IComponent
    @Override IToggleButton size(final int width, final int height);
    @Override IToggleButton pos(final int x, int y);
    @Override IToggleButton visible(final boolean visible);
    @Override IToggleButton focus();
    @Override default IToggleButton borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IToggleButton background(final IColor background) { return this; }
    @Override default IToggleButton foreground(final IColor foreground) { return this; }
    @Override default IToggleButton grounds(final IColor background, final IColor foreground) { return background(background).foreground(foreground); }
    @Override default IToggleButton update() { return this; }
}
