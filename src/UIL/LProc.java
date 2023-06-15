package UIL;

public class LProc {
    private final Object o;

    public LProc(final Object object) { o = object; }

    public String toString(final Object... args) {
        String r = o.toString();
        for (int i = args.length - 1; i >= 0; i--)
            r = r.replaceAll("%" + i, args[i].toString());
        return r;
    }
}
