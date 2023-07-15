package UIL;

public class LangItem {
    public String value = null;

    public LangItem() {}
    public LangItem(final String value) {
        this.value = value;
    }

    @Override public String toString() { return value; }
}