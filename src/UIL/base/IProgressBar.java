package UIL.base;

import Utils.RRunnable;

public interface IProgressBar extends IComponent {
    long maxProgress();
    IProgressBar maxProgress(long max);
    long progress();
    IProgressBar progress(long progress);

    // IComponent
    @Override IProgressBar size(int width, int height);
    @Override IProgressBar pos(int x, int y);
    @Override IProgressBar visible(boolean visible);
    @Override IProgressBar focus();
    @Override default IProgressBar borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IProgressBar borderRadius(final int borderRadius) { return this; }
    @Override default IProgressBar background(IColor bg) { return this; }
    @Override default IProgressBar foreground(IColor fg) { return this; }
    @Override default IProgressBar grounds(IColor bg, IColor fg) { return background(bg).foreground(fg); }
    @Override default IProgressBar on(String name, Runnable runnable) { return this; }
}
