package UIL.base;

public class ChangeableColor implements IColor {
    private IColor c;

    public ChangeableColor() { c = null; }
    public ChangeableColor(final IColor color) { c = color; }

    public ChangeableColor set(final IColor color) {
        synchronized (this) {
            c = color;
            notifyAll();
        }
        return this;
    }

    @Override
    public int alpha() {
        final IColor co = c;
        return co == null ? 0 : co.alpha();
    }

    @Override
    public Object get() {
        final IColor co = c;
        return co == null ? null : co.get();
    }
}
