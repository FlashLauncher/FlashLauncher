package UIL.base;

public class LoadingImage extends ChangeableImage {
    boolean finished = false;

    public LoadingImage() { super(); }
    public LoadingImage(final IImage image) { super(image); }

    @Override
    public LoadingImage setImage(final IImage image) {
        if (image != null && !finished)
            synchronized (this) {
                super.setImage(image);
                finished = true;
            }
        return this;
    }

    public boolean isFinished() {
        synchronized (this) {
            return finished;
        }
    }
}
