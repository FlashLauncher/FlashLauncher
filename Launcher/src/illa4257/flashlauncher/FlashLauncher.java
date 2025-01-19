package illa4257.flashlauncher;

import illa4257.i4Framework.base.FrameworkWindow;
import illa4257.i4Framework.base.components.Container;
import illa4257.i4Framework.base.components.Panel;
import illa4257.i4Framework.base.components.Window;
import illa4257.i4Framework.base.events.components.StyleUpdateEvent;
import illa4257.i4Framework.base.points.PointAttach;

public class FlashLauncher extends Window {
    public static final Container stylesheet = new Container();
    public final FrameworkWindow frameworkWindow;

    public final Panel menuBar = new Panel();

    public FlashLauncher() {
        tag.set("Window");
        classes.add("flash-launcher");

        frameworkWindow = FLLoader.framework.newWindow(this);
        stylesheet.add(this);

        menuBar.setLocation(8, 8);
        menuBar.setWidth(64);
        menuBar.setEndY(new PointAttach(-8, height));
        add(menuBar);

        setSize(720, 480);
        center();
        setVisible(true);
    }
}