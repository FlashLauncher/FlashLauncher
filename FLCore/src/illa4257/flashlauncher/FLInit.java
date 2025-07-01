package illa4257.flashlauncher;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class FLInit {
    /* Configuration */
    public static final byte[] APP_ID = new byte[] { 102, 108, 97, 115, 104, 45, 108, 97, 117, 110, 99, 104, 101, 114 };
    public static final int PORT = 53789;

    /* Reserved */
    public static PrintStream out, err;
    public static InputStream in;
    public static Object logger;


    public static void main(final String... args) throws Exception {
        in = System.in;
        out = System.out;
        err = System.err;

        final Loader.ResourceClassLoader utils = new Loader.ResourceClassLoader(FLInit.class.getClassLoader(), "i4Utils.jar");
        Loader.loaders.put("i4Utils", utils);

        final Class<?> loggerClass = utils.loadClass("illa4257.i4Utils.logger.i4Logger");
        logger = loggerClass.getConstructor(String.class).newInstance("FlashLauncher");

        {
            final Class<?> levels = utils.loadClass("illa4257.i4Utils.logger.Level");

            final Class<?> logHandlerClass = utils.loadClass("illa4257.i4Utils.logger.ILogHandler");
            final Constructor<?> curLogHandlerConstructor = utils
                    .loadClass("illa4257.i4Utils.logger.AnsiColoredPrintStreamLogHandler").getConstructor(
                            PrintStream.class
                    );

            loggerClass.getMethod("registerHandler", logHandlerClass).invoke(logger, curLogHandlerConstructor.newInstance(out));

            //loggerClass.getMethod("inheritGlobalIO").invoke(loggerClass.getField("INSTANCE").get(null));
        }

        final Class<?> ms = utils.loadClass("illa4257.i4Utils.io.MultiSocketFactory");
        final Object s = ms.getConstructor(byte[].class, int.class).newInstance(APP_ID, PORT);

        final Object server = ms.getMethod("reserve").invoke(s);
        if (server == null) {
            final Object o = ms.getMethod("connect").invoke(s);
            if (o == null)
                return;
            try (final Closeable c = (Closeable) o) {
                final Class<?> socket = utils.loadClass("illa4257.i4Utils.io.MultiSocket");
                final OutputStream os = (OutputStream) socket.getMethod("getOutputStream").invoke(c);
                os.write(APP_ID.length);
                os.write(APP_ID);
                os.write(0);
                os.flush();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        final Loader.ResourceClassLoader framework = new Loader.ResourceClassLoader(utils, "i4Framework.jar");
        Loader.loaders.put("i4Framework", framework);

        final Loader.ResourceClassLoader frameworkDesktop = new Loader.ResourceClassLoader(framework, "i4Framework.desktop.jar");
        Loader.loaders.put("i4Framework", frameworkDesktop);

        final Loader.ResourceClassLoader launcher = new Loader.ResourceClassLoader(frameworkDesktop, "Launcher.jar");
        Loader.loaders.put("FlashLauncher", launcher);
        final Class<?> launcherClass = launcher.loadClass("illa4257.flashlauncher.FLLoader");
        launcherClass.getMethod("init", utils.loadClass("illa4257.i4Utils.io.MultiSocketServer")).invoke(null, server);
    }

    public static void update() throws Exception {
        throw new Exception("Not implemented yet.");
    }
}