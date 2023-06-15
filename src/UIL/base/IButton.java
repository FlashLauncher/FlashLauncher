package UIL.base;

import UIL.HAlign;
import UIL.ImgAlign;
import Utils.IntRunnable;

public interface IButton extends IText {
    IButton image(IImage image);
    IButton imageTextDist(int imageTextDist);
    default IButton imageOffset(int imageOffset) { return this; }
    default IButton imageAlign(ImgAlign align) { return this; }
    default IButton on(String action, IButtonRunnable runnable) { return this; }
    default IButton on(IButtonAction runnable) { return this; }
    default IButton onAction(IButtonAction runnable) { return this; }

    // IText
    @Override IButton text(Object text);
    @Override IButton font(IFont font);
    @Override IButton ha(HAlign align);

    // IComponent
    @Override IButton size(int width, int height);
    @Override IButton pos(int x, int y);
    @Override IButton visible(boolean visible);
    @Override IButton focus();
    @Override default IButton borderRadius(IntRunnable borderRadius) { return this; }
    @Override default IButton borderRadius(int borderRadius) { return this; }
    @Override default IButton background(IColor bg) { return this; }
    @Override default IButton foreground(IColor fg) { return this; }
    @Override default IButton grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IButton on(String name, Runnable runnable) { return this; }

    interface IButtonActionEvent {
        boolean isMouse();
        int clickCount();
    }

    interface IButtonRunnable { void run(IButton self); }
    interface IButtonAction { void run(IButton self, IButtonActionEvent event); }
}
