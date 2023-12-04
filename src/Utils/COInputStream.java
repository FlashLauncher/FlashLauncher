package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;

public class COInputStream extends InputStream {
    private final Object l = new Object();
    private int index = 0;
    private boolean closed = false;

    private static class Page {
        final byte[] d;
        final int o, e;

        public Page(final byte[] data, final int offset, final int length) {
            d = data;
            o = offset;
            e = Math.min(offset + length, data.length);
        }

        Page next = null;
    }

    Page current = null, last = null;

    @Override
    public int read() throws IOException {
        synchronized (l) {
            try {
                while (current == null) {
                    if (closed)
                        return -1;
                    l.wait();
                }
            } catch (final InterruptedException ex) {
                throw new InterruptedIOException(ex.getMessage());
            }
            final byte r = current.d[index++];
            if (index > current.e) {
                current = current.next;
                if (current != null)
                    index = current.o;
            }
            return r & 0xFF;
        }
    }

    @Override
    public void close() {
        synchronized (l) {
            if (closed)
                return;
            closed = true;
            if (current == null)
                l.notifyAll();
        }
    }

    /**
     * Does not copy.
     */
    public void write(final byte[] data) {
        if (data.length == 0)
            return;
        final Page p = new Page(data, 0, data.length);
        synchronized (l) {
            if (closed)
                return;
            if (last != null)
                last.next = p;
            last = p;
            if (current == null) {
                current = p;
                index = 0;
                l.notifyAll();
            }
        }
    }

    /**
     * Does not copy.
     */
    public void write(final byte[] data, final int offset, final int length) {
        if (data.length == 0 || offset > data.length || length == 0)
            return;
        final Page p = new Page(data, offset, length);
        synchronized (l) {
            if (closed)
                return;
            if (last != null)
                last.next = p;
            last = p;
            if (current == null) {
                current = p;
                index = offset;
                l.notifyAll();
            }
        }
    }

    private final int cl(final Page p) { return p.next == null ? p.e - p.o : p.e - p.o + cl(p.next); }

    @Override
    public int available() throws IOException {
        synchronized (l) {
            return current == null ? 0 : cl(current);
        }
    }
}