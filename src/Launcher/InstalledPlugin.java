package Launcher;

import UIL.Lang;
import UIL.base.IImage;
import Utils.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

abstract class InstalledPlugin extends InstalledMeta {
    final File data, cache;
    boolean locked = false, enabled = false;
    File file = null;
    FSRoot root = null;
    Object n, sd;
    final Object c = new Object();
    Object[] cats = null;
    PluginContext context = null;
    Plugin plugin = null;
    ClassLoader cl = null;
    String main = null;
    IImage icon = null;
    boolean smoothIcon = true;
    private IniGroup lang = null;
    final ArrayList<InstalledPlugin> childs = new ArrayList<>(), connected = new ArrayList<>();
    final ListMap<String, String> dl = new ListMap<>(), ol = new ListMap<>();

    InstalledPlugin(final String id, final Object name, final Version version, final String author, final Object shortDescription) {
        super(id, version, author);
        n = name;
        sd = shortDescription;
        data = new File(FLCore.LAUNCHER_DIR, "plugins_data/" + id);
        cache = new File(FLCore.LAUNCHER_DIR, "plugins_cache/" + id);
    }

    @Override public IImage getIcon() { return icon; }
    @Override public boolean smoothIcon() { return smoothIcon; }
    @Override public Object getName() { return n; }
    @Override public Object getShortDescription() { return sd; }
    @Override public Object[] getCategories() { return cats; }

    public void enable() {
        synchronized (c) {
            if (enabled)
                return;
            for (final Map.Entry<String, String> e : dl.entrySet()) {
                final InstalledMeta im = FLCore.getById(e.getKey());
                if (im == null || !im.getVersion().isCompatibility(e.getValue()) || !(im instanceof InstalledPlugin) || !((InstalledPlugin) im).enabled) {
                    connected.clear();
                    return;
                }
                connected.add((InstalledPlugin) im);
            }
            for (final Map.Entry<String, String> e : ol.entrySet()) {
                final InstalledMeta im = FLCore.getById(e.getKey());
                if (im == null || !im.getVersion().isCompatibility(e.getValue()) || !(im instanceof InstalledPlugin) || !((InstalledPlugin) im).enabled)
                    continue;
                connected.add((InstalledPlugin) im);
            }
            enabled = true;
            if (root != null)
                FS.addRoot(root);
            context = new PluginContext(this);
            for (final InstalledPlugin ip : connected)
                synchronized (ip.c) {
                    ip.childs.add(this);
                    final Plugin p = ip.plugin;
                    if (p != null)
                        p.onPreEnableChild(context);
                }
            if (file == null) {
                cl = null;
                plugin = null;
            } else {
                try {
                    if (root != null)
                        if (root.exists("assets/" + getID() + "/langs/" + FLCore.lang + ".ini")) {
                            Lang.add(lang = new IniGroup(new String(root.readFully("assets/" + getID() + "/langs/" + FLCore.lang + ".ini"), StandardCharsets.UTF_8), false));
                            Lang.update();
                        } else if (root.exists("assets/" + getID() + "/langs/en_US.ini")) {
                            Lang.add(lang = new IniGroup(new String(root.readFully("assets/" + getID() + "/langs/en_US.ini"), StandardCharsets.UTF_8), false));
                            Lang.update();
                        } else
                            lang = null;
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }
                cl = new ClassLoader() {
                    private final FSRoot root = InstalledPlugin.this.root;
                    private final InstalledPlugin[] l = connected.toArray(new InstalledPlugin[0]);

                    @Override
                    protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
                        try {
                            Class<?> c = findLoadedClass(name);
                            if (c != null)
                                return c;
                            final String n = name.replaceAll("\\.", "/") + ".class";
                            if (root.exists(n)) {
                                final byte[] d = root.readFully(n);
                                c = defineClass(name, d, 0, d.length);
                                if (resolve)
                                    resolveClass(c);
                                return c;
                            }
                        } catch (final IOException ex) {
                            ex.printStackTrace();
                        }
                        for (final InstalledPlugin ip : l)
                            try {
                                return ip.cl.loadClass(name);
                            } catch (final ClassNotFoundException ignored) {
                            }
                        return super.loadClass(name, resolve);
                    }
                };

                try {
                    plugin = main == null ? null : (Plugin) cl.loadClass(main).getConstructor(PluginContext.class).newInstance(context);
                    if (plugin != null)
                        plugin.onEnable();
                } catch (final Throwable ex) {
                    (ex instanceof InvocationTargetException ?
                            ((InvocationTargetException) ex).getTargetException() : ex).printStackTrace();
                }

                for (final InstalledPlugin ip : connected)
                    synchronized (ip.c) {
                        ip.childs.add(this);
                        final Plugin p = ip.plugin;
                        if (p != null)
                            p.onEnableChild(context);
                    }
            }
            context.onEnable();
        }
    }

    public void disable() {
        synchronized (c) {
            if (!enabled)
                return;
            enabled = false;
            for (final InstalledPlugin ip : connected)
                synchronized (ip.c) {
                    final Plugin p = ip.plugin;
                    if (p != null)
                        p.onPreDisableChild(context);
                }
            for (final InstalledPlugin ip : childs.toArray(new InstalledPlugin[0])) {
                ip.disable();
                if (!ip.dl.containsKey(getID()))
                    ip.enable();
            }
            if (context != null)
                context.onDisable();
            if (plugin != null)
                plugin.onDisable();
            for (final InstalledPlugin ip : connected)
                synchronized (ip.c) {
                    final Plugin p = ip.plugin;
                    if (p != null)
                        p.onDisableChild(context);
                    ip.childs.remove(this);
                }
            if (lang != null) {
                Lang.remove(lang);
                lang = null;
                Lang.update();
            }
            FS.removeRoot(root);
        }
    }
}