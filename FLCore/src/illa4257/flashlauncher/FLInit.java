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
    public static final byte[] APP_ID = "flash-launcher".getBytes(StandardCharsets.UTF_8);
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
        loggerClass.getMethod("setParent", loggerClass).invoke(null, logger);
        {
            final Class<?> levels = utils.loadClass("illa4257.i4Utils.logger.Level");

            final Class<?> logHandlerClass = utils.loadClass("illa4257.i4Utils.logger.LogHandler");
            final Constructor<?> curLogHandlerConstructor = utils
                    .loadClass("illa4257.i4Utils.logger.AnsiColoredPrintStreamLogHandler").getConstructor(
                            PrintStream.class,
                            levels,
                            Object.class
                    );

            final Method reg = loggerClass.getMethod("registerHandler", logHandlerClass);
            reg.invoke(logger, curLogHandlerConstructor.newInstance(out, levels.getField("INFO").get(null), out));
            reg.invoke(logger, curLogHandlerConstructor.newInstance(out, levels.getField("DEBUG").get(null), out));
            reg.invoke(logger, curLogHandlerConstructor.newInstance(out, levels.getField("WARN").get(null), out));
            reg.invoke(logger, curLogHandlerConstructor.newInstance(out, levels.getField("ERROR").get(null), out));

            final Method m = loggerClass.getMethod("newOutputStream", levels);
            System.setOut(new PrintStream((OutputStream) m.invoke(logger, levels.getField("INFO").get(null))));
            System.setErr(new PrintStream((OutputStream) m.invoke(logger, levels.getField("ERROR").get(null))));
        }

        final Class<?> ms = utils.loadClass("illa4257.i4Utils.MultiSocketFactory");
        final Object s = ms.getConstructor(byte[].class, int.class).newInstance(APP_ID, PORT);

        final Object server = ms.getMethod("reserve").invoke(s);
        if (server == null) {
            final Object o = ms.getMethod("connect").invoke(s);
            if (o == null)
                return;
            try (final Closeable c = (Closeable) o) {
                final Class<?> socket = utils.loadClass("illa4257.i4Utils.MultiSocket");
                final OutputStream os = (OutputStream) socket.getMethod("getOutputStream").invoke(c);
                os.write(0);
                os.flush();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        final Loader.ResourceClassLoader framework = new Loader.ResourceClassLoader(utils, "i4Framework.jar");
        Loader.loaders.put("i4Framework", framework);

        final Loader.ResourceClassLoader launcher = new Loader.ResourceClassLoader(framework, "FlashLauncher.jar");
        Loader.loaders.put("FlashLauncher", launcher);
        final Class<?> launcherClass = launcher.loadClass("illa4257.flashlauncher.FLLoader");
        launcherClass.getMethod("init", utils.loadClass("illa4257.i4Utils.MultiSocketServer")).invoke(null, server);
    }

    public static void update() throws Exception {
        throw new Exception("Not implemented yet.");
    }
}