package UIL.base;

public class ChangeableImage implements IImage {
    private IImage i;

    public ChangeableImage() { i = null; }
    public ChangeableImage(final IImage image) { i = image; }

    @Override
    public Object getImage() {
        final IImage ii = i;
        return ii == null ? null : ii.getImage();
    }

    public ChangeableImage setImage(final IImage image) {
        synchronized (this) {
            i = image;
            notifyAll();
        }
        return this;
    }
}
