package illa4257.flashlauncher;

import illa4257.i4Framework.base.CSSParser;
import illa4257.i4Framework.base.Cache;
import illa4257.i4Framework.base.Framework;
import illa4257.i4Framework.base.Image;
import illa4257.i4Utils.MultiSocket;
import illa4257.i4Utils.MultiSocketServer;

import javax.imageio.ImageIO;
import java.io.InputStreamReader;
import java.net.Socket;

public class FLLoader {
    public static Loader.ResourceClassLoader classLoader;
    public static Framework framework;

    public static void init(final MultiSocketServer server) throws Exception {
        classLoader = (Loader.ResourceClassLoader) FLLoader.class.getClassLoader();

        final Loader.ResourceClassLoader frameworkLoader = (Loader.ResourceClassLoader) Loader.loaders.get("i4Framework");

        final Loader.ResourceClassLoader frameworkSwingExt = new Loader.ResourceClassLoader(frameworkLoader, "i4Framework.swing.jar");
        Loader.loaders.put("i4Framework.swing", frameworkSwingExt);
        classLoader.add(frameworkSwingExt);

        framework = (Framework) classLoader.loadClass("illa4257.i4Framework.swing.SwingFramework").getField("INSTANCE").get(null);

        Cache.images.putIfAbsent("background2", new Image(ImageIO.read(classLoader.getResourceAsStream("assets/flash-launcher/images/backgrounds/background2.png"))));
        Cache.images.putIfAbsent("background1", new Image(ImageIO.read(classLoader.getResourceAsStream("assets/flash-launcher/images/backgrounds/background1.jpg"))));

        CSSParser.parse(FlashLauncher.stylesheet.stylesheet, new InputStreamReader(classLoader.getResourceAsStream("assets/flash-launcher/styles/base.css")));

        new Thread(() -> {
            try {
                while (true) {
                    final MultiSocket s = server.accept();
                    System.out.println(s);
                    System.out.println(s.getInputStream().read());
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        }).start();

        new FlashLauncher();
    }
}