package Launcher;

import UIL.base.IImage;
import Utils.SyncVar;
import Utils.Version;

/**
 * @since FlashLauncher 0.2.4
 */
public class CIMeta extends InstalledMeta {
    private final SyncVar<IImage> icon = new SyncVar<>();

    private final SyncVar<Object>
            name = new SyncVar<>(),
            sd = new SyncVar<>();

    /**
     * @since FlashLauncher 0.2.4
     */
    public CIMeta(String id, Version version, String author) { super(id, version, author); }

    @Override public IImage getIcon() { return icon.get(); }
    @Override public Object getName() { return name.get(); }
    @Override public Object getShortDescription() { return sd.get(); }

    public void setIcon(final IImage icon) { this.icon.set(icon); }
    public void setName(final Object name) { this.name.set(name); }
    public void setShortDescription(final Object shortDescription) { sd.set(shortDescription); }

    @Override
    public String toString() {
        final Object n = name.get();
        return n == null ? null : n.toString();
    }
}