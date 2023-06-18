package Launcher.metas;

import Launcher.TaskGroup;
import UIL.base.IImage;
import Utils.Version;

public abstract class Meta {
    public abstract String getID();
    public String getName() { return getID(); }
    public abstract Version getVersion();
    public abstract IImage getIcon();
    public TaskGroup start() { return null; }
}
