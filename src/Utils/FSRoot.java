package Utils;

import java.io.IOException;
import java.io.InputStream;

public abstract class FSRoot {
    public abstract boolean exists(final String path);
    public abstract InputStream openInputStream(final String path) throws IOException;
    public abstract byte[] readFully(final String path) throws IOException;
    public abstract FSFile[] list(final String path) throws IOException;

    public boolean exists(final FSFile file) { return exists(file.toString()); }
    public InputStream openInputStream(final FSFile path) throws IOException { return openInputStream(path.toString()); }
    public byte[] readFully(final FSFile path) throws IOException { return readFully(path.toString()); }
    public FSFile[] list(final FSFile path) throws IOException { return list(path.toString()); }
}
