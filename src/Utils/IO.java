package Utils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;

public class IO {
    public static final int BUFFER_SIZE = 1024 * 1024;

    private interface IReader {
        byte[] run(final InputStream inputStream) throws IOException;
    }

    private static IReader detectReader() {
        try {
            final Method m = InputStream.class.getMethod("readAllBytes");
            return is -> {
                try {
                    return (byte[]) m.invoke(is);
                } catch (final Throwable ex) {
                    if (ex instanceof InvocationTargetException) {
                        final Throwable e = ((InvocationTargetException) ex).getTargetException();
                        if (e instanceof NullPointerException)
                            throw (NullPointerException) e;
                        throw (IOException) e;
                    }
                    throw new IOException(ex);
                }
            };
        } catch (final NoSuchMethodException ignored) {}
        try {
            final Class<?> c = Class.forName("sun.misc.IOUtils");
            try {
                final Method m = c.getDeclaredMethod("readFully", InputStream.class);
                return is -> {
                    try {
                        return (byte[]) m.invoke(null, is);
                    } catch (final Throwable ex) {
                        if (ex instanceof InvocationTargetException) {
                            final Throwable e = ((InvocationTargetException) ex).getTargetException();
                            if (e instanceof NullPointerException)
                                throw (NullPointerException) e;
                            throw (IOException) e;
                        }
                        throw new IOException(ex);
                    }
                };
            } catch (final NoSuchMethodException ignored) {}
            try {
                final Method m = c.getDeclaredMethod("readFully", InputStream.class, int.class, boolean.class);
                return is -> {
                    try {
                        return (byte[]) m.invoke(null, is, -1, true);
                    } catch (final Throwable ex) {
                        if (ex instanceof InvocationTargetException) {
                            final Throwable e = ((InvocationTargetException) ex).getTargetException();
                            if (e instanceof NullPointerException)
                                throw (NullPointerException) e;
                            throw (IOException) e;
                        }
                        throw new IOException(ex);
                    }
                };
            } catch (final NoSuchMethodException ignored) {}
        } catch (final ClassNotFoundException ignored) {}
        return is -> {
            if (is == null)
                throw new NullPointerException("InputStream is null!");
            final ByteArrayOutputStream r = new ByteArrayOutputStream();
            final byte[] buff = new byte[BUFFER_SIZE];
            for (int len = is.read(buff, 0, buff.length); len > -1; len = is.read(buff, 0, buff.length))
                r.write(buff, 0, len);
            return r.toByteArray();
        };
    }

    private static final IReader reader;

    static {
        reader = detectReader();
    }

    public static byte[] readFully(final InputStream inputStream, final boolean close) throws IOException {
        try {
            return reader.run(inputStream);
        } finally {
            if (close)
                inputStream.close();
        }
    }

    public static byte[] readFully(final InputStream inputStream) throws IOException {
        try {
            return reader.run(inputStream);
        } finally {
            inputStream.close();
        }
    }

    public static byte[] readFully(final File path) throws IOException {
        try (final InputStream fis = Files.newInputStream(path.toPath())) {
            return reader.run(fis);
        }
    }
}
