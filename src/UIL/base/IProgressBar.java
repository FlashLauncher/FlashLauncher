package UIL.base;

import Utils.RRunnable;

public interface IProgressBar extends IComponent {
    long maxProgress();
    long progress();

    IProgressBar maxProgress(final long max);
    IProgressBar progress(final long progress);

    // IComponent
    @Override IProgressBar size(final int width, final int height);
    @Override IProgressBar pos(final int x, final int y);
    @Override IProgressBar visible(final boolean visible);
    @Override IProgressBar focus();
    @Override default IProgressBar borderRadius(final RRunnable<Integer> borderRadius) { return this; }
    @Override default IProgressBar borderRadius(final int borderRadius) { return this; }
    @Override default IProgressBar background(final IColor bg) { return this; }
    @Override default IProgressBar foreground(final IColor fg) { return this; }
    @Override default IProgressBar grounds(final IColor bg, final IColor fg) { return background(bg).foreground(fg); }
    @Override default IProgressBar update() { return this; }
}
