package UIL.base;

import UIL.HAlign;
import UIL.ImgAlign;
import Utils.RRunnable;

public interface IButton extends IText {
    IButton image(final IImage image);
    IButton imageTextDist(final int imageTextDist);
    default IButton imageOffset(final int imageOffset) { return this; }
    default IButton imageAlign(final ImgAlign align) { return this; }
    default IButton onAction(final IButtonAction runnable) { return this; }

    // IText
    @Override IButton text(final Object text);
    @Override IButton font(final IFont font);
    @Override IButton ha(final HAlign align);

    // IComponent
    @Override IButton size(final int width, final int height);
    @Override IButton pos(final int x, int y);
    @Override IButton visible(final boolean visible);
    @Override IButton focus();
    @Override default IButton borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IButton borderRadius(final int borderRadius) { return this; }
    @Override default IButton background(final IColor bg) { return this; }
    @Override default IButton foreground(final IColor fg) { return this; }
    @Override default IButton grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IButton update() { return this; }

    interface IButtonActionEvent {
        boolean isMouse();
        int clickCount();
    }

    interface IButtonAction { void run(final IButton self, final IButtonActionEvent event); }
}
