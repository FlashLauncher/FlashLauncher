package Utils;

import UIL.Lang;
import UIL.LangItem;
import Utils.fixed.FixedEntry;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Core {
    public static final String
            CHARS_NUMS = "0123456789",
            CHARS_EN_LOW = "abcdefghijklmnopqrstuvwxyz",
            CHARS_EN_UP = CHARS_EN_LOW.toUpperCase(),
            UTF_8 = StandardCharsets.UTF_8.toString(),
            URI_CHARS = CHARS_EN_LOW + CHARS_EN_UP + CHARS_NUMS + ":/?#[]@!$&'()*+,;=-._~%"
    ;

    public static final boolean
            IS_WINDOWS,
            IS_WINDOWS_10,
            IS_LINUX,
            IS_MACOS
    ;

    static {
        final String osn = System.getProperty("os.name").toLowerCase();
        IS_WINDOWS = osn.contains("win");
        if (IS_WINDOWS) {
            IS_WINDOWS_10 = osn.contains("10");
            IS_LINUX = false;
            IS_MACOS = false;
        } else {
            IS_WINDOWS_10 = false;
            IS_LINUX = osn.contains("nix") || osn.contains("nux") || osn.contains("aix");
            IS_MACOS = !IS_LINUX && osn.contains("mac");
        }
    }

    public static String encodeURI(final String url) throws UnsupportedEncodingException {
        StringBuilder b = new StringBuilder();
        for (final char ch : url.replaceAll(" ", "%20").toCharArray())
            if (URI_CHARS.indexOf(ch) == -1)
                b.append(URLEncoder.encode(String.valueOf(ch), UTF_8));
            else
                b.append(ch);
        return b.toString();
    }

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
        return !t.isEmpty() ? str.indexOf(t.charAt(0)) : 0;
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

    private static final LangItem
            bytes = Lang.get("size.bytes"),
            kilobytes = Lang.get("size.kilobytes"),
            megabytes = Lang.get("size.megabytes"),
            gigabytes = Lang.get("size.gigabytes"),
            terabytes = Lang.get("size.terabytes");

    public static String strSize1024(final long size) {
        if (size > -1)
            return size >= 1024 ?
                    size >= 1048576 ?
                            size >= 1073741824L ?
                                    size >= 1099511627776L ?
                                            size / 1099511627776L + " " + terabytes :
                                            size / 1073741824L + " " + gigabytes :
                                    size / 1048576 + " " + megabytes :
                            size / 1024 + " " + kilobytes :
                    size + " " + bytes;
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
                }, "Wait multiple " + ob) {{
                    start();
                }});
            o.wait();
        }
        for (final Thread t : l)
            t.interrupt();
    }

    private static final ListMap<Object, FixedEntry<Thread, List<Runnable>>> waiterList = new ListMap<>();
    public static Runnable onNotifyLoop(final Object object, final Runnable listener) {
        if (listener == null)
            return null;
        synchronized (waiterList) {
            final FixedEntry<Thread, List<Runnable>> l = waiterList.get(object);
            if (l == null) {
                final List<Runnable> rl = new ArrayList<>();
                rl.add(listener);
                waiterList.put(object, new FixedEntry<>(
                        new Thread(() -> {
                            while (true)
                                synchronized (object) {
                                    final Runnable[] rul;
                                    synchronized (rl) {
                                        rul = rl.toArray(new Runnable[0]);
                                    }
                                    for (final Runnable r : rul)
                                        try {
                                            r.run();
                                        } catch (final Throwable ex) {
                                            ex.printStackTrace();
                                        }
                                    try {
                                        object.wait();
                                    } catch (final InterruptedException ignored) {
                                        break;
                                    }
                                }
                        }) {{
                            start();
                        }}, rl
                ));
            } else {
                synchronized (l.getValue()) {
                    l.getValue().add(listener);
                }
                synchronized (object) {
                    listener.run();
                }
            }
        }
        return listener;
    }

    public static Runnable onNotifyLoop(final Object object, final Runnable1a<Runnable> listener) {
        return onNotifyLoop(object, new Runnable() {
            @Override
            public void run() {
                listener.run(this);
            }
        });
    }

    public static void offNotifyLoop(final Runnable listener) {
        if (listener == null)
            return;
        synchronized (waiterList) {
            for (final Map.Entry<Object, FixedEntry<Thread, List<Runnable>>> e : waiterList.entrySet()) {
                final List<Runnable> l = e.getValue().getValue();
                synchronized (l) {
                    if (l.contains(listener)) {
                        if (l.size() > 1)
                            l.remove(listener);
                        else {
                            e.getValue().getKey().interrupt();
                            waiterList.remove(e.getKey());
                        }
                        break;
                    }
                }
            }
        }
    }

    private static final ListMap<Object, FixedEntry<Thread, ArrayList<Runnable>>> waiterList2 = new ListMap<>();
    public static Runnable onNotify(final Object object, final Runnable listener) {
        if (listener == null)
            return null;
        synchronized (waiterList2) {
            final FixedEntry<Thread, ArrayList<Runnable>> l = waiterList2.get(object);
            if (l == null) {
                final ArrayList<Runnable> rl = new ArrayList<>();
                rl.add(listener);
                waiterList2.put(object, new FixedEntry<>(
                        new Thread(() -> {
                            while (true)
                                synchronized (object) {
                                    try {
                                        object.wait();
                                    } catch (final InterruptedException ignored) {
                                        break;
                                    }
                                    final Runnable[] rul;
                                    synchronized (rl) {
                                        rul = rl.toArray(new Runnable[0]);
                                    }
                                    for (final Runnable r : rul)
                                        try {
                                            r.run();
                                        } catch (final Throwable ex) {
                                            ex.printStackTrace();
                                        }
                                }
                        }) {{
                            start();
                        }}, rl
                ));
            } else {
                synchronized (l.getValue()) {
                    l.getValue().add(listener);
                }
                synchronized (object) {
                    listener.run();
                }
            }
        }
        return listener;
    }

    public static void offNotify(final Runnable listener) {
        if (listener == null)
            return;
        synchronized (waiterList2) {
            for (final Map.Entry<Object, FixedEntry<Thread, ArrayList<Runnable>>> e : waiterList2.entrySet()) {
                final ArrayList<Runnable> l = e.getValue().getValue();
                synchronized (l) {
                    if (l.contains(listener)) {
                        if (l.size() > 1)
                            l.remove(listener);
                        else {
                            e.getValue().getKey().interrupt();
                            waiterList2.remove(e.getKey());
                        }
                        break;
                    }
                }
            }
        }
    }

    public static int syncGetSize(final Collection<?> collection) { synchronized (collection) { return collection.size(); } }
    public static int syncGetSize(final Map<?, ?> map) { synchronized (map) { return map.size(); } }

    public static <T> T syncGet(final List<T> list, final int index) {
        synchronized (list) {
            return list.get(index);
        }
    }

    public static <K, V> V syncGet(final Map<K, V> map, final K key) {
        synchronized (map) {
            return map.get(key);
        }
    }
}