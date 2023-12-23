package Launcher;

import UIL.base.IImage;
import Utils.SyncVar;
import Utils.Version;

public abstract class Meta {
    public static final int ICON_SIZE = 56;

    private final String id;
    private final SyncVar<String> author;
    Version ver;

    public Meta(final String id, final Version version, final String author) { this.id = id; ver = version; this.author = new SyncVar<>(author); }

    public final String getID() { return id; }
    public Version getVersion() { return ver; }
    public final String getAuthor() { return author.get(); }

    public Object[] getCategories() { return null; }
    public TaskGroup install() { return null; }

    public abstract IImage getIcon();
    public boolean smoothIcon() { return true; }
    public abstract Object getName();
    public abstract Object getShortDescription();

    public void setAuthor(final String author) { this.author.set(author); }
}