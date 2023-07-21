package Utils;

import java.io.IOException;
import java.io.InputStream;

public abstract class FSRoot {
    public abstract boolean exists(final String path);
    public abstract InputStream openInputStream(final String path) throws IOException;
    public abstract byte[] readFully(final String path) throws IOException;
    public abstract FSFile[] list(final String path) throws IOException;
}
