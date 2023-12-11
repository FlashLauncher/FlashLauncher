package Launcher;

import Utils.Version;

/**
 * @since FlashLauncher 0.2.4
 */
public abstract class InstalledMeta extends Meta {
    String market = null;

    InstalledMeta(final String id, final Version version, final String author) {
        super(id, version, author);
    }

    public String getMarket() {
        System.out.println("get market");
        return market;
    }

    protected void onDelete() throws Throwable {}
}