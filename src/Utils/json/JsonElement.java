package Utils.json;

import java.util.UUID;
import java.util.regex.Matcher;

public class JsonElement {
    private final Object o;

    public JsonElement() { o = this; }
    public JsonElement(final Object obj) {
        if (obj instanceof UUID) {
            o = obj.toString();
            return;
        }
        o = obj;
    }

    public boolean isDict() { return o instanceof JsonDict; }
    public boolean isList() { return o instanceof JsonList; }
    public boolean isString() { return o instanceof String; }

    public Object get() { return o; }
    public JsonDict getAsDict() { return (JsonDict) o; }
    public JsonList getAsList() { return (JsonList) o; }
    public String getAsString() { return o.toString(); }
    public int getAsInt() { return (int) o; }
    public float getAsFloat() { return (float) o; }
    public boolean getAsBool() { return (boolean) o; }

    @Override public String toString() {
        return o == null ? "null" :
            o instanceof String ? "\"" + Matcher.quoteReplacement(((String) o))
                        .replaceAll("\n", "\\\\n")
                        .replaceAll("\"", "\\\\\"")
                    + "\"" :
                    o.toString();
    }
}
