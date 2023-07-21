package Utils;

import java.io.*;
import java.nio.file.Files;

public class FSDir extends FSRoot {
    private final File d;

    public FSDir(final File dir) {
        d = dir;
    }

    @Override
    public boolean exists(final String path) {
        return new File(d, path).exists();
    }

    @Override
    public InputStream openInputStream(final String path) throws IOException {
        return Files.newInputStream(new File(d, path).toPath());
    }

    @Override
    public byte[] readFully(final String path) throws IOException {
        return IO.readFully(new File(d, path));
    }

    @Override
    public FSFile[] list(final String path) {
        final File[] l = new File(d, path).listFiles();
        if (l == null)
            return null;
        final FSFile[] fl = new FSFile[l.length];
        for (int i = 0; i < l.length; i++)
            fl[i] = new FSDirFile(l[i]);
        return fl;
    }

    public static class FSDirFile implements FSFile {
        private final File f;

        public FSDirFile(File file) {
            f = file;
        }

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
    }
}
