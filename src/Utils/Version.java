package Utils;

public class Version {
    private final String v;

    public Version(final String ver) { v = ver; }

    public final boolean isCompatibility(final String ver) {
        if (ver == null || ver.length() == 0)
            return true;
        for (final String sv : ver.split("\\|"))
            if (sv.startsWith("-")) {
                if ((sv.length() == 1 || sv.equals("-*") || v.equals(sv.substring(1))))
                    return false;
            } else if (sv.length() == 0 || sv.equals("*") || sv.equals(v))
                return true;
        return true;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (obj instanceof Version)
            return v.equals(((Version) obj).v);
        return false;
    }

    @Override public final String toString() { return v; }
}
