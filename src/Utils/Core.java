package Utils;

import UIL.Lang;
import UIL.LangItem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Core {
    public static int BUFFER_SIZE = 1024 * 1024;

    private interface IReader {
        byte[] run(final InputStream inputStream) throws IOException;
    }

    private static final IReader reader;
    private static IReader detectReader() {
        try {
            final Method m = InputStream.class.getMethod("readAllBytes");
            return is -> {
                try {
                    return (byte[]) m.invoke(is);
                } catch (final Throwable ex) {
                    if (ex instanceof InvocationTargetException)
                        throw (IOException) ((InvocationTargetException) ex).getTargetException();
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
                        if (ex instanceof InvocationTargetException)
                            throw (IOException) ((InvocationTargetException) ex).getTargetException();
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
                        if (ex instanceof InvocationTargetException)
                            throw (IOException) ((InvocationTargetException) ex).getTargetException();
                        throw new IOException(ex);
                    }
                };
            } catch (final NoSuchMethodException ignored) {}
        } catch (final ClassNotFoundException ignored) {}
        return is -> {
            final ByteArrayOutputStream r = new ByteArrayOutputStream();
            final byte[] buff = new byte[BUFFER_SIZE];
            for (int len = is.read(buff, 0, buff.length); len > -1; len = is.read(buff, 0, buff.length))
                r.write(buff, 0, len);
            return r.toByteArray();
        };
    }

    static {
        reader = detectReader();
    }

    public static byte[] readFully(final InputStream inputStream, final boolean close) throws IOException {
        final byte[] r = reader.run(inputStream);
        if (close) inputStream.close();
        return r;
    }

    public static byte[] readFully(final InputStream inputStream) throws IOException { return readFully(inputStream, true); }

    public static final String RES = "res://";
    public static final String FILE = "file://";

    private static void pcd(final File dir) {
        final File[] l = dir.listFiles();
        if (l == null) return;
        for (final File f : l) {
            if (f.isDirectory()) pcd(f);
            f.delete();
        }
    }

    public static void cleanDir(final File dir) { pcd(dir.getAbsoluteFile()); }

    private static void makeDir(final File dir) {
        if (!dir.exists()) {
            makeDir(dir.getParentFile());
            dir.mkdir();
        } else if (dir.isFile()) {
            dir.delete();
            dir.mkdir();
        }
    }

    public static void mkdir(final File file) { makeDir(file.getAbsoluteFile()); }

    public static OutputStream writeStream(File file) throws IOException {
        file = file.getAbsoluteFile();
        makeDir(file.getParentFile());
        if (file.exists() && file.isDirectory()) {
            pcd(file);
            file.delete();
        } else if (!file.exists())
            file.createNewFile();
        return Files.newOutputStream(file.toPath());
    }

    public static InputStream openStream(String path) throws IOException, SecurityException {
        if (path.startsWith(RES))
            return ClassLoader.getSystemResourceAsStream(path.substring(RES.length()));
        if (path.startsWith(FILE))
            path = path.substring(FILE.length());
        else {
            final int ip = path.indexOf("://");
            if (ip > -1 && ip == path.indexOf(':') && ip < path.indexOf("/"))
                return ClassLoader.getSystemResourceAsStream("assets/" + path.substring(0, ip) + path.substring(ip + 2));
        }
        return Files.newInputStream(Paths.get(path));
    }

    public static boolean exists(String path) {
        if (path.startsWith(RES))
            return ClassLoader.getSystemResource(path.substring(RES.length())) != null;
        if (path.startsWith(FILE))
            path = path.substring(FILE.length());
        else {
            final int ip = path.indexOf("://");
            if (ip > -1 && ip == path.indexOf(':') && ip < path.indexOf("/"))
                return ClassLoader.getSystemResource("assets/" + path.substring(0, ip) + path.substring(ip + 2)) != null;
        }
        return new File(path).exists();
    }

    public static byte[] readFully(final File path) throws IOException { return readFully(Files.newInputStream(path.toPath())); }

    public static byte[] readFully(final String path, final boolean close) throws IOException, SecurityException { return readFully(openStream(path), close); }

    public static byte[] readFully(final String path) throws IOException, SecurityException { return readFully(openStream(path), true); }

    public static BufferedImage getImage(final String path) throws IOException, SecurityException { return ImageIO.read(openStream(path)); }

    public static int minIndexOf(final String str, final String... sub) { return minIndexOf(str, 0, sub); }

    public static int minIndexOf(final String str, final int fromIndex, final String... sub) {
        int r = -1;
        for (final String s : sub) {
            final int i = str.indexOf(s, fromIndex);
            if (i > -1 && (r < 0 || i < r))
                r = i;
        }
        return r;
    }

    public static int minIndexOf(final String str, final int... sub) { return minIndexOf(str, 0, sub); }

    public static int minIndexOf(final String str, final int fromIndex, final int... sub) {
        int r = -1;
        for (final int s : sub) {
            final int i = str.indexOf(s, fromIndex);
            if (i > -1 && (r < 0 || i < r))
                r = i;
        }
        return r;
    }

    public static int minIndexOf(final String str, final int fromIndex, final char... sub) {
        int r = -1;
        for (final char s : sub) {
            final int i = str.indexOf(s, fromIndex);
            if (i > -1 && (r < 0 || i < r))
                r = i;
        }
        return r;
    }

    public static int minIndexOf(final String str, final char... sub) { return minIndexOf(str, 0, sub); }

    public static boolean startsWith(final String str, final String... prefixes) {
        for (final String prefix : prefixes)
            if (str.startsWith(prefix))
                return true;
        return false;
    }

    public static int indexOfFC(final String str, final String... starts) {
        String t = str;
        for (final String s : starts)
            t = t.replaceAll(s, "");
        return t.length() > 0 ? str.indexOf(t.charAt(0)) : 0;
    }

    public static String removeStart(final String str, final String... starts) {
        final int i = indexOfFC(str, starts);
        return i == -1 ? "" : str.substring(i);
    }

    public static int fromHexInt(final String hex) {
        int r = 0;
        for (final char ch : hex.toCharArray())
            r = r * 16 + fromHex1(ch);
        return r;
    }

    public static int fromHex2(final String s) { return fromHex1(s.substring(0, 1)) * 16 + fromHex1(s.substring(1)); }

    public static int fromHex1as2(char s) {
        final int v = fromHex1(s);
        return v * 16 + v;
    }

    public static int fromHex1(String s) { return fromHex1(s.charAt(0)); }

    public static int fromHex1(char s) {
        s = Character.toLowerCase(s);
        switch (s) {
            case 'f':
                return 15;
            case 'e':
                return 14;
            case 'd':
                return 13;
            case 'c':
                return 12;
            case 'b':
                return 11;
            case 'a':
                return 10;
        }
        try {
            return Integer.parseInt(String.valueOf(s));
        } catch (final NumberFormatException ex) {
            System.out.println(s);
            throw ex;
        }
    }

    public static File getFile(final Class<?> c) {
        try {
            return new File(c.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (final URISyntaxException ignored) {
            throw new RuntimeException("I cannot parse path");
        }
    }

    public static File getPath(final Class<?> c) { return getFile(c).getParentFile(); }

    public static String relative(final File main, final File other) {
        final File[] mp = parents(main);
        final File[] op = parents(other);

        int i = 0;
        for (; i < mp.length && i < op.length; i++)
            if (!mp[i].getPath().equals(op[i].getPath())) {
                if (i == 0) return other.getAbsolutePath();
                break;
            }
        final int s = mp.length - i;
        final StringBuilder b = new StringBuilder(i == 1 ? "/" : s > 1 ? String.join("", Collections.nCopies(s, "../")) : "./");
        for (; i < op.length; i++) b.append(op[i].getName()).append("/");
        return b.toString();
    }

    public static File[] parents(File f) {
        if (f != null) f = f.getAbsoluteFile();
        final ArrayList<File> parents = new ArrayList<>();
        for (; f != null; f = f.getParentFile())
            parents.add(0, f);
        return parents.toArray(new File[0]);
    }

    private static final LangItem
            bytes = Lang.get("size.bytes"),
            kilobytes = Lang.get("size.kilobytes"),
            megabytes = Lang.get("size.megabytes"),
            gigabytes = Lang.get("size.gigabytes"),
            terabytes = Lang.get("size.terabytes");

    public static String strSize1024(final long size) {
        return Long.compareUnsigned(size, 1024) == 1 ?
                    Long.compareUnsigned(size, 1048576) == 1 ?
                            Long.compareUnsigned(size, 1073741824L) == 1 ?
                                    Long.compareUnsigned(size, 1099511627776L) == 1 ?
                                            Long.toUnsignedString(Long.divideUnsigned(size, 1099511627776L)) + " " + terabytes :
                                            Long.toUnsignedString(Long.divideUnsigned(size, 1073741824L)) + " " + gigabytes :
                                    Long.toUnsignedString(size / 1048576) + " " + megabytes :
                            Long.toUnsignedString(size / 1024) + " " + kilobytes :
                    Long.toUnsignedString(size) + " " + bytes;
    }

    public static final String
            CHARS_NUMS = "0123456789",
            CHARS_EN_LOW = "abcdefghijklmnopqrstuvwxyz",
            CHARS_EN_UP = CHARS_EN_LOW.toUpperCase();

    public static String random(int len) { return random(len, CHARS_NUMS + CHARS_EN_LOW + CHARS_EN_UP); }

    public static String random(int len, String chars) {
        char[] buf = new char[len];
        Random r = new Random();
        for (int i = 0; i < buf.length; i++)
            buf[i] = chars.charAt(r.nextInt(chars.length()));
        return new String(buf);
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (final Map.Entry<T, E> entry : map.entrySet())
            if (Objects.equals(value, entry.getValue()))
                return entry.getKey();
        return null;
    }

    public static byte[] hash(final String algorithm, final byte[] data) throws NoSuchAlgorithmException { return MessageDigest.getInstance(algorithm).digest(data); }

    public static String bytesToHex(final byte[] data) {
        final StringBuilder b = new StringBuilder(data.length * 2);
        for (final byte d : data) {
            final String hex = Integer.toHexString(0xff & d);
            if (hex.length() == 1)
                b.append('0');
            b.append(hex);
        }
        return b.toString();
    }

    public static String hashToHex(final String algorithm, final byte[] data) throws NoSuchAlgorithmException { return bytesToHex(hash(algorithm, data)); }
    public static String hashStringToHex(final String algorithm, final String data) throws NoSuchAlgorithmException { return bytesToHex(hash(algorithm, data.getBytes(StandardCharsets.UTF_8))); }

    public static void waitM(final Object... objects) throws InterruptedException {
        if (objects.length == 0)
            return;
        if (objects.length == 1) {
            final Object o = objects[0];
            if (o instanceof ObjLocker)
                ((ObjLocker) o).waitNotify();
            else
                o.wait();
            return;
        }
        final Object o = new Object();
        final ArrayList<Thread> l = new ArrayList<>();
        synchronized (o) {
            for (final Object ob : objects)
                l.add(new Thread(() -> {
                    try {
                        if (ob instanceof ObjLocker)
                            ((ObjLocker) ob).waitNotify();
                        else
                            synchronized (ob) {
                                ob.wait();
                            }
                        synchronized (o) {
                            o.notifyAll();
                        }
                    } catch (final InterruptedException ignored) {}
                }) {{
                    start();
                }});
            o.wait();
        }
        for (final Thread t : l)
            t.interrupt();
    }
}
