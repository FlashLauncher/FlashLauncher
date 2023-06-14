package Utils.json;

public class JsonElement {
    private final Object o;

    public JsonElement() { o = this; }
    public JsonElement(final Object obj) { o = obj; }

    public boolean isDict() { return o instanceof JsonDict; }
    public boolean isString() { return o instanceof String; }

    public Object get() { return o; }
    public JsonDict getAsDict() { return (JsonDict) o; }
    public JsonList getAsList() { return (JsonList) o; }
    public String getAsString() { return o.toString(); }
    public int getAsInt() { return (int) o; }
    public float getAsFloat() { return (float) o; }

    @Override public String toString() { return o == null ? "null" : o instanceof String ? "\"" + o +  "\"" : o.toString(); }
}
