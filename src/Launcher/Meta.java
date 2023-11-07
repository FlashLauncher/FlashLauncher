package Launcher;

import UIL.base.IImage;
import Utils.Version;

public abstract class Meta {
    public static final int ICON_SIZE = 56;

    private final String id;
    String author;
    Version ver;

    public Meta(final String id, final Version version, final String author) { this.id = id; ver = version; this.author = author; }

    public final String getID() { return id; }
    public Version getVersion() { return ver; }
    public final String getAuthor() { return author; }

    public Object[] getCategories() { return null; }
    public TaskGroup install() { return null; }

    public abstract IImage getIcon();
    public boolean smoothIcon() { return true; }
    public abstract Object getName();
    public abstract Object getShortDescription();
}