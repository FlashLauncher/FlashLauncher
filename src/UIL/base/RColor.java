package UIL.base;

public interface RColor extends IColor {
    IColor getColor();

    @Override default int alpha() { return getColor().alpha(); }
    @Override default Object get() { return getColor().get(); }
}