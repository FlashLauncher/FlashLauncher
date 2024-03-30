package Utils;

public class Version {
    private final String v;

    public Version(final String ver) { v = ver; }

    public final boolean isCompatibility(final String ver) {
        if (ver == null || ver.isEmpty())
            return true;
        m:
        for (String sv : ver.split(",")) {
            final boolean m = sv.startsWith("-");
            if (m)
                sv = sv.substring(1);

            if (sv.startsWith("^")) {
                sv = sv.substring(1);
                final String[] l1 = sv.split("\\."), l2 = v.split("\\.");
                final int l = Math.min(l1.length, l2.length);
                for (int i = 0; i < l; i++)
                    if (!l1[i].equals(l2[i]))
                        try {
                            if (Integer.parseInt(l1[i]) > Integer.parseInt(l2[i]))
                                continue m;
                            return !m;
                        } catch (final Exception ex) {
                            ex.printStackTrace();
                            continue m;
                        }
                if (m)
                    return l2.length < l1.length;
                return l2.length >= l1.length;
            }

            if (sv.isEmpty() || sv.equals("*") || sv.equals(v))
                return !m;
        }
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
