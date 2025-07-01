package illa4257.flashlauncher;

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
import illa4257.i4Utils.logger.AnsiColoredPrintStreamLogHandler;
import illa4257.i4Utils.logger.i4Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FlashLauncher {
    public static final byte[] APP_ID = new byte[] { 102, 108, 97, 115, 104, 45, 108, 97, 117, 110, 99, 104, 101, 114 };
    public static final int PORT = 53789;
    public static final i4Logger L = new i4Logger("FlashLauncher")
            .registerHandler(new AnsiColoredPrintStreamLogHandler(System.out));

    static final ExecutorService threadPoolShort = Executors.newCachedThreadPool();

    public static Framework framework;

    static final QueueObserver.ObservedQueue<JavaInfo> portableJavaList = new QueueObserver.ObservedQueue<>(),
            localJavaList = new QueueObserver.ObservedQueue<>();

    public static void init(final MultiSocketServer server) throws Exception {
        framework.addThemeListener(FlashLauncher::onThemeUpdate);
        onThemeUpdate(framework.getTheme(), framework.getBaseTheme());

        Cache.images.putIfAbsent("background2", framework.getImage("assets:///illa4257/flash-launcher/images/backgrounds/background2.png"));
        Cache.images.putIfAbsent("background1", framework.getImage("assets:///illa4257/flash-launcher/images/backgrounds/background1.jpg"));

        portableJavaList.triggers.add(() -> framework.invokeLater(portableJavaList::tick));
        localJavaList.triggers.add(() -> framework.invokeLater(localJavaList::tick));

        new Thread() {
            {
                setName("Service Listener");
                setDaemon(true);
                setPriority(Thread.MIN_PRIORITY);
            }

            @Override
            public void run() {
                try {
                    while (true) {
                        final MultiSocket socket = server.accept();
                        threadPoolShort.submit(() -> {
                            try (final MultiSocket s = socket; final InputStream is = s.getInputStream()) {
                                final byte[] id = IO.readByteArray(is, IO.readByteI(is));
                                if (Arrays.equals(id, FlashLauncher.APP_ID)) {
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

        new FlashLauncherWindow();
    }

    public static void onThemeUpdate(final String theme, final BaseTheme baseTheme) {
        framework.stylesheet.clear();
        try (final InputStreamReader r = new InputStreamReader(framework.openResource("assets:///illa4257/flash-launcher/styles/" + baseTheme.name().toLowerCase() + ".css"))) {
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
        framework = SwingFramework.INSTANCE;
        init(serv);
    }
}