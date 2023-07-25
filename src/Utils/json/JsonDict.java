package Utils.json;

import Utils.ListMap;

import java.util.Map;
import java.util.Set;

public class JsonDict extends JsonElement {
    private final ListMap<String, JsonElement> elements = new ListMap<>();

    public JsonDict from(Map<String, Object> map) {
        for (String k : map.keySet())
            put(k, map.get(k));
        return this;
    }

    public JsonDict from(JsonDict dict) {
        for (String k : dict.keys())
            put(k, dict.get(k));
        return this;
    }

    public void clear() { elements.clear(); }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("{");
        boolean f = true;
        for (Map.Entry<String, JsonElement> e : elements.entrySet()) {
            if (f)
                f = false;
            else
                b.append(",");
            b.append("\"").append(e.getKey()).append("\":").append(e.getValue());
        }
        return b.append("}").toString();
    }

    public Set<String> keys() { return elements.keySet(); }
    public int size() { return elements.size(); }

    public void put(String key, Object value) {
        elements.put(key.startsWith("\"") && key.endsWith("\"") ? key.substring(1, key.length() - 1) : key,
                value instanceof JsonElement ? (JsonElement) value : new JsonElement(value));
    }

    public boolean has(final String key) { return elements.containsKey(key); }
    public JsonElement get(final String key) { return elements.get(key); }

    public JsonDict getAsDict(final String key) { return get(key).getAsDict(); }
    public JsonList getAsList(final String key) { return get(key).getAsList(); }
    public String getAsString(final String key) { return get(key).getAsString(); }
    public int getAsInt(final String key) { return get(key).getAsInt(); }
    public float getAsFloat(final String key) { return get(key).getAsFloat(); }
    public boolean getAsBool(final String key) { return get(key).getAsBool(); }

    public String getAsStringOrDefault(final String key, final String value) {
        final JsonElement v = get(key);
        if (v == null) return value;
        return v.getAsString();
    }
}
