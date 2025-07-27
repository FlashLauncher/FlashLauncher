package illa4257.flashlauncher;

import illa4257.flashlauncher.accounts.Account;
import illa4257.flashlauncher.instances.Instance;
import illa4257.i4Framework.base.Framework;
import illa4257.i4Framework.base.events.components.StyleUpdateEvent;
import illa4257.i4Framework.base.styling.BaseTheme;
import illa4257.i4Framework.base.utils.CSSParser;
import illa4257.i4Framework.base.utils.Cache;
import illa4257.i4Framework.swing.SwingFramework;
import illa4257.i4Utils.*;
import illa4257.i4Utils.io.IO;
import illa4257.i4Utils.io.MultiSocket;
import illa4257.i4Utils.io.MultiSocketFactory;
import illa4257.i4Utils.io.MultiSocketServer;
import illa4257.i4Utils.lang.LangMgr;
import illa4257.i4Utils.lists.ArrNotifier;
import illa4257.i4Utils.logger.*;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.*;

import static illa4257.i4Utils.logger.Level.INFO;

public class FlashLauncher {
    public static final byte[] APP_ID = new byte[] { 102, 108, 97, 115, 104, 45, 108, 97, 117, 110, 99, 104, 101, 114 };
    public static final int PORT = 53789;
    public static final SemVer VERSION = new SemVer("0.0.0-dev+0");
    public static final i4Logger L = new i4Logger("FlashLauncher")
            .registerHandler(new AnsiColoredPrintStreamLogHandler(System.out));

    public static final ArrNotifier<LogRecord> LOGS = new ArrNotifier<>(new LogRecord[16]);
    static final ExecutorService threadPoolShort = Executors.newCachedThreadPool();
    public static Framework framework;
    static final QueueTrigger<JavaInfo> portableJavaList = new QueueTrigger<>(), localJavaList = new QueueTrigger<>();

    static final QueueTrigger<Account> accounts = new QueueTrigger<>();
    static final QueueTrigger<Instance> instances = new QueueTrigger<>();

    protected static final LinkedBlockingQueue<Task> tasks = new LinkedBlockingQueue<>();
    private static final int threadNumber = 2;

    protected static volatile TaskGroup loader;

    public static final LangMgr lang = new LangMgr();

    private static void init(final MultiSocketServer server) throws Exception {
        framework.addThemeListener(FlashLauncher::onThemeUpdate);
        onThemeUpdate(framework.getTheme(), framework.getBaseTheme());

        Cache.images.putIfAbsent("background2", framework.getImage("assets:///illa4257/flashlauncher/images/backgrounds/background2.png"));
        Cache.images.putIfAbsent("background1", framework.getImage("assets:///illa4257/flashlauncher/images/backgrounds/background1.jpg"));

        L.registerHandler(new LogHandler() {
            @Override
            public void log(final Level level, final String prefix, final String message) {
                LOGS.add(new LogRecord(level, prefix, message));
            }
        });

        L.log(INFO, "FlashLauncher " + VERSION.format());

        new Thread() {
            {
                setName("Service Listener");
                setDaemon(true);
                setPriority(Thread.MIN_PRIORITY);
            }

            @Override
            public void run() {
                try {
                    //noinspection InfiniteLoopStatement
                    while (true) {
                        final MultiSocket socket = server.accept();
                        threadPoolShort.submit(() -> {
                            try (final MultiSocket s = socket; final InputStream is = s.getInputStream()) {
                                final byte[] id = IO.readByteArray(is, IO.readByteI(is));
                                if (Arrays.equals(id, APP_ID)) {
                                    if (IO.readByteI(is) == 0)
                                        new FlashLauncherWindow();
                                }
                            } catch (final Exception ex) {
                                L.log(ex);
                            }
                        });
                    }
                } catch (final Exception ex) {
                    if (ex instanceof SocketException && ("Socket closed".equals(ex.getMessage()) || "Socket is closed".equals(ex.getMessage())))
                        return;
                    L.log(ex);
                }
            }
        }.start();

        final ConcurrentLinkedQueue<Task> loaderTasks = new ConcurrentLinkedQueue<>();

        try {
            final File cfg = new File(framework.getLocalAppDataDir(), "config.json");
            if (cfg.exists()) {
                System.out.println("Parse " + cfg);
            }
        } catch (final Exception ex) {
            L.log(ex);
        }

        loaderTasks.offer(new Task() {
            @Override
            protected void run() throws Exception {
                portableJavaList.add(JavaInfo.check(new File(System.getProperty("java.home") + (Arch.JVM.IS_WINDOWS ? "/bin/java.exe" : "/bin/java"))));
            }
        });
        loaderTasks.offer(new Task() {
            @Override
            protected void run() {
                lang.put("play", "Play");
            }
        });

        runTaskGroup(loader = new TaskGroup(loaderTasks));

        new FlashLauncherWindow();

        for (int i = 0; i < threadNumber; i++)
            new TaskRunner().start();
    }

    public static void onThemeUpdate(final String theme, final BaseTheme baseTheme) {
        framework.stylesheet.clear();
        try (final InputStreamReader r = new InputStreamReader(framework.openResource("assets:///illa4257/flashlauncher/styles/" + baseTheme.name().toLowerCase() + ".css"))) {
            CSSParser.parse(framework.stylesheet, r);
        } catch (final Exception ex) {
            L.log(ex);
        }
        try (final InputStreamReader r = new InputStreamReader(framework.openResource("assets:///illa4257/i4Framework/" + baseTheme.name().toLowerCase() + ".css"))) {
            CSSParser.parse(framework.stylesheet, r);
        } catch (final Exception ex) {
            L.log(ex);
        }
        framework.fireAllWindows(new StyleUpdateEvent());
    }

    public static void main(final String[] args) throws Exception {
        L.inheritGlobalIO();

        final MultiSocketFactory f = new MultiSocketFactory(APP_ID, PORT);
        final MultiSocketServer serv = f.reserve();
        if (serv == null) {
            try (final MultiSocket s = f.connect()) {
                if (s == null)
                    return;
                final OutputStream os = s.getOutputStream();
                os.write(0);
                os.flush();
            } catch (final Exception ex) {
                L.log(ex);
            }
            f.close();
            return;
        }
        framework = new SwingFramework("FlashLauncher");
        init(serv);
    }

    public static void runTask(final Task task) { tasks.offer(task); }
    public static void runTaskGroup(final TaskGroup task) {
        for (final Task t : task.tasks)
            if (t != null)
                tasks.offer(t);
        tasks.offer(task);
    }
}