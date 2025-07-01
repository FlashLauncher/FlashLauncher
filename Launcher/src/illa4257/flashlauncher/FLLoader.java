package illa4257.flashlauncher;

import illa4257.i4Framework.base.Framework;
import illa4257.i4Framework.base.utils.CSSParser;
import illa4257.i4Framework.base.utils.Cache;
import illa4257.i4Utils.*;
import illa4257.i4Utils.io.IO;
import illa4257.i4Utils.io.MultiSocket;
import illa4257.i4Utils.io.MultiSocketServer;
import illa4257.i4Utils.logger.i4Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FLLoader {
    static final ExecutorService threadPoolShort = Executors.newCachedThreadPool();

    public static Loader.ResourceClassLoader classLoader;
    public static Framework framework;
    public static i4Logger log;

    static final QueueObserver.ObservedQueue<JavaInfo> portableJavaList = new QueueObserver.ObservedQueue<>(),
            localJavaList = new QueueObserver.ObservedQueue<>();

    public static void init(final MultiSocketServer server) throws Exception {
        classLoader = (Loader.ResourceClassLoader) FLLoader.class.getClassLoader();
        log = (i4Logger) FLInit.logger;

        final Loader.ResourceClassLoader frameworkLoader = (Loader.ResourceClassLoader) Loader.loaders.get("i4Framework");
        classLoader.add(frameworkLoader);

        final Loader.ResourceClassLoader frameworkSwingExt = new Loader.ResourceClassLoader(frameworkLoader, "i4Framework.swing.jar");
        Loader.loaders.put("i4Framework.swing", frameworkSwingExt);
        classLoader.add(frameworkSwingExt);

        framework = (Framework) classLoader.loadClass("illa4257.i4Framework.swing.SwingFramework").getField("INSTANCE").get(null);

        Cache.images.putIfAbsent("background2", framework.getImage("assets:///flash-launcher/images/backgrounds/background2.png"));
        Cache.images.putIfAbsent("background1", framework.getImage("assets:///flash-launcher/images/backgrounds/background1.jpg"));

        CSSParser.parse(FlashLauncher.stylesheet.stylesheet, new InputStreamReader(framework.openResource("assets:///flash-launcher/styles/light.css")));
        CSSParser.parse(FlashLauncher.stylesheet.stylesheet, new InputStreamReader(framework.openResource("assets:///illa4257/i4Framework/light.css")));

        portableJavaList.triggers.add(() -> framework.invokeLater(portableJavaList::tick));
        localJavaList.triggers.add(() -> framework.invokeLater(localJavaList::tick));

        new Thread(() -> {
            try {
                while (true) {
                    final MultiSocket socket = server.accept();
                    threadPoolShort.submit(() -> {
                        try (final MultiSocket s = socket; final InputStream is = s.getInputStream()) {
                            final byte[] id = IO.readByteArray(is, IO.readByteI(is));
                            if (Arrays.equals(id, FLInit.APP_ID)) {
                                if (IO.readByteI(is) == 0)
                                    new FlashLauncher();
                                return;
                            }
                        } catch (final Exception ex) {
                            log.log(ex);
                        }
                    });
                }
            } catch (final Exception ex) {
                if (ex instanceof SocketException && ("Socket closed".equals(ex.getMessage()) || "Socket is closed".equals(ex.getMessage())))
                    return;
                log.log(ex);
            }
        }).start();

        new FlashLauncher();
    }
}