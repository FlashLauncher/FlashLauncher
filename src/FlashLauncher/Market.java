package FlashLauncher;

import FlashLauncher.metas.Meta;
import UIL.base.IImage;

public abstract class Market {
    public abstract String getID();
    public abstract String getName();
    public IImage getIcon() { return null; }
    public abstract Meta getByID(final String id);

    public abstract TaskGroup refresh();
    public abstract Meta[] find(final String search);
}
