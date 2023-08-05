package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

public class FS {
    private static final ArrayList<FSRoot> roots = new ArrayList<>();

    public static final FSRoot OS = new FSRoot() {
        @Override public boolean exists(final String path) { return new File(path).exists(); }
        @Override public InputStream openInputStream(final String path) throws IOException { return Files.newInputStream(Paths.get(path)); }
        @Override public byte[] readFully(final String path) throws IOException { return IO.readFully(new File(path)); }

        @Override
        public FSFile[] list(final String path) throws IOException {
            final File[] l = new File(path).listFiles();
            if (l == null)
                throw new IOException("I can't get list of files");
            final FSFile[] r = new FSFile[l.length];
            for (int i = 0; i < l.length; i++) {
                final File f = l[i];
                r[i] = new FSFile() {
                    @Override
                    public String getName() {
                        return f.getName();
                    }

                    @Override
                    public boolean isDir() {
                        return f.isDirectory();
                    }

                    @Override
                    public String toString() {
                        return f.toString();
                    }
                };
            }
            return r;
        }
    }, ROOT = new FSRoot() {
        @Override
        public boolean exists(String path) {
            final int i = path.indexOf("://");
            if (i == -1)
                return OS.exists(path);

            if (i == 0)
                path = path.substring(3);
            else
                path = "assets/" + path.substring(0, i) + "/" + path.substring(i + 3);
            synchronized (roots) {
                for (final FSRoot r : roots)
                    return r.exists(path);
            }
            return false;
        }

        @Override
        public InputStream openInputStream(String path) throws IOException {
            final int i = path.indexOf("://");
            if (i == -1)
                return OS.openInputStream(path);

            if (i == 0)
                path = path.substring(3);
            else if (path.startsWith("res://"))
                path = path.substring(6);
            else
                path = "assets/" + path.substring(0, i) + "/" + path.substring(i + 3);
            synchronized (roots) {
                for (final FSRoot r : roots)
                    if (r.exists(path))
                        try {
                            return r.openInputStream(path);
                        } catch (final IOException ignored) {}
            }
            throw new IOException("File " + path + " not found!");
        }

        @Override
        public byte[] readFully(String path) throws IOException {
            final int i = path.indexOf("://");
            if (i == -1)
                return OS.readFully(path);

            if (i == 0)
                path = path.substring(3);
            else if (path.startsWith("res://"))
                path = path.substring(6);
            else
                path = "assets/" + path.substring(0, i) + "/" + path.substring(i + 3);
            synchronized (roots) {
                for (final FSRoot r : roots)
                    if (r.exists(path))
                        try {
                            return r.readFully(path);
                        } catch (final IOException ignored) {}
            }
            throw new IOException("File " + path + " not found!");
        }

        @Override
        public FSFile[] list(String path) throws IOException {
            final int i = path.indexOf("://");
            if (i == -1)
                return OS.list(path);

            if (i == 0)
                path = path.substring(3);
            else if (path.startsWith("res://"))
                path = path.substring(6);
            else
                path = "assets/" + path.substring(0, i) + "/" + path.substring(i + 3);
            final ArrayList<FSFile> l = new ArrayList<>();
            synchronized (roots) {
                for (final FSRoot r : roots)
                    if (r.exists(path))
                        try {
                            Collections.addAll(l, r.list(path));
                        } catch (final IOException ignored) {}
            }
            return l.toArray(new FSFile[0]);
        }
    };

    public static void addRoot(final FSRoot root) {
        if (root == null)
            throw new RuntimeException("Root is null");
        synchronized (roots) {
            roots.add(0, root);
        }
    }

    public static void removeRoot(final FSRoot root) {
        synchronized (roots) {
            roots.remove(root);
        }
    }

    public static FSRoot newFS(final File path) {
        if (path == null || !path.exists())
            return null;
        return path.isFile() ? new FSZip(path) : new FSDir(path);
    }



    public static File[] parents(File f) {
        if (f != null) f = f.getAbsoluteFile();
        final ArrayList<File> parents = new ArrayList<>();
        for (; f != null; f = f.getParentFile())
            parents.add(0, f);
        return parents.toArray(new File[0]);
    }

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

    public static boolean clearDir(final File dir) {
        final File[] fl = dir.listFiles();
        if (fl == null)
            return false;
        for (final File f : fl) {
            if (f.isDirectory() && !clearDir(f))
                return false;
            if (!f.delete())
                return false;
        }
        return true;
    }
}