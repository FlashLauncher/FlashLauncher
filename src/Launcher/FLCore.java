package Launcher;

import Launcher.base.*;
import UIL.*;
import UIL.Swing.SPanel;
import UIL.Swing.SSwing;
import UIL.base.*;
import Utils.*;
import Utils.fixed.FixedEntry;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FLCore {
    public static final File LAUNCHER_DIR = Core.getPath(FLCore.class);
    public static final IImage ICON_PLAY, ICON_PROFILES, ICON_MARKET, ICON_ACCOUNTS, ICON_FIND, ICON_INSTALL, ICON_UPDATE, ICON_DELETE, ICON_ADD;

    static final IniGroup config;

    private static final ArrayList<String> langs = new ArrayList<>();

    static {
        IniGroup g = null;
        final File cf = new File(Core.getPath(FLCore.class), "config.ini");
        if (cf.exists() && cf.isFile())
            try {
                g = new IniGroup(new String(FS.OS.readFully(cf.getAbsolutePath()), StandardCharsets.UTF_8), false);
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        config = g == null ? new IniGroup() : g;

        UI.UI = new SSwing();
        IImage ip = null, ip2 = null, im = null, ia = null, ifi = null, ii = null, iu = null, id = null, a = null;
        try {
            FS.addRoot(FS.newFS(Core.getFile(FLCore.class)));

            ip = UI.image("flash-launcher://images/play.png");
            ip2 = UI.image("flash-launcher://images/profiles.png");
            im = UI.image("flash-launcher://images/market.png");
            ia = UI.image("flash-launcher://images/accounts.png");
            ifi = UI.image("flash-launcher://images/find.png");
            ii = UI.image("flash-launcher://images/install.png");
            iu = UI.image("flash-launcher://images/update.png");
            id = UI.image("flash-launcher://images/delete.png");
            a = UI.image("flash-launcher://images/add.png");
        } catch (final Throwable ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        ICON_PLAY = ip;
        ICON_PROFILES = ip2;
        ICON_MARKET = im;
        ICON_ACCOUNTS = ia;
        ICON_FIND = ifi;
        ICON_INSTALL = ii;
        ICON_UPDATE = iu;
        ICON_DELETE = id;
        ICON_ADD = a;

        try {
            for (final FSFile f : FS.ROOT.list("flash-launcher://langs"))
                if (!f.getName().equals("lang-names.ini"))
                    langs.add(f.getNameWithoutExt());
        } catch (final Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    private static ServerSocket server;
    private static final int PORT = 53788;
    static String lang = "en_US";

    private static final ArrayList<Thread> threads = new ArrayList<>();
    static final ArrayList<FlashLauncher> frames = new ArrayList<>();

    private static int groupIndex = 0;
    static final ArrayList<TaskGroup> groups = new ArrayList<TaskGroup>() {
        @Override
        public boolean add(final TaskGroup g) {
            if (g == null)
                return false;
            synchronized (g.tasks) {
                if (g.tasks.isEmpty())
                    return false;
            }
            if (super.contains(g))
                return true;
            return super.add(g);
        }

        @Override
        public boolean remove(Object o) {
            final boolean r = super.remove(o);
            if (r && super.isEmpty())
                synchronized (this) { notifyAll(); }
            return r;
        }
    };
    static final ListMap<String, TaskGroup> iml = new ListMap<>();

    static TaskGroup loader = new TaskGroupAutoProgress(1);
    static final ConcurrentLinkedQueue<FLListener> listeners = new ConcurrentLinkedQueue<>();

    static final ArrayList<FLMenuItemListener>
            MENU_ITEMS = new ArrayList<>(),
            SETTINGS_ITEMS = new ArrayList<>(),
            HELP_ITEMS = new ArrayList<>()
    ;

    static final SyncVar<IAccount> latestAccount = new SyncVar<>();
    static final SyncVar<IProfile> latestProfile = new SyncVar<>();

    static final ArrayList<InstalledMeta> installed = new ArrayList<>();
    static final ArrayList<Market> markets = new ArrayList<>();
    static final ArrayList<IProfile> profiles = new ArrayList<>();
    static final ArrayList<IMaker<IProfile>> profileMakers = new ArrayList<>();
    static final ArrayList<IMaker<IAccount>> accountMakers = new ArrayList<>();
    static final ArrayList<IAccount> accounts = new ArrayList<>();

    static InstalledMeta getById(final String id) {
        if (id == null)
            return null;
        synchronized (installed) {
            for (final InstalledMeta im : installed)
                if (im.getID().equals(id))
                    return im;
        }
        return null;
    }

    public static boolean bindTaskGroup(final String id, final TaskGroup group) {
        if (id == null || id.isEmpty() || group == null)
            return false;
        synchronized (iml) {
            if (iml.containsKey(id))
                return false;
            iml.put(id, group);
            return true;
        }
    }

    public static boolean bindMeta(final InstalledMeta meta) {
        if (meta == null)
            return false;
        final String id = meta.getID();
        if (id == null || id.isEmpty())
            return false;
        synchronized (installed) {
            for (final InstalledMeta m : installed)
                if (id.equals(m.getID()))
                    return false;
            installed.add(meta);
            return true;
        }
    }

    public static void unbindMeta(final InstalledMeta meta) {
        synchronized (installed) {
            installed.remove(meta);
        }
    }

    public static void main(final String[] args) {
        final FastList<String> doNotUpdate = new FastList<>(new String[0]);

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--dev")) {
                if (arg.length() == 5) {
                    i++;
                    if (i < args.length) {
                        if (!args[i].isEmpty())
                            doNotUpdate.addAll(Arrays.asList(args[i].split(",")));
                        continue;
                    } else {
                        System.err.println("--dev package1,package2");
                        UI.UI.dispose();
                        System.exit(1);
                        return;
                    }
                } else if (arg.charAt(5) == '=') {
                    if (arg.length() != 6)
                        doNotUpdate.addAll(Arrays.asList(arg.substring(6).split(",")));
                    continue;
                } else {
                    System.err.println("--dev=package1,package2");
                    UI.UI.dispose();
                    System.exit(1);
                    return;
                }
            }
            System.err.println("Unrecognized argument: " + arg);
            UI.UI.dispose();
            System.exit(1);
            return;
        }

        if (!doNotUpdate.isEmpty()) {
            doNotUpdate.optimize();
            System.out.println("Updates will be skipped for: " + doNotUpdate);
        }

        try {
            server = new ServerSocket();
            server.bind(new InetSocketAddress("127.0.0.1", PORT));
        } catch (final Throwable ignored) {
            UI.UI.dispose();
            try (final Socket s = new Socket("127.0.0.1", PORT)) {
                final OutputStream os = s.getOutputStream();
                os.write(0);
                os.flush();
            } catch (final IOException ignored1) {}
            return;
        }

        if(!UI.check()) {
            System.err.println("Can't create window.");
            System.exit(1);
            return;
        }
        UI.initColors();

        try {
            Theme.current = Theme.parse(new IniGroup(new String(FS.ROOT.readFully(FlashLauncher.ID + "://themes/dark.ini"), StandardCharsets.UTF_8), false));

            String l = Locale.getDefault().toString();

            if (config.get("lang") instanceof String)
                l = config.getAsString("lang");
            else {
                config.put("lang", l);
                try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                    fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                } catch (final IOException ex) {
                    ex.printStackTrace();
                }
            }

            final boolean fle = FS.ROOT.exists(FlashLauncher.ID + "://langs/" + l + ".ini"), ule = FS.ROOT.exists("ui-lib://langs/" + l + ".ini");
            if (fle || ule)
                lang = l;
            else
                config.put("lang", "en_US");
            Lang.add(
                    new IniGroup(new String(FS.ROOT.readFully(FlashLauncher.ID + "://langs/lang-names.ini"), StandardCharsets.UTF_8), false),
                    new IniGroup(new String(
                            fle ? FS.ROOT.readFully(FlashLauncher.ID + "://langs/" + lang + ".ini") : FS.ROOT.readFully(FlashLauncher.ID + "://langs/en_US.ini"),
                            StandardCharsets.UTF_8), false),
                    new IniGroup(new String(
                            ule? FS.ROOT.readFully("ui-lib://langs/" + lang + ".ini") : FS.ROOT.readFully("ui-lib://langs/en_US.ini"),
                            StandardCharsets.UTF_8), false)
            );
            Lang.update();
        } catch (final Throwable ex) {
            ex.printStackTrace();
            System.exit(1);
            return;
        }

        markets.add(new Market("installed", FlashLauncher.ICON) {
            @Override public void checkForUpdates(final Meta... items) {}

            @Override
            public Meta[] find(final String query) {
                synchronized (installed) {
                    if (query.isEmpty())
                        return installed.toArray(new Meta[0]);
                    final ArrayList<Meta> l = new ArrayList<>();
                    for (final Meta m : installed)
                        if (m.getName().toString().contains(query))
                            l.add(m);
                    return l.toArray(new Meta[0]);
                }
            }

            {
                addCategory(Lang.get("categories.launcher"));
                addCategory(Lang.get("categories.library"));
            }
        });

        installed.add(new InstalledPlugin(FlashLauncher.ID, FlashLauncher.NAME, FlashLauncher.VERSION, FlashLauncher.AUTHOR, FlashLauncher.SHORT_DESCRIPTION) {
            final LangItem[] cl = new LangItem[] { Lang.get("categories.launcher") };

            @Override public Version getVersion() { return FlashLauncher.VERSION; }
            @Override public String getMarket() {
                synchronized (config) {
                    return config.group("plugins." + FlashLauncher.ID).getAsString("market");
                }
            }
            @Override public IImage getIcon() { return FlashLauncher.ICON; }
            @Override public Object[] getCategories() { return cl; }

            { file = Core.getFile(FLCore.class); }
        });

        loader.addTask(new Task() {
            final ClassLoader cl = ClassLoader.getSystemClassLoader();

            void loadAll(final String p1, final String p2) throws Throwable {
                final FSFile[] l = FS.ROOT.list(p1);
                if (l == null)
                    return;
                for (final FSFile f : l)
                    if (f.isDir())
                        loadAll(p1 + f.getName() + "/", p2 + f.getName() + ".");
                    else if (f.getName().endsWith(".class"))
                        cl.loadClass(p2 + f.getName().substring(0, f.getName().length() - 6));
            }

            @Override
            public void run() {
                try {
                    loadAll("://", "");
                } catch (final Throwable ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public String toString() {
                return "Loading classes ...";
            }
        });

        loader.addTask(new Task() {
            class PlayStatus {
                final RunProc rp;
                boolean finished = false;
                Process process = null;

                final ArrayList<LaunchListener> l = new ArrayList<>();

                public PlayStatus(final RunProc rp) { this.rp = rp; }
            }

            @Override
            public void run() {
                final LangItem langPlay = Lang.get("launcher.play"), langHome = Lang.get("launcher.home"),
                        langAdd = Lang.get("launcher.add"), langProfiles = Lang.get("profiles.name"), langAccounts = Lang.get("accounts.name");
                final Object
                        langProfileMakers = new Object() {
                            @Override
                            public String toString() {
                                return langProfiles + " > " + langAdd;
                            }
                        },
                        langAccountMakers = new Object() {
                            @Override
                            public String toString() {
                                return langAccounts + " > " + langAdd;
                            }
                        }
                    ;
                final int h = 48, y = (h - 32) / 2, y2 = (h - 30) / 2, o = 8, y3 = (h - 18) / 2;
                final RRunnable<Integer> br = () -> Theme.BORDER_RADIUS.run() * 2;
                final ListMap<FlashLauncher, PlayStatus> statusList = new ListMap<>();
                listeners.add(launcher -> {
                    synchronized (statusList) {
                        statusList.remove(launcher);
                    }
                });
                synchronized (HELP_ITEMS) {
                    HELP_ITEMS.add(new FLMenuItemListener("launcher", FlashLauncher.ICON, FlashLauncher.NAME) {
                        @Override
                        public void onOpen(final FLMenuItemEvent e) {
                            e.add(UI.text("WIP").size(e.width() - 24, 32).pos(8, 8));
                        }
                    });
                }
                synchronized (SETTINGS_ITEMS) {
                    SETTINGS_ITEMS.add(new FLMenuItemListener("launcher", FlashLauncher.ICON, FlashLauncher.NAME) {
                        @Override
                        public void onOpen(final FLMenuItemEvent e) {
                            final int cw = e.width() - 16, cwh = (cw - 8) / 2, cwhO = cwh + 16;
                            //e.add(UI.text("Thread count: " + Core.syncGetSize(threads)).ha(HAlign.LEFT).size(e.width() - 24, 18).pos(8, 8));
                            e.add(
                                    UI.text("Language:").size(cwh, 32).pos(8, 8).ha(HAlign.LEFT),
                                    UI.comboBox().text(Lang.get(Core.syncGetString(config, "lang"))).size(cwh, 32).pos(cwhO, 8).onList(event -> {
                                        final IContainer c = event.getContainer();
                                        c.size(cwh, 40 * langs.size() + 8).pos(cwhO, 48);
                                        int y = 8;
                                        final int cwhS = cwh - 16;
                                        for (final String l : langs) {
                                            c.add(
                                                    lang.equals(l) ?
                                                            UI.button(Lang.get(l)).size(cwhS, 32).pos(8, y).background(Theme.BACKGROUND_ACCENT_COLOR) :
                                                            UI.button(Lang.get(l)).size(cwhS, 32).pos(8, y).onAction((b, se) -> {
                                                                synchronized (config) {
                                                                    config.put("lang", l);
                                                                    try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                                        fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                                    } catch (final IOException ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                }
                                                                event.getSelf().text(Lang.get(l));
                                                                event.close();
                                                            })
                                            );
                                            y += 40;
                                        }
                                        return e.container;
                                    })
                            );
                        }
                    });
                }
                synchronized (MENU_ITEMS) {
                    MENU_ITEMS.add(new FLMenuItemListener("play", ICON_PLAY, null) {
                            public void update(final FlashLauncher launcher, final IContainer cont, final IContainer c) {
                                final PlayStatus status = Core.syncGet(statusList, launcher);
                                c.clear();
                                if (status == null || status.finished)
                                    c.add(
                                            UI.comboBox().text(new Object() {
                                                @Override
                                                public String toString() {
                                                    final IAccount i = launcher.account;
                                                    return i == null ? "None" : i.toString();
                                                }
                                            }).image(() -> {
                                                final IAccount i = launcher.account;
                                                if (i == null)
                                                    return null;
                                                final IImage ic = i.getIcon();
                                                return ic == null ? null : ic.getImage();
                                            }).imageOffset(4).size(200, 32).pos(y, y).onList(le -> {
                                                final IContainer container = le.getContainer();
                                                container.background(UI.TRANSPARENT).size(cont.width(), cont.height() - h - 8).pos(0, 0);
                                                final ContainerListBuilder clb = new ContainerListBuilder(UI.scrollPane()
                                                                .size(container.width(), container.height()).content(UI.panel().size(container.width(), container.height()).borderRadius(UI.ZERO)),
                                                        150, 8);
                                                container.add(clb);
                                                final Runnable r = Core.onNotifyLoop(accounts, () -> {
                                                    clb.clear();
                                                    for (final IAccount i : accounts)
                                                        clb.add(UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(20).onAction((s, e) -> {
                                                            latestAccount.set(launcher.account = i);
                                                            synchronized (config) {
                                                                final String a = i.getID();
                                                                if (a == null || a.isEmpty())
                                                                    config.remove("latestAccount");
                                                                else
                                                                    config.put("latestAccount", a);
                                                                try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                                    fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                                } catch (final IOException ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            }
                                                            le.close();
                                                        }));
                                                    clb.update();
                                                });
                                                final RRunnable<Boolean> r1 = () -> {
                                                    Core.offNotifyLoop(r);
                                                    return true;
                                                };
                                                le.getSelf().onCloseList(ignored -> {
                                                    Core.offNotifyLoop(r);
                                                    launcher.menuBar.offChange(r1);
                                                    return true;
                                                });
                                                launcher.menuBar.onChange(r1);
                                                return cont;
                                            }),
                                            UI.comboBox().text(new Object() {
                                                @Override
                                                public String toString() {
                                                    final IProfile i = launcher.profile;
                                                    return i == null ? "None" : i.toString();
                                                }
                                            }).image(() -> {
                                                final IProfile i = launcher.profile;
                                                if (i == null)
                                                    return null;
                                                final IImage ic = i.getIcon();
                                                return ic == null ? null : ic.getImage();
                                            }).imageOffset(4).size(200, 32).pos(y + 8 + 200, y).onList(le -> {
                                                final IContainer container = le.getContainer();
                                                container.background(UI.TRANSPARENT).size(cont.width(), cont.height() - h - 8).pos(0, 0);
                                                final ContainerListBuilder clb = new ContainerListBuilder(
                                                        UI.scrollPane().size(container.width(), container.height())
                                                                .content(UI.panel().size(container.width(), container.height()).borderRadius(UI.ZERO)),
                                                        150, 8);
                                                container.add(clb);
                                                final Runnable r = Core.onNotifyLoop(profiles, () -> {
                                                    clb.clear();
                                                    for (final IProfile i : profiles)
                                                        clb.add(UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(20).onAction((s, e) -> {
                                                            latestProfile.set(launcher.profile = i);
                                                            synchronized (config) {
                                                                final String a = i.getID();
                                                                if (a == null || a.isEmpty())
                                                                    config.remove("latestProfile");
                                                                else
                                                                    config.put("latestProfile", a);
                                                                try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                                    fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                                } catch (final IOException ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            }
                                                            le.close();
                                                        }));
                                                    clb.update();
                                                });
                                                le.getSelf().onCloseList(ignored -> {
                                                    Core.offNotifyLoop(r);
                                                    return true;
                                                });
                                                return cont;
                                            }),
                                            UI.button(langPlay, ICON_PLAY).imageOffset(6).size(112, 32).pos(400 + y + 16, y).onAction((self, event) -> {
                                                final IAccount acc = launcher.account;
                                                final IProfile pro = launcher.profile;
                                                if (acc == null || pro == null || !acc.isCompatible(pro) || !pro.isCompatible(acc))
                                                    return;
                                                final PlayStatus s = new PlayStatus(new RunProc(launcher, acc, pro));

                                                {
                                                    LaunchListener listener = pro.init(s.rp);
                                                    if (listener != null)
                                                        s.l.add(listener);

                                                    listener = acc.init(s.rp);
                                                    if (listener != null)
                                                        s.l.add(listener);
                                                }

                                                synchronized (statusList) {
                                                    statusList.put(launcher, s);
                                                }

                                                update(launcher, cont, c);

                                                new Thread(() -> {
                                                    try {
                                                        for (final LaunchListener ll : s.l)
                                                            ll.preLaunch();
                                                        while (true) {
                                                            final TaskGroup g = getUnfinishedGroup(s.rp.groups);
                                                            if (g != null)
                                                                g.waitFinish();
                                                            else
                                                                break;
                                                        }
                                                        for (final LaunchListener ll : s.l)
                                                            ll.launch();
                                                        while (true) {
                                                            final TaskGroup g = getUnfinishedGroup(s.rp.groups);
                                                            if (g != null)
                                                                g.waitFinish();
                                                            else
                                                                break;
                                                        }
                                                        synchronized (s.rp.groups) {
                                                            s.rp.groups.clear();
                                                            s.rp.generalObjects.clear();
                                                        }
                                                        System.gc();
                                                        if (s.rp.beginArgs.isEmpty() && s.rp.args.isEmpty() && s.rp.endArgs.isEmpty()) {
                                                            System.out.println("No arguments!");
                                                            for (final LaunchListener ll : s.l)
                                                                ll.cancel();
                                                            return;
                                                        }

                                                        final Process proc;

                                                        try {
                                                            final ArrayList<String> args = new ArrayList<>();

                                                            args.addAll(s.rp.beginArgs);
                                                            args.addAll(s.rp.args);
                                                            args.addAll(s.rp.endArgs);

                                                            /*System.out.print("Arguments:");
                                                            for (final String arg : args)
                                                                System.out.print(arg.contains(" ") ? " \"" + arg + '"' : ' ' + arg);
                                                            System.out.println();*/

                                                            final ProcessBuilder b = new ProcessBuilder(
                                                                    args.toArray(new String[0])
                                                            ).directory(s.rp.workDir);
                                                            b.environment().putAll(s.rp.env);
                                                            proc = s.process = b.start();
                                                        } catch (final Exception ex) {
                                                            for (final LaunchListener ll : s.l)
                                                                ll.cancel();
                                                            throw ex;
                                                        }

                                                        s.rp.beginArgs.clear();
                                                        s.rp.args.clear();
                                                        s.rp.endArgs.clear();
                                                        s.rp.env.clear();

                                                        synchronized (s.rp.l) {
                                                            s.rp.r = true;
                                                        }

                                                        final Thread
                                                                t1 = new Thread(() -> {
                                                                    try (
                                                                            final InputStreamReader ro = new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8);
                                                                            final CLineReader o = new CLineReader()
                                                                    ) {
                                                                        final char[] buf = new char[256];
                                                                        int l;
                                                                        while ((l = ro.read(buf, 0, buf.length)) != -1) {
                                                                            for (final LaunchListener ll : s.l)
                                                                                ll.out(buf, l);
                                                                            o.write(buf, 0, l);
                                                                            while (o.hasNextLine()) {
                                                                                final String line = o.nextLine();
                                                                                for (final LaunchListener ll : s.l)
                                                                                    ll.outLine(line);
                                                                            }
                                                                        }
                                                                    } catch (final Exception ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                    for (final LaunchListener ll : s.l)
                                                                        ll.closeOut();
                                                                    System.out.println("Output closed");
                                                                }),
                                                                t2 = new Thread(() -> {
                                                                    try (
                                                                            final InputStreamReader ro = new InputStreamReader(proc.getErrorStream(), StandardCharsets.UTF_8);
                                                                            final CLineReader o = new CLineReader()
                                                                    ) {
                                                                        final char[] buf = new char[256];
                                                                        int l;
                                                                        while ((l = ro.read(buf, 0, buf.length)) != -1) {
                                                                            for (final LaunchListener ll : s.l)
                                                                                ll.err(buf, l);
                                                                            o.write(buf, 0, l);
                                                                            if (o.hasNextLine()) {
                                                                                final String line = o.nextLine();
                                                                                for (final LaunchListener ll : s.l)
                                                                                    ll.errLine(line);
                                                                            }
                                                                        }
                                                                    } catch (final Exception ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                    for (final LaunchListener ll : s.l)
                                                                        ll.closeErr();
                                                                    System.out.println("Error closed");
                                                                });
                                                        t1.start();
                                                        t2.start();

                                                        System.out.println("Finished " + proc.waitFor());
                                                        for (final LaunchListener ll : s.l)
                                                            ll.exit(proc.exitValue());
                                                        s.process = null;
                                                    } catch (final Throwable ex) {
                                                        ex.printStackTrace();
                                                    } finally {
                                                        synchronized (s.rp.l) {
                                                            s.rp.r = false;
                                                        }
                                                        s.finished = true;
                                                        synchronized (statusList) {
                                                            statusList.remove(launcher, s);
                                                        }
                                                        launcher.frame.visible(true);
                                                    }
                                                }).start();
                                            }),
                                            UI.button(langHome, FSChooser.ICON_FOLDER).imageOffset(6).size(96, 32).pos(c.width() - y - 96, y).onAction((s, e) -> {
                                                try {
                                                    final File f = launcher.profile.home(launcher.account);
                                                    if (f != null) {
                                                        if (!f.exists())
                                                            f.mkdirs();
                                                        if (f.exists() && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN))
                                                            Desktop.getDesktop().open(f);
                                                    }
                                                } catch (final Throwable ex) {
                                                    ex.printStackTrace();
                                                }
                                            })
                                    ).update();
                                else if (status.process == null) {
                                    final IText st = UI.text();
                                    status.rp.statusWidth = c.width() - (y2 + o) * 2;
                                    c.add(st.ha(HAlign.LEFT).size(status.rp.statusWidth, 18).pos(y2 + o, y2)).update();
                                    final int w = st.width();
                                    new Thread() {
                                        private IProgressBar[] pbl = new IProgressBar[0];
                                        private Runnable[] rl = new Runnable[0];
                                        private final Thread t = new Thread(() -> {
                                            try {
                                                while (true) {
                                                    Task t = null;
                                                    synchronized (status.rp.groups) {
                                                        for (final TaskGroup g2 : status.rp.groups) {
                                                            final Task t2 = g2.getAny();
                                                            if (t2 != null) {
                                                                t = t2;
                                                                break;
                                                            }
                                                        }
                                                    }
                                                    if (t != null) {
                                                        st.text(t).update();
                                                        t.waitFinish();
                                                    } else if (status.process != null || status.finished) {
                                                        interrupt();
                                                        join();
                                                        update(launcher, cont, c);
                                                        break;
                                                    }
                                                }
                                            } catch (final InterruptedException ignored) {}
                                        }) {{
                                            start();
                                        }};

                                        private boolean mbc() {
                                            interrupt();
                                            t.interrupt();
                                            return true;
                                        }

                                        @Override
                                        public void run() {
                                            final ArrayList<TaskGroup> gl = status.rp.groups;
                                            try {
                                                while (true)
                                                    synchronized (gl) {
                                                        final int s = gl.size();
                                                        if (pbl.length != s) {
                                                            final int pbw = (w + 8) / s - 8;
                                                            if (s > pbl.length) {
                                                                int i = pbl.length;
                                                                pbl = Arrays.copyOf(pbl, s);
                                                                rl = Arrays.copyOf(rl, s);
                                                                for (; i < s; i++) {
                                                                    final TaskGroup g = gl.get(i);
                                                                    final IProgressBar pb = UI.progressBar();
                                                                    c.add(pbl[i] = pb);
                                                                    rl[i] = Core.onNotifyLoop(g.po, () -> pb.maxProgress(g.m).progress(g.p).update());
                                                                }
                                                            } else {
                                                                for (int i = s; i < pbl.length; i++) {
                                                                    c.remove(pbl[i]);
                                                                    Core.offNotifyLoop(rl[i]);
                                                                }
                                                                pbl = Arrays.copyOf(pbl, s);
                                                                rl = Arrays.copyOf(rl, s);
                                                            }
                                                            int x = y2 + o;
                                                            for (final IProgressBar pb : pbl) {
                                                                pb.size(pbw, 4).pos(x, y2 + 18 + 8);
                                                                x += pbw + 8;
                                                            }
                                                        }
                                                        c.update();
                                                        gl.wait();
                                                    }
                                            } catch (final InterruptedException ignored) {}
                                            launcher.menuBar.offChange(this::mbc);
                                            for (final Runnable r : rl)
                                                Core.offNotifyLoop(r);
                                            rl = null;
                                        }

                                        {
                                            launcher.menuBar.onChange(this::mbc);
                                            start();
                                        }
                                    };
                                } else {
                                    final IText st = UI.text("Running ...");
                                    c.add(st.ha(HAlign.LEFT).size(c.width() - (y3 + o) * 2, 18).pos(y3 + o, y3)).update();
                                    new Thread(() -> {
                                        try {
                                            final Process p = status.process;
                                            if (p != null)
                                                p.waitFor();
                                            update(launcher, cont, c);
                                        } catch (final Throwable ex) {
                                            ex.printStackTrace();
                                        }
                                    }).start();
                                }
                            }

                            @Override
                            public void onOpen(final FLMenuItemEvent e) {
                                final IContainer c2 = UI.panel().size(e.width(), h).pos(0, e.height() - h).borderRadius(br);
                                e.add(c2);
                                update(e.launcher, e.container, c2);
                            }
                    });
                    MENU_ITEMS.add(new FLMenuItemListener("profiles", ICON_PROFILES, null) {
                        @Override
                        public void onOpen(final FLMenuItemEvent e) {
                            new Object() {
                                final IText title = UI.text().ha(HAlign.LEFT).size(e.width() - 56, 32).pos(48, 8);
                                final IContainer tp = UI.panel().size(e.width(), 48).add(title);

                                final IImageView icon = UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(ICON_PROFILES).size(32, 32).pos(8, 8);
                                final ContainerListBuilder b = new ContainerListBuilder(UI.scrollPane()
                                        .size(e.width(), e.height() - 56).pos(0, 56).content(UI.panel().borderRadius(UI.ZERO)), 150, 8);

                                final ConcurrentLinkedQueue<Runnable> cl = new ConcurrentLinkedQueue<>();

                                final IButton
                                        back = UI.button("<").size(32, 32).pos(8, 8).onAction((s, evt) -> {
                                            tp.remove(s);
                                            mainPage();
                                        }), back2 = UI.button("<").size(32, 32).pos(8, 8).onAction((s, evt) -> {
                                            tp.remove(s);
                                            for (final Runnable r : cl)
                                                r.run();
                                            cl.clear();
                                            makers();
                                        })
                                ;

                                // List
                                Runnable rpu = null;
                                Runnable[] rl = null;

                                void open(final IProfile i) {
                                    if (rpu != null) {
                                        Core.offNotifyLoop(rpu);
                                        rpu = null;
                                    }
                                    if (rl != null) {
                                        for (final Runnable plr : rl)
                                            Core.offNotify(plr);
                                        rl = null;
                                    }

                                    title.text(new Object() {
                                        @Override
                                        public String toString() {
                                            return langProfiles + " > " + i;
                                        }
                                    });
                                    final IContainer container = UI.panel().size(e.width(), e.height() - 56).pos(0, 56);
                                    e.clear().add(tp.add(back), container);
                                    i.open(new IEditorContext() {
                                        final Object l = new Object();
                                        boolean f = false;

                                        @Override public int width() { return container.width(); }
                                        @Override public int height() { return container.height(); }

                                        @Override
                                        public IEditorContext add(final IComponent component) {
                                            container.add(component);
                                            return this;
                                        }

                                        @Override
                                        public IEditorContext add(final IComponent... components) {
                                            container.add(components);
                                            return this;
                                        }

                                        @Override
                                        public IContainer getContainer() {
                                            return container;
                                        }

                                        @Override
                                        public void close() {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                f = true;
                                            }
                                            tp.remove(back);
                                            cl.clear();
                                            mainPage();
                                        }

                                        @Override public boolean isClosed() { synchronized (l) { return f; } }

                                        @Override
                                        public void onClose(final Runnable runnable) {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                cl.add(runnable);
                                            }
                                        }

                                        {
                                            cl.add(() -> f = true);
                                        }
                                    });
                                    tp.update();
                                    container.update();
                                }

                                void make(final IMaker<IProfile> m) {
                                    if (rpu != null) {
                                        Core.offNotifyLoop(rpu);
                                        rpu = null;
                                    }
                                    if (rl != null) {
                                        for (final Runnable plr : rl)
                                            Core.offNotifyLoop(plr);
                                        rl = null;
                                    }

                                    title.text(new Object() {
                                        @Override
                                        public String toString() {
                                            return langProfiles + " > " + langAdd + " > " + m;
                                        }
                                    });
                                    final IContainer container = UI.panel().background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(e.width(), e.height() - 56).pos(0, 56);
                                    e.clear().add(tp.add(back2), container);
                                    m.make(new IMakerContext<IProfile>() {
                                        final Object l = new Object();
                                        boolean f = false;

                                        @Override public IContainer getContainer() { return container; }

                                        @Override
                                        public IProfile end(final IProfile result) {
                                            synchronized (l) {
                                                if (f)
                                                    return result;
                                                f = true;
                                            }
                                            cl.clear();
                                            tp.remove(back2);
                                            if (result == null)
                                                makers();
                                            else
                                                open(result);
                                            return result;
                                        }

                                        @Override public boolean isFinished() { synchronized (l) { return f; } }

                                        @Override
                                        public void onCancel(final Runnable runnable) {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                cl.add(runnable);
                                            }
                                        }

                                        {
                                            cl.add(() -> f = true);
                                        }
                                    });
                                    tp.update();
                                    container.update();
                                }

                                void makers() {
                                    title.text(langProfileMakers);
                                    e.clear().add(tp.add(back), b);
                                    tp.update();

                                    if (rpu != null)
                                        Core.offNotifyLoop(rpu);
                                    rpu = Core.onNotifyLoop(profileMakers, () -> {
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        rl = new Runnable[profileMakers.size()];
                                        b.clear();
                                        int index = 0;
                                        for (final IMaker<IProfile> i : profileMakers) {
                                            final IButton btn = UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(24);
                                            rl[index++] = Core.onNotify(i, b::update);
                                            b.add(btn.onAction((s, e) -> {
                                                tp.remove(back);
                                                make(i);
                                            }));
                                        }
                                        b.update();
                                    });
                                }

                                void mainPage() {
                                    title.text(langProfiles);
                                    e.clear().add(tp.add(icon), b);
                                    tp.update();

                                    if (rpu != null)
                                        Core.offNotifyLoop(rpu);
                                    rpu = Core.onNotifyLoop(profiles, () -> {
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        rl = new Runnable[profiles.size()];
                                        b.clear();
                                        int index = 0;
                                        for (final IProfile i : profiles) {
                                            final IButton btn = UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(24);
                                            rl[index++] = Core.onNotify(i, b::update);
                                            b.add(btn.onAction((s, e) -> {
                                                tp.remove(icon);
                                                open(i);
                                            }));
                                        }
                                        b.add(UI.button(langAdd, ICON_ADD).imageAlign(ImgAlign.TOP).imageOffset(24).onAction((s, e) -> {
                                            tp.remove(icon);
                                            makers();
                                        })).update();
                                    });
                                }

                                {
                                    e.launcher.menuBar.onChange(() -> {
                                        if (rpu != null)
                                            Core.offNotifyLoop(rpu);
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        for (final Runnable r : cl)
                                            r.run();
                                        cl.clear();
                                        return true;
                                    });
                                    mainPage();
                                }
                            };
                        }
                    });
                    MENU_ITEMS.add(new FLMenuItemListener("market", ICON_MARKET, null) {
                        @Override
                        public void onOpen(final FLMenuItemEvent e) {
                            final LangItem all = Lang.get("market.all"), mixed = Lang.get("market.mixed");

                            final IComboBox filter = UI.comboBox().text(all).imageOffset(4).size(160, 32).pos(0, 0);
                            final ContainerListBuilder ilb;
                            {
                                final IContainer c;
                                ilb = new ContainerListBuilder(
                                        UI.scrollPane()
                                                .content(c = UI.panel().borderRadius(UI.ZERO))
                                                .size(e.width(), e.height() - 80)
                                                .pos(0, 40),
                                        //c.width() / 2 - 16,
                                        e.width() - 16,
                                        120, 8);
                                if (c instanceof SPanel)
                                    ((SPanel) c).onTransfer(new TransferListener() {
                                        @Override
                                        public boolean canImport(final TransferEvent event) {
                                            return event.isDrop() && event.hasFileListSupport();
                                        }

                                        @Override
                                        public boolean onImport(final TransferEvent event) {
                                            if (!canImport(event))
                                                return false;

                                            try {
                                                for (final File f : event.getFileList())
                                                    try {
                                                        final ListMap<String, byte[]> l = IO.toMap(f);
                                                        if (l.containsKey("fl-plugin.ini") || l.containsKey("fl-info.ini")) {
                                                            final IniGroup g = new IniGroup(new String(l.get(l.containsKey("fl-plugin.ini") ? "fl-plugin.ini" : "fl-info"), StandardCharsets.UTF_8), false);
                                                            final String id = g.getAsString("id");
                                                            final TaskGroupAutoProgress gr = new TaskGroupAutoProgress(1);
                                                            if (!bindTaskGroup(id, gr))
                                                                continue;
                                                            gr.addTask(new InstallPluginTask(l));
                                                            synchronized (groups) {
                                                                groups.add(gr);
                                                                groups.notifyAll();
                                                            }
                                                        }
                                                    } catch (final Exception ex) {
                                                        ex.printStackTrace();
                                                    }
                                            } catch (final Exception ex) {
                                                ex.printStackTrace();
                                            }
                                            return true;
                                        }
                                    });
                            }

                            final ITextField findField = UI.textField("").hint(Lang.get("market.hint")).size(e.width() - 208, 32).pos(168, 0);

                            final IText cg;
                            final IProgressBar pb2;
                            final int cgw = 128;
                            e.add(UI.panel().size(cgw, 32).pos(e.width() - cgw, e.height() - 32).add(
                                    cg = UI.text().size(32, 32),
                                    pb2 = UI.progressBar().size(cgw - 32 - 13, 6).pos(32, 13)
                            ));

                            final ArrayList<Market> mle = new ArrayList<>();

                            final AtomicBoolean srr = new AtomicBoolean(true);
                            final ArrayList<Runnable> listeners = new ArrayList<>();
                            final Runnable1a<Runnable1a> sr = (sr1) -> {
                                final String q = findField.text();
                                //ilb.clear().childSize(c.width() / 2 - 16, ilb.getChildHeight());
                                //ilb.clear().childSize(c.width() - 16, ilb.getChildHeight());
                                ilb.clear().childSize(e.width() - 24, ilb.getChildHeight());

                                final Market[] ml;
                                synchronized (mle) {
                                    if (mle.isEmpty())
                                        synchronized (markets) {
                                            ml = markets.toArray(new Market[0]);
                                        }
                                    else
                                        ml = mle.toArray(new Market[0]);
                                }

                                final ListMap<String, ArrayList<FixedEntry<Market, Meta>>> mel = new ListMap<>();

                                for (final Market m : ml) {
                                    final Meta[] l = m.find(q);
                                    if (l == null)
                                        continue;
                                    for (final Meta me : l) {
                                        final ArrayList<FixedEntry<Market, Meta>> i = mel.get(me.getID());
                                        if (i == null)
                                            mel.put(me.getID(), new ArrayList<FixedEntry<Market, Meta>>() {{
                                                add(new FixedEntry<>(m, me));
                                            }});
                                        else
                                            i.add(new FixedEntry<>(m, me));
                                    }
                                }

                                /*final IFont font = Theme.FONT;
                                int tw = ilb.getChildWidth() - Meta.ICON_SIZE - 72, sdw = ilb.getChildWidth() - 16;
                                for (final Map.Entry<String, ArrayList<FixedEntry<Market, Meta>>> emm : mel.entrySet()) {
                                    final Meta me = emm.getValue().get(0).getValue();
                                    final Object sd = me.getShortDescription();
                                    if (UI.stringWidth(font, me.getName().toString()) > tw || (sd != null && UI.stringWidth(font, me.getShortDescription().toString()) > sdw)) {
                                        ilb.childSize(c.width() - 16, ilb.getChildHeight());
                                        tw = ilb.getChildWidth() - Meta.ICON_SIZE - 72;
                                        sdw = ilb.getChildWidth() - 16;
                                        break;
                                    }
                                }*/
                                final int tw = ilb.getChildWidth() - Meta.ICON_SIZE - 72, sdw = ilb.getChildWidth() - 16;

                                for (final Map.Entry<String, ArrayList<FixedEntry<Market, Meta>>> emm : mel.entrySet()) {
                                    final String id = emm.getKey();
                                    final FixedEntry<Market, Meta> i = emm.getValue().get(0);
                                    final Meta me = i.getValue();

                                    final IText name = UI.text().ha(HAlign.LEFT).size(tw, 20).pos(Meta.ICON_SIZE + 16, 8);
                                    final IImage icon = me.getIcon();
                                    final IImageView iconView = icon == null ? null : UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(me.getIcon()).size(Meta.ICON_SIZE, Meta.ICON_SIZE).pos(8, 8);
                                    final IContainer ic = UI.panel().add(
                                            name.text(new Object() {
                                                final Object n = me.getName(), v = me.getVersion();

                                                @Override
                                                public String toString() {
                                                    return v == null ? n == null ? "Unnamed" : n.toString() : n + " (" + v + ")";
                                                }
                                            }),
                                            UI.text(me.getAuthor()).foreground(Theme.AUTHOR_FOREGROUND_COLOR).ha(HAlign.LEFT).size(tw, 16).pos(Meta.ICON_SIZE + 16, 30),
                                            UI.text(me.getShortDescription()).ha(HAlign.LEFT).size(sdw, ilb.getChildHeight() - Meta.ICON_SIZE - 24).pos(8, Meta.ICON_SIZE + 16),
                                            UI.button().borderRadius(UI.ZERO).background(UI.TRANSPARENT).size(ilb.getChildWidth(), ilb.getChildHeight()).onAction((self, event) -> {
                                                final IComponent[] cl = e.childs();
                                                e.clear().add(
                                                        UI.panel().size(e.width(), 128).add(
                                                                UI.button("<").size(32, 32).pos(8, 8).onAction((self2, event1) -> e.clear().add(cl).update())
                                                        )
                                                ).update();
                                            })
                                    );

                                    if (iconView != null) {
                                        ic.add(iconView.smooth(me.smoothIcon()));
                                        if (icon instanceof ChangeableImage) {
                                            if (icon instanceof LoadingImage)
                                                if (!((LoadingImage) icon).isFinished()) {
                                                    final IComponent loader = UI.loader().size(iconView.width(), iconView.height()).pos(8, 8);
                                                    ic.add(loader);
                                                    listeners.add(Core.onNotifyLoop(icon, self -> {
                                                        if (((LoadingImage) icon).isFinished()) {
                                                            synchronized (sr1) {
                                                                Core.offNotifyLoop(self);
                                                                listeners.remove(self);
                                                            }
                                                            ic.remove(loader).update();
                                                        }
                                                        iconView.update();
                                                    }));
                                                }
                                        } else
                                            listeners.add(Core.onNotifyLoop(icon, iconView::update));
                                    }

                                    final Object[] cats = me.getCategories();
                                    if (cats != null) {
                                        int x = Meta.ICON_SIZE + 12, w;
                                        for (final Object cat : cats) {
                                            final String cs = cat.toString();
                                            w = Math.round(UI.stringWidth(Theme.FONT, cs)) + 16;
                                            ic.add(UI.button(cs).grounds(Theme.CATEGORIES_BACKGROUND_COLOR, Theme.CATEGORIES_FOREGROUND_COLOR).size(w, 22).pos(x, 36 + 12));
                                            x += w + 4;
                                        }
                                    }

                                    final TaskGroup g;
                                    synchronized (iml) {
                                        g = iml.get(id);
                                    }

                                    if (g == null) {
                                        final InstalledMeta im = getById(id);
                                        final Version
                                                mev = me == null ? null : me.getVersion(),
                                                imv1 = im == null ? null : im.ver,
                                                imv2 = im == null ? null : im.getVersion();
                                        if (im != null) {
                                            if (ml.length == 1 && mev != null && !mev.equals(imv1) && !mev.equals(imv2)) {
                                                ic.add(UI.button(ICON_UPDATE).imageOffset(2)
                                                        .grounds(Theme.CATEGORIES_BACKGROUND_COLOR, Theme.CATEGORIES_FOREGROUND_COLOR)
                                                        .size(40, 20).pos(ilb.getChildWidth() - 48, 8).onAction((self, event) -> {
                                                            final TaskGroup gr = i.getValue().install();
                                                            if (!bindTaskGroup(me.getID(), gr))
                                                                return;

                                                            synchronized (groups) {
                                                                groups.add(gr);
                                                                groups.notifyAll();
                                                            }
                                                            synchronized (config) {
                                                                config.group("plugins." + id).put("market", i.getKey().getID());
                                                                try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                                    fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                                } catch (final IOException ex) {
                                                                    ex.printStackTrace();
                                                                }
                                                            }
                                                        }));
                                                name.text(new Object() {
                                                    final Object n = me.getName(), v1 = im.getVersion(), v2 = me.getVersion();

                                                    @Override
                                                    public String toString() {
                                                        return n + " (" + v1 + " -> " + v2 + ")";
                                                    }
                                                });
                                            } else {
                                                if (id.equals(FlashLauncher.ID)) {
                                                    final Object v1 = me.getVersion(), v2 = im.ver;
                                                    if (!v1.equals(v2))
                                                        name.text(new Object() {
                                                            final Object n = me.getName();

                                                            @Override
                                                            public String toString() {
                                                                return n + " (" + v1 + " -> " + v2 + ")";
                                                            }
                                                        });
                                                } else
                                                    ic.add(UI.button(ICON_DELETE).imageOffset(2)
                                                            .grounds(Theme.CATEGORIES_BACKGROUND_COLOR, Theme.CATEGORIES_FOREGROUND_COLOR)
                                                            .size(40, 20).pos(ilb.getChildWidth() - 48, 8).onAction((self, event) -> {
                                                                synchronized (installed) {
                                                                    installed.remove(im);
                                                                }
                                                                if (im instanceof InstalledPlugin) {
                                                                    final InstalledPlugin ip = (InstalledPlugin) im;
                                                                    synchronized (ip.c) {
                                                                        ip.disable();
                                                                        ip.file.delete();
                                                                    }
                                                                }
                                                                synchronized (config) {
                                                                    config.remove("plugins." + im.getID());
                                                                    try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                                        fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                                    } catch (final IOException ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                }
                                                                synchronized (groups) {
                                                                    try {
                                                                        final TaskGroup gr = im.onDelete();
                                                                        if (gr != null && bindTaskGroup(im.getID(), gr))
                                                                            groups.add(gr);
                                                                    } catch (final Throwable ex) {
                                                                        ex.printStackTrace();
                                                                    }
                                                                    groups.notifyAll();
                                                                }
                                                            }));
                                            }
                                        } else if (emm.getValue().size() == 1)
                                            ic.add(UI.button(ml.length == 1 ? ICON_INSTALL : i.getKey().getIcon()).imageOffset(2).grounds(Theme.CATEGORIES_BACKGROUND_COLOR, Theme.CATEGORIES_FOREGROUND_COLOR)
                                                    .size(40, 20).pos(ilb.getChildWidth() - 48, 8).onAction((self, event) -> {
                                                final TaskGroup gr = i.getValue().install();
                                                if (!bindTaskGroup(me.getID(), gr))
                                                    return;
                                                synchronized (groups) {
                                                    groups.add(gr);
                                                    groups.notifyAll();
                                                }
                                                synchronized (config) {
                                                    config.group("plugins." + me.getID()).put("market", i.getKey().getID());
                                                    try (final FileOutputStream fos = new FileOutputStream(new File(Core.getPath(FLCore.class), "config.ini"))) {
                                                        fos.write(config.toString().getBytes(StandardCharsets.UTF_8));
                                                    } catch (final IOException ex) {
                                                        ex.printStackTrace();
                                                    }
                                                }
                                            }));
                                        else
                                            ic.add(
                                                    UI.comboBox().grounds(Theme.CATEGORIES_BACKGROUND_COLOR, Theme.CATEGORIES_FOREGROUND_COLOR).imageOffset(4)
                                                            .size(40, 20).pos(ilb.getChildWidth() - 48, 8).onList(le -> {
                                                        le.getContainer().size(e.width(), e.height());
                                                        return e.container;
                                                    }),
                                                    UI.button(i.getKey().getIcon()).imageOffset(2).size(20, 20).pos(ilb.getChildWidth() - 48, 8).background(UI.TRANSPARENT)
                                            );
                                    } else {
                                        final IProgressBar pb = UI.progressBar().size(ilb.getChildWidth(), 4).pos(0, ilb.getChildHeight() - 4);
                                        ic.add(pb);
                                        final Runnable r1 = Core.onNotifyLoop(g.po, () -> pb.maxProgress(g.m).progress(g.p).update());
                                        listeners.add(() -> Core.offNotifyLoop(r1));
                                    }

                                    ilb.add(ic);
                                }

                                ilb.update();
                            };

                            final Runnable glr;
                            final Runnable[] ul = new Runnable[]{null};
                            Core.onNotifyLoop(groups, glr = () -> {
                                final int countGroups = groups.size();
                                cg.text(countGroups).update();
                                synchronized (sr) {
                                    if (srr.get()) {
                                        if (countGroups > 0) {
                                            final TaskGroup tg = groups.get(0);
                                            if (ul[0] != null)
                                                Core.offNotifyLoop(ul[0]);
                                            ul[0] = Core.onNotifyLoop(tg.po, () -> pb2.maxProgress(tg.m).progress(tg.p).update());
                                        } else {
                                            pb2.maxProgress(1).progress(1).update();
                                            if (ul[0] != null) {
                                                Core.offNotifyLoop(ul[0]);
                                                ul[0] = null;
                                            }
                                        }
                                        sr.run(sr);
                                    }
                                }
                            });
                            e.launcher.menuBar.onChange(() -> {
                                synchronized (sr) {
                                    srr.set(false);
                                    for (final Runnable lr : listeners)
                                        lr.run();
                                    listeners.clear();
                                    if (ul[0] != null)
                                        Core.offNotifyLoop(ul[0]);
                                }
                                Core.offNotifyLoop(glr);
                                return true;
                            });

                            e.add(ilb.update(),
                                    filter.onList(le -> {
                                        final IComboBox self = le.getSelf();
                                        final IContainer container = le.getContainer().size(e.width(), e.height() - 40).pos(0, 40);
                                        final ContainerListBuilder
                                                clb = new ContainerListBuilder(
                                                UI.scrollPane()
                                                        .size(e.width() - 158, container.height())
                                                        .borderRadius(UI.ZERO)
                                                        .content(UI.panel().borderRadius(UI.ZERO)),
                                                150, 8),
                                                clb2 = new ContainerListBuilder(
                                                        UI.scrollPane()
                                                                .borderRadius(UI.ZERO)
                                                                .size(158, container.height())
                                                                .pos(e.width() - 158, 0)
                                                                .content(UI.panel().borderRadius(UI.ZERO)),
                                                        134, 32, 8);

                                        final Runnable
                                                rc = Core.onNotifyLoop(markets, () -> {
                                                    clb2.clear();
                                                    final Market[] ml;
                                                    synchronized (mle) {
                                                        if (mle.isEmpty())
                                                            synchronized (markets) {
                                                                ml = markets.toArray(new Market[0]);
                                                            }
                                                        else
                                                            ml = mle.toArray(new Market[0]);
                                                    }
                                                    for (final Market m : ml)
                                                        if (!m.cl.isEmpty()) {
                                                            if (m.getIcon() == null)
                                                                clb2.add(UI.text(m.getName()).ha(HAlign.LEFT));
                                                            else
                                                                clb2.add(
                                                                        UI.panel().add(
                                                                                UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(m.getIcon()).size(24, 24).pos(4, 4),
                                                                                UI.text(m.getName()).size(94, 24).pos(32, 4).ha(HAlign.LEFT)
                                                                        )
                                                                );
                                                            for (final Object cat : m.getCategories())
                                                                clb2.add(UI.toggleButton(cat, null, false).onChange((s, n) -> {
                                                                    synchronized (sr) {
                                                                        if (srr.get())
                                                                            sr.run(sr);
                                                                    }
                                                                }));
                                                        }
                                                    clb2.update();
                                                }),
                                                r = Core.onNotifyLoop(markets, () -> {
                                                    clb.clear();
                                                    synchronized (mle) {
                                                        for (final Market m : markets)
                                                            clb.add(UI.toggleButton(m.getName(), m.getIcon(), mle.contains(m))
                                                                    .imageAlign(ImgAlign.TOP).imageOffset(16).imageTextDist(4)
                                                                    .smooth(m.smoothIcon()).onChange((self1, newValue) -> {
                                                                synchronized (mle) {
                                                                    if (newValue) {
                                                                        mle.add(m);
                                                                        final int mleS = mle.size();
                                                                        if (mleS > 1)
                                                                            if (mleS == Core.syncGetSize(markets))
                                                                                filter.text(all).image(null).update();
                                                                            else
                                                                                filter.text(mixed).image(null).update();
                                                                        else
                                                                            filter.text(m.getName()).image(m.getIcon()).update();
                                                                    } else {
                                                                        mle.remove(m);
                                                                        final int mleS = mle.size();
                                                                        if (mleS > 1)
                                                                            if (mleS == Core.syncGetSize(markets))
                                                                                filter.text(all).image(null).update();
                                                                            else
                                                                                filter.text(mixed).image(null).update();
                                                                        else if (mleS == 1) {
                                                                            final Market m2 = mle.get(0);
                                                                            filter.text(m2.getName()).image(m2.getIcon()).update();
                                                                        } else
                                                                            filter.text(all).image(null).update();
                                                                    }
                                                                }
                                                                rc.run();
                                                                synchronized (sr) {
                                                                    if (srr.get())
                                                                        sr.run(sr);
                                                                }
                                                            }));
                                                    }
                                                    clb.update();
                                                });

                                        self.onCloseList(le1 -> {
                                            self.offCloseList(le1.getSelfListener());
                                            Core.offNotifyLoop(r);
                                            return true;
                                        });



                                        /*clb2.add(
                                                UI.toggleButton("Game Version", null, false),
                                                UI.toggleButton("Release", null, false),
                                                UI.toggleButton("Snapshot", null, false),
                                                UI.toggleButton("Alpha", null, false),
                                                UI.toggleButton("PreAlpha", null, false),
                                                UI.toggleButton("Game Assets", null, false),
                                                UI.toggleButton("Fabric", null, false),
                                                UI.toggleButton("Forge", null, false),
                                                UI.toggleButton("LiteLoader", null, false),
                                                UI.toggleButton("Mod", null, false),
                                                UI.toggleButton("Plugin", null, false),
                                                UI.toggleButton("test", null, false),
                                                UI.toggleButton("test 2", null, false)
                                        );*/

                                        container.background(UI.TRANSPARENT).add(clb, clb2.update());
                                        return e.container;
                                    }),
                                    findField.onAction(self -> {
                                        synchronized (sr) {
                                            if (srr.get())
                                                sr.run(sr);
                                        }
                                    }),
                                    UI.button(ICON_FIND).imageOffset(4).size(32, 32).pos(e.width() - 32, 0).onAction((self, event) -> {
                                        synchronized (sr) {
                                            if (srr.get())
                                                sr.run(sr);
                                        }
                                    })
                            );
                        }
                    });
                    MENU_ITEMS.add(new FLMenuItemListener("accounts", ICON_ACCOUNTS, null) {
                        @Override
                        public void onOpen(final FLMenuItemEvent e) {
                            new Object() {
                                final IText title = UI.text().ha(HAlign.LEFT).size(e.width() - 56, 32).pos(48, 8);
                                final IContainer tp = UI.panel().size(e.width(), 48).add(title);

                                final IImageView icon = UI.imageView(ImagePosMode.CENTER, ImageSizeMode.SCALE).image(ICON_ACCOUNTS).size(32, 32).pos(8, 8);
                                final ContainerListBuilder b = new ContainerListBuilder(UI.scrollPane()
                                        .size(e.width(), e.height() - 56).pos(0, 56).content(UI.panel().borderRadius(UI.ZERO)),
                                        150, 8);

                                final ConcurrentLinkedQueue<Runnable> cl = new ConcurrentLinkedQueue<>();

                                final IButton
                                        back = UI.button("<").size(32, 32).pos(8, 8).onAction((s, evt) -> {
                                            tp.remove(s);
                                            mainPage();
                                        }), back2 = UI.button("<").size(32, 32).pos(8, 8).onAction((s, evt) -> {
                                            tp.remove(s);
                                            for (final Runnable r : cl)
                                                r.run();
                                            cl.clear();
                                            makers();
                                        });

                                // List
                                Runnable rpu = null;
                                Runnable[] rl = null;

                                void open(final IAccount i) {
                                    if (rpu != null) {
                                        Core.offNotifyLoop(rpu);
                                        rpu = null;
                                    }
                                    if (rl != null) {
                                        for (final Runnable plr : rl)
                                            Core.offNotify(plr);
                                        rl = null;
                                    }

                                    title.text(new Object() { @Override public String toString() { return langAccounts + " > " + i; } });
                                    final IContainer container = UI.panel().background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(e.width(), e.height() - 56).pos(0, 56);
                                    e.clear().add(tp.add(back), container);
                                    i.open(new IEditorContext() {
                                        final Object l = new Object();
                                        boolean f = false;

                                        @Override public int width() { return container.width(); }
                                        @Override public int height() { return container.height(); }

                                        @Override
                                        public IEditorContext add(final IComponent component) {
                                            container.add(component);
                                            return this;
                                        }

                                        @Override
                                        public IEditorContext add(final IComponent... components) {
                                            container.add(components);
                                            return this;
                                        }

                                        @Override
                                        public IContainer getContainer() {
                                            return container;
                                        }

                                        @Override
                                        public void close() {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                f = true;
                                            }
                                            tp.remove(back);
                                            cl.clear();
                                            mainPage();
                                        }

                                        @Override
                                        public boolean isClosed() {
                                            synchronized (l) {
                                                return f;
                                            }
                                        }

                                        @Override
                                        public void onClose(final Runnable runnable) {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                cl.add(runnable);
                                            }
                                        }

                                        {
                                            cl.add(() -> f = true);
                                        }
                                    });
                                    tp.update();
                                    container.update();
                                }

                                void make(final IMaker<IAccount> m) {
                                    if (rpu != null) {
                                        Core.offNotifyLoop(rpu);
                                        rpu = null;
                                    }
                                    if (rl != null) {
                                        for (final Runnable plr : rl)
                                            Core.offNotifyLoop(plr);
                                        rl = null;
                                    }

                                    title.text(new Object() {
                                        @Override
                                        public String toString() {
                                            return langAccounts + " > " + langAdd + " > " + m;
                                        }
                                    });
                                    final IContainer container = UI.panel().background(UI.TRANSPARENT).borderRadius(UI.ZERO).size(e.width(), e.height() - 56).pos(0, 56);
                                    e.clear().add(tp.add(back2), container);
                                    m.make(new IMakerContext<IAccount>() {
                                        final Object l = new Object();
                                        boolean f = false;

                                        @Override
                                        public IContainer getContainer() {
                                            return container;
                                        }

                                        @Override
                                        public IAccount end(final IAccount result) {
                                            synchronized (l) {
                                                if (f)
                                                    return result;
                                                f = true;
                                            }
                                            cl.clear();
                                            tp.remove(back2);
                                            if (result == null)
                                                makers();
                                            else
                                                open(result);
                                            return result;
                                        }

                                        @Override
                                        public boolean isFinished() {
                                            synchronized (l) {
                                                return f;
                                            }
                                        }

                                        @Override
                                        public void onCancel(final Runnable runnable) {
                                            synchronized (l) {
                                                if (f)
                                                    return;
                                                cl.add(runnable);
                                            }
                                        }

                                        {
                                            cl.add(() -> f = true);
                                        }
                                    });
                                    tp.update();
                                    container.update();
                                }

                                void makers() {
                                    title.text(langAccountMakers);
                                    e.clear().add(tp.add(back), b);
                                    tp.update();

                                    if (rpu != null)
                                        Core.offNotifyLoop(rpu);
                                    rpu = Core.onNotifyLoop(accountMakers, () -> {
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        rl = new Runnable[accountMakers.size()];
                                        b.clear();
                                        int index = 0;
                                        for (final IMaker<IAccount> i : accountMakers) {
                                            final IButton btn = UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(24);
                                            rl[index++] = Core.onNotify(i, b::update);
                                            b.add(btn.onAction((s, e) -> {
                                                tp.remove(back);
                                                make(i);
                                            }));
                                        }
                                        b.update();
                                    });
                                }

                                void mainPage() {
                                    title.text(langAccounts);
                                    e.clear().add(tp.add(icon), b);
                                    tp.update();

                                    if (rpu != null)
                                        Core.offNotifyLoop(rpu);
                                    rpu = Core.onNotifyLoop(accounts, () -> {
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        rl = new Runnable[accounts.size()];
                                        b.clear();
                                        int index = 0;
                                        for (final IAccount i : accounts) {
                                            final IButton btn = UI.button(i, i.getIcon()).imageAlign(ImgAlign.TOP).imageOffset(24);
                                            rl[index++] = Core.onNotify(i, b::update);
                                            b.add(btn.onAction((s, e) -> {
                                                tp.remove(icon);
                                                open(i);
                                            }));
                                        }
                                        b.add(UI.button(langAdd, ICON_ADD).imageAlign(ImgAlign.TOP).imageOffset(24).onAction((s, e) -> {
                                            tp.remove(icon);
                                            makers();
                                        })).update();
                                    });
                                }

                                {
                                    e.launcher.menuBar.onChange(() -> {
                                        if (rpu != null)
                                            Core.offNotifyLoop(rpu);
                                        if (rl != null)
                                            for (final Runnable plr : rl)
                                                Core.offNotify(plr);
                                        for (final Runnable r : cl)
                                            r.run();
                                        cl.clear();
                                        return true;
                                    });
                                    mainPage();
                                }
                            };
                        }
                    });
                    MENU_ITEMS.notify();
                }
            }

            @Override
            public String toString() {
                return "Loading FlashLauncher";
            }
        });

        loader.addTask(new Task() {
            class pi {
                String id, author, sd, main, market;
                Object name;
                Version ver;
                IImage icon = null;
                boolean smoothIcon = true;
                Object[] cats;
                File f;
                FSRoot root;

                final ListMap<String, String> dependencies = new ListMap<>();
                final ListMap<String, String> optional = new ListMap<>();
            }

            pi load(final File path, final FSRoot root, final IniGroup g) {
                final pi r = new pi();

                r.root = root;
                r.id = g.getAsString("id");
                r.author = g.getAsString("author");
                r.sd = g.getAsString("shortDescription");
                r.name = g.getAsString("name");
                final String verStr = g.getAsString("version");
                if (r.id == null || verStr == null || r.id.equals("flash-launcher"))
                    return null;
                r.ver = new Version(verStr);
                r.f = path;
                final String cs = g.getAsString("categories");
                final ArrayList<LangItem> cl = new ArrayList<>();
                if (cs != null)
                    for (final String cat : cs.split("\\|"))
                        if (!cat.replaceAll(" ", "").replaceAll("\t", "").isEmpty())
                            cl.add(Lang.get("categories." + cat));
                r.cats = cl.toArray();
                r.main = g.has("main") ? g.getAsString("main") : null;
                synchronized (config) {
                    r.market = config.group("plugins." + r.id).getAsString("market");
                }

                try {
                    if (root.exists("icon.png"))
                        r.icon = UI.image(root.readFully("icon.png"));
                    else if (root.exists("icon.jpg"))
                        r.icon = UI.image(root.readFully("icon.jpg"));
                    if (r.icon != null)
                        r.smoothIcon = g.getAsBool("smooth-icon", true);
                } catch (final Exception ex) {
                    ex.printStackTrace();
                }

                final String dl = g.getAsString("dependencies"), ol = g.getAsString("optional");
                if (dl != null)
                    for (final String d : dl.split(";")) {
                        if (d.isEmpty())
                            continue;
                        final int ii = d.indexOf(':');
                        if (ii == -1) {
                            if (d.equals(FlashLauncher.ID))
                                continue;
                            r.dependencies.put(d, "*");
                        } else {
                            final String id = d.substring(0, ii), ver = d.substring(ii + 1);
                            if (id.equals(FlashLauncher.ID))
                                if (FlashLauncher.VERSION.isCompatibility(ver))
                                    continue;
                                else
                                    System.out.println("Skipped " + id + " because launcher version is " + FlashLauncher.VERSION + ", but requirement is " + ver);
                            r.dependencies.put(id, ver);
                        }
                    }
                if (ol != null)
                    for (final String d : ol.split(";")) {
                        if (d.isEmpty())
                            continue;
                        final int ii = d.indexOf(':');
                        if (ii == -1)
                            r.optional.put(d, "*");
                        else
                            r.optional.put(d.substring(0, ii), d.substring(ii + 1));
                    }

                return r;
            }

            @Override
            public void run() {
                try {
                    final ListMap<String, ArrayList<pi>> mp = new ListMap<>();
                    final File[] dirs = new File[] { new File(LAUNCHER_DIR, "plugins") };
                    for (final File d : dirs) {
                        final File[] l = d.listFiles();
                        if (l == null)
                            continue;
                        for (final File f : l) {
                            final IniGroup ig;
                            final FSRoot root = FS.newFS(f);
                            if (root.exists("fl-plugin.ini"))
                                ig = new IniGroup(new String(root.readFully("fl-plugin.ini"), StandardCharsets.UTF_8), false);
                            else
                                continue;
                            final pi p = load(f, root, ig);
                            if (p == null)
                                continue;
                            final ArrayList<pi> lp = mp.get(p.id);
                            if (lp == null)
                                mp.put(p.id, new ArrayList<pi>() {{ add(p); }});
                            else
                                lp.add(p);
                        }
                    }

                    ListMap<String, pi> loaded = new ListMap<>();
                    boolean a = true;
                    while (a) {
                        a = false;
                        for (final ArrayList<pi> pl : mp.values())
                            s:
                            for (final pi p : pl) {
                                for (final Map.Entry<String, String> d : p.dependencies.entrySet()) {
                                    final pi dp = loaded.get(d.getKey());
                                    if (dp == null || !dp.ver.isCompatibility(d.getValue()))
                                        continue s;
                                }
                                for (final Map.Entry<String, String> d : p.optional.entrySet()) {
                                    final pi dp = loaded.get(d.getKey());
                                    if (dp != null && dp.ver.isCompatibility(d.getValue()))
                                        continue s;
                                }
                                loaded.put(p.id, p);
                                mp.remove(p.id);
                                a = true;
                            }
                    }
                    a = true;
                    while (a) {
                        a = false;
                        for (final ArrayList<pi> pl : mp.values())
                            s:
                            for (final pi p : pl) {
                                for (final Map.Entry<String, String> d : p.dependencies.entrySet()) {
                                    final pi dp = loaded.get(d.getKey());
                                    if (dp == null || !dp.ver.isCompatibility(d.getValue()))
                                        continue s;
                                }
                                loaded.put(p.id, p);
                                mp.remove(p.id);
                                a = true;
                            }
                    }
                    mp.clear();
                    synchronized (installed) {
                        for (final pi p : loaded.values())
                            installed.add(new InstalledPlugin(p.id, p.name, p.ver, p.author, p.sd) {
                                {
                                    icon = p.icon;
                                    smoothIcon = p.smoothIcon;
                                    root = p.root;
                                    file = p.f;
                                    cats = p.cats;
                                    main = p.main;
                                    market = p.market;

                                    dl.putAll(p.dependencies);
                                    ol.putAll(p.optional);
                                }
                            });
                    }
                } catch (final Throwable ex) {
                    ex.printStackTrace();
                }
            }

            @Override public String toString() { return "Loading plugins ..."; }
        });

        loader.addTask(new Task() {
            @Override
            public void run() {
                synchronized (installed) {
                    for (final InstalledMeta im : installed)
                        if (im instanceof InstalledPlugin)
                            ((InstalledPlugin) im).enable();
                }
            }

            @Override
            public String toString() {
                return "Enabling plugins ...";
            }
        });

        loader.addTask(new Task() {
            @Override
            public void run() {
                final Market[] ml;
                synchronized (markets) { ml = markets.toArray(new Market[0]); }
                final InstalledMeta[] mel;
                synchronized (installed) { mel = installed.toArray(new InstalledMeta[0]); }
                for (final Market m : ml) {
                    final String id = m.getID();
                    final ArrayList<InstalledMeta> cml = new ArrayList<>();
                    for (final InstalledMeta meta : mel)
                        if (id.equals(meta.market) && !doNotUpdate.contains(meta.getID()))
                            cml.add(meta);
                    if (cml.isEmpty())
                        m.checkForUpdates();
                    else
                        m.checkForUpdates(cml.toArray(new InstalledMeta[0]));
                }
                System.gc();
            }

            final LangItem ch = Lang.get("market.checkingForUpdates");
            @Override public String toString() { return ch.toString(); }
        });

        loader.addTask(new Task() {
            @Override
            public void run() throws Throwable {
                synchronized (config) {
                    final String a = config.getAsString("latestAccount"), p = config.getAsString("latestProfile");
                    if (a != null && !a.isEmpty())
                        synchronized (accounts) {
                            for (final IAccount acc : accounts)
                                if (a.equals(acc.getID())) {
                                    latestAccount.set(acc);
                                    break;
                                }
                        }
                    if (p != null && !p.isEmpty())
                        synchronized (accounts) {
                            for (final IProfile pro : profiles)
                                if (p.equals(pro.getID())) {
                                    latestProfile.set(pro);
                                    break;
                                }
                        }
                }
            }

            @Override public String toString() { return "Finalizing ..."; }
        });

        groups.add(loader);

        new Thread(() -> {
            try {
                final TaskGroup lg = loader;
                lg.waitFinish();
                synchronized (lg) {
                    loader = null;
                    lg.notifyAll();
                }
            } catch (final InterruptedException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }).start();

        for (int i = 0, l = Runtime.getRuntime().availableProcessors(); i < l; i++)
            startThread();

        new Thread(() -> {
            try {
                while (true) {
                    final Socket socket = server.accept();
                    new Thread(() -> {
                        try (final Socket s = socket) {
                            new FlashLauncher().menuBar.select("play");
                        } catch (final IOException ignored) {}
                    }).start();
                }
            } catch (final IOException ignored) {}
        }).start();

        new FlashLauncher().menuBar.select("play");

        final Runnable r = Core.onNotify(MENU_ITEMS, () -> {
            synchronized (frames) {
                for (final FlashLauncher f : frames)
                    f.updateMenuBar();
            }
        });

        new Thread(() -> {
            final ObjLocker lf = new ObjLocker(frames), lg = new ObjLocker(groups);
            while (true)
                try {
                    lf.lock();
                    lg.lock();
                    if (frames.isEmpty() && groups.isEmpty())
                        break;
                    Core.waitM(lf, lg);
                } catch (final InterruptedException ignored) {
                    break;
                } finally {
                    lg.unlock();
                    lf.unlock();
                }
            try {
                server.close();
            } catch (final IOException ignored) {}
            Core.offNotify(r);
            synchronized (installed) {
                for (final InstalledMeta m : installed)
                    if (m instanceof InstalledPlugin)
                        ((InstalledPlugin) m).disable();
            }
            synchronized (groups) {
                if (!groups.isEmpty())
                    System.out.println("Exit. Skip groups: " + groups.size());
            }
            synchronized (threads) {
                for (final Thread t : threads)
                    t.interrupt();
            }
            UI.UI.dispose();
            System.exit(0);
        }).start();
    }

    private static void startThread() {
        synchronized (threads) {
            final Thread t = new Thread(FLCore::threadRun, "Task Runner");
            t.setPriority(Thread.MAX_PRIORITY);
            threads.add(t);
            t.start();
        }
    }

    private static void threadRun() {
        while (true)
            try {
                runAnyTask(null);
            } catch (final InterruptedException ignored) {
                break;
            }
        synchronized (threads) {
            threads.remove(Thread.currentThread());
            if (threads.isEmpty())
                threads.notifyAll();
        }
    }

    public static boolean isTaskThread() {
        synchronized (threads) {
            return threads.contains(Thread.currentThread());
        }
    }

    static void runAnyTask(final ObjLocker w) throws InterruptedException {
        final ObjLocker l = new ObjLocker(groups);
        Task t = null;
        l.lock();
        final int s = groups.size(), i = s > groupIndex ? groupIndex : 0;
        for (; groupIndex < s; groupIndex++) {
            final TaskGroup g = groups.get(groupIndex);
            final Task st = g.lockAny();
            if (st != null) {
                t = st;
                break;
            }
        }
        if (t == null && s > 0)
            for (groupIndex = 0; groupIndex < i; groupIndex++) {
                final TaskGroup g = groups.get(groupIndex);
                final Task st = g.lockAny();
                if (st != null) {
                    t = st;
                    break;
                }
            }
        if (t == null) {
            try {
                if (w == null)
                    l.waitNotify();
                else
                    Core.waitM(l, w);
            } catch (final InterruptedException ex) {
                throw ex;
            } finally {
                l.unlock();
                if (w != null)
                    w.unlock();
            }
        } else {
            l.unlock();
            t.LRun();
        }
    }

    public static TaskGroup getUnfinishedGroup(final List<TaskGroup> groups) {
        synchronized (groups) {
            for (final TaskGroup g : groups)
                synchronized (g) {
                    if (!g.f)
                        return g;
                }
        }
        return null;
    }
}