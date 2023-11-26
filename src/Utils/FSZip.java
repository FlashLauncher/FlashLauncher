package Utils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FSZip extends FSRoot {
    private final File a;

    public FSZip(final File archive) { a = archive; }

    @Override
    public boolean exists(final String path) {
        try {
            final String p2 = path + "/";
            try (final ZipInputStream zis = new ZipInputStream(Files.newInputStream(a.toPath()))) {
                for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry())
                    if (e.getName().equals(path) || e.getName().equals(p2))
                        return true;
            }
        } catch (final IOException ex) {
            if (ex instanceof InterruptedIOException)
                ex.printStackTrace();
        }
        return false;
    }

    @Override
    public InputStream openInputStream(final String path) throws IOException {
        final ZipInputStream zis = new ZipInputStream(Files.newInputStream(a.toPath()));
        for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry())
            if (e.getName().equals(path))
                return zis;
        zis.close();
        throw new IOException("File " + path + " not found!");
    }

    @Override
    public byte[] readFully(final String path) throws IOException {
        final ZipInputStream zis = new ZipInputStream(Files.newInputStream(a.toPath()));
        for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry())
            if (e.getName().equals(path))
                return IO.readFully(zis);
        zis.close();
        throw new IOException("File " + path + " not found!");
    }

    @Override
    public FSFile[] list(String path) throws IOException {
        if (path.endsWith("/"))
            path = path.substring(0, path.length() - 1);
        final String p2 = path.isEmpty() ? path : path + "/";
        final int le = p2.length();
        final ArrayList<FSFile> l = new ArrayList<>();

        try (final ZipInputStream zis = new ZipInputStream(Files.newInputStream(a.toPath()))) {
            for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry())
                if (e.getName().startsWith(p2)) {
                    if (e.getName().length() == le)
                        continue;
                    final String n = e.getName().substring(le);
                    final int i = n.indexOf('/');
                    if (i == -1) {
                        l.add(new FSZipFile(path, n, false));
                        continue;
                    }
                    if (n.length() > i + 1)
                        continue;
                    l.add(new FSZipFile(path, n.substring(0, i), true));
                }
        }

        return l.toArray(new FSFile[0]);
    }

    public static class FSZipFile implements FSFile {
        private final String p, n;
        private final boolean d;

        public FSZipFile(final String path, final String name, final boolean isDir) {
            p = path;
            n = name;
            d = isDir;
        }

        @Override public String getName() { return n; }
        @Override public boolean isDir() { return d; }
        @Override public String toString() { return p + "/" + n; }
    }
}
