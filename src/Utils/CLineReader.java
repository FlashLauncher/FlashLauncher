package Utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;

public class CLineReader implements Closeable {
    private final Object l = new Object();
    private StringBuilder sb = new StringBuilder();
    private ArrayList<String> lines = new ArrayList<>();

    private boolean closed = false;

    public boolean hasNextLine() { synchronized (l) { return !lines.isEmpty(); } }

    public String nextLine() throws IOException {
        synchronized (l) {
            try {
                while (lines.isEmpty()) {
                    if (closed)
                        return null;
                    l.wait();
                }
            } catch (final InterruptedException ex) {
                throw new InterruptedIOException(ex.getMessage());
            }
            return lines.remove(0);
        }
    }

    private boolean nr = false;

    public void write(final char[] chars, final int offset, final int length) {
        synchronized (l) {
            if (closed || offset >= chars.length)
                return;
            final int e = Math.min(offset + length, chars.length);
            int i = offset;
            for (int i1 = i; i1 < e; i1++)
                if (chars[i1] == '\r' || chars[i1] == '\n') {
                    if (nr) {
                        i = i1 + 1;
                        nr = false;
                        continue;
                    }
                    nr = chars[i1] == '\r';
                    lines.add(sb + new String(chars, i, i1 - i));
                    sb = new StringBuilder();
                    i = i1;
                } else if (nr)
                    nr = false;
            if (chars.length - i > 0)
                sb.append(new String(chars, i, e - i));
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (l) {
            closed = true;
        }
    }
}