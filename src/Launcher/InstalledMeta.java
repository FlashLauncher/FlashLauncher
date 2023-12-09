package Launcher;

import Utils.Version;

abstract class InstalledMeta extends Meta {
    String market = null;

    InstalledMeta(final String id, final Version version, final String author) {
        super(id, version, author);
    }

    public String getMarket() { return market; }
}