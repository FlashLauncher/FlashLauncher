package UIL;

import java.util.ArrayList;

public class LProc {
    private final String text;
    private final String[] vars;

    public LProc(String str) {
        if (str.contains("=<<!:")) {
            final int i = str.indexOf("=<<!:");
            String s = str.substring(0, i);
            final ArrayList<String> l = new ArrayList<>();
            for (String an : str.substring(i + 5).split(","))
                if (an.length() > 0)
                    l.add(an);
                else
                    l.add(null);
            text = s;
            vars = l.toArray(new String[0]);
        } else {
            text = str;
            vars = new String[0];
        }
    }

    public LProc(LangItem li) {
        this(li.toString());
    }

    public String toString(Object... args) {
        String r = text;
        for (int i = 0; i < args.length && i < vars.length; i++) {
            final String vn = vars[i];
            if (vn == null)
                continue;
            r = r.replace("%" + vn + ";", args[i].toString());
        }
        return r;
    }
}
