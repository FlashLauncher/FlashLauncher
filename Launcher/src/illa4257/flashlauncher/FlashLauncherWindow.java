package illa4257.flashlauncher;

import illa4257.flashlauncher.components.CubeLoader;
import illa4257.flashlauncher.events.OnAdd;
import illa4257.i4Framework.base.FrameworkWindow;
import illa4257.i4Framework.base.components.*;
import illa4257.i4Framework.base.events.components.ActionEvent;
import illa4257.i4Framework.base.points.PPointAdd;
import illa4257.i4Framework.base.points.Point;
import illa4257.i4Framework.base.points.PointAttach;
import illa4257.i4Framework.base.styling.StyleSetting;
import illa4257.i4Utils.lang.LangMgr;
import illa4257.i4Utils.logger.i4Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class FlashLauncherWindow extends Window {
    public static final i4Logger L = FlashLauncher.L;
    private static final ConcurrentHashMap<String, Button> topMenu = new ConcurrentHashMap<>();

    private static final LangMgr lang = FlashLauncher.lang;

    public final FrameworkWindow frameworkWindow;

    public final Panel menuBar = new Panel();

    private Button currentItem = null;
    private Point lastTopPoint = null;

    Container container = null;

    private static class CP extends Point {
        public final Point parentWidth;
        public final float width;

        public CP(final Point pw, final float w) {
            parentWidth = pw;
            width = w;
        }

        @Override
        protected float calc() {
            return (parentWidth.calcFloat() - width) / 2;
        }

        @Override
        public void onConstruct() {
            super.onConstruct();
            parentWidth.subscribe(this::reset);
        }

        @Override
        public void onDestruct() {
            super.onDestruct();
            parentWidth.unsubscribe(this::reset);
        }
    }

    public FlashLauncherWindow() {
        tag.set("Window");
        classes.add("flash-launcher");

        frameworkWindow = FlashLauncher.framework.newWindow(this);

        final Panel loaderPanel = new Panel();
        loaderPanel.classes.add("loader-panel");
        loaderPanel.setSize(128, 192);
        loaderPanel.setStartX(new CP(width, 128));
        loaderPanel.setStartY(new CP(height, 192));
        add(loaderPanel);

        final CubeLoader loader = new CubeLoader();
        loader.setSize(128, 128);
        loaderPanel.add(loader);

        final Label loaderLabel = new Label("Loading ...");
        loaderLabel.styles.put("text-align", new StyleSetting("center"));
        loaderLabel.setSize(128, 64);
        loaderLabel.setY(128);
        loaderPanel.add(loaderLabel);

        setTitle("FlashLauncher");
        setSize(720, 480);
        center();
        setVisible(true);

        FlashLauncher.threadPoolShort.submit(() -> {
            try {
                FlashLauncher.loader.join();
                invokeLater(() -> {
                    remove(loaderPanel);
                    repaint();
                });
                invokeLater(this::initUI);
            } catch (final Exception ex) {
                L.log(ex);
            }
        });
    }

    public void open(final String menuItem) {
        final Button b = topMenu.get(menuItem);
        if (b == null)
            return;
        b.fire(new ActionEvent());
    }

    private final int elementMargin = 4;
    private Point elementEndX, elementWidth;

    void newTopElement(final String id, final String name, final Consumer<Container> cons) {
        topMenu.computeIfAbsent(id, k -> {
            final Button b = new Button(name);
            b.classes.add("menu-item");
            b.setX(elementMargin);
            b.setEndX(elementEndX);
            b.setStartY(lastTopPoint != null ? lastTopPoint : new PointAttach(elementMargin, null));
            b.setEndY(new PPointAdd(b.startY, elementWidth));
            lastTopPoint = new PointAttach(elementMargin, b.endY);
            b.addEventListener(ActionEvent.class, e -> {
                if (b.pseudoClasses.contains("selected"))
                    return;
                b.pseudoClasses.add("selected");
                if (currentItem != null) {
                    currentItem.pseudoClasses.remove("selected");
                    currentItem.repaint();
                }
                currentItem = b;

                final Container c = new Container();
                c.setStartX(new PointAttach(16, menuBar.width));
                c.setStartY(new PointAttach(8, null));
                c.setEndX(new PointAttach(-8, width));
                c.setEndY(new PointAttach(-8, height));

                if (container != null)
                    remove(container);
                add(container = c);

                cons.accept(c);

                b.repaint();
            });
            menuBar.add(b);
            return b;
        });
    }

    private void initUI() {
        menuBar.classes.add("menu-bar");
        menuBar.setLocation(8, 8);
        menuBar.setWidth(64);
        menuBar.setEndY(new PointAttach(-8, height));
        add(menuBar);

        elementWidth = new PointAttach(-elementMargin * 2, menuBar.width);
        elementEndX = new PointAttach(-elementMargin, menuBar.width);

        newTopElement("play", "Play", c -> {
            final Panel p = new Panel();
            p.classes.add("play-menu-bar");
            p.setStartY(new PointAttach(-64, p.endY));
            p.setEndX(c.width);
            p.setEndY(c.height);
            c.add(p);

            final float o = 8;
            final Point eY = new PointAttach(-o, p.height);

            final Button play = new Button(lang.of("play"));
            play.setLocation(o, o);
            play.setWidth(96);
            play.setEndY(eY);
            p.add(play);

            final TabPane.Tab tab = new TabPane.Tab("Logs", new Panel(), false);
            final TabPane tabs = new TabPane();
            tabs.addTab(tab);
            tabs.selectTab(tab);
            tabs.setEndX(c.width);
            tabs.setEndY(new PointAttach(-8, p.startY));
            c.add(tabs);
        });

        newTopElement("javaList", "Javas", c -> {
            final TabPane.Tab t = new TabPane.Tab("Portable Javas", observedQueue(FlashLauncher.portableJavaList, ji -> {
                final Button j = new Button(ji.distribution.displayName + " " + ji.majorVersion);
                j.setWidth(128);
                j.setHeight(128);
                return j;
            }), false);

            final TabPane tabs = new TabPane();
            tabs.setEndX(c.width);
            tabs.setEndY(c.height);
            tabs.addTab(t);
            tabs.addTab(new TabPane.Tab("Local Javas", observedQueue(FlashLauncher.localJavaList, ji -> {
                final Button j = new Button(ji.distribution.displayName + " " + ji.majorVersion);
                j.setWidth(128);
                j.setHeight(128);
                return j;
            }), false));
            final Panel add = new Panel();
            tabs.addTab(new TabPane.Tab("+", add, false));
            tabs.selectTab(t);
            c.add(tabs);
        });

        open("play");
    }

    public <T> Container observedQueue(final QueueTrigger<T> queue, final Function<T, Component> elementConstructor) {
        return new Panel() {
            public void onAdd(final OnAdd<T> e) {
                if (e.queue == queue)
                    add(elementConstructor.apply(e.element));
            }

            @Override
            public boolean add(Component component) {
                if (super.add(component)) {
                    component.setX(8);
                    component.setY(8);
                    return true;
                }
                return false;
            }

            @Override
            public void onConstruct() {
                super.onConstruct();
                for (final T e : queue)
                    add(elementConstructor.apply(e));
                getWindow().addEventListener(OnAdd.class, this::onAdd);
            }

            @Override
            public void onDestruct() {
                super.onDestruct();
                getWindow().removeEventListener(this::onAdd);
            }
        };
    }
}