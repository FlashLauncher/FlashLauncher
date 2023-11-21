package Utils.json;

import Utils.Core;

import java.io.*;
import java.nio.charset.Charset;

public abstract class Json extends Reader implements AutoCloseable {
    int p = -1;

    public static JsonElement parse(final char[] buf) throws IOException {
        try (final Json j = new Json() {
            private int i = 0;

            @Override
            public int read(final char[] cbuf, final int off, final int len) {
                if (i == buf.length)
                    return -1;
                if (i + len > buf.length) {
                    final int r = buf.length - i;
                    System.arraycopy(buf, i, cbuf, off, r);
                    i = buf.length;
                    return r;
                } else
                    System.arraycopy(buf, i, cbuf, off, len);
                return len;
            }

            @Override public void close() {}
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final String string) throws IOException {
        try (final Json j = new Json() {
            private final StringReader isr = new StringReader(string);

            @Override
            public int read(final char[] cbuf, final int off, final int len) throws IOException {
                return isr.read(cbuf, off, len);
            }

            @Override public void close() { isr.close(); }
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final InputStreamReader isr, final boolean autoClose) throws IOException {
        try (final Json j = new Json() {
            @Override
            public int read(final char[] cbuf, final int off, final int len) throws IOException {
                return isr.read(cbuf, off, len);
            }

            @Override
            public void close() throws IOException {
                if (autoClose)
                    isr.close();
            }
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final InputStream is, final boolean autoClose, Charset charset) throws IOException {
        try (final Json j = new Json() {
            private final InputStreamReader isr = new InputStreamReader(is, charset);

            @Override
            public int read(final char[] cbuf, final int off, final int len) throws IOException {
                return isr.read(cbuf, off, len);
            }

            @Override
            public void close() throws IOException {
                if (autoClose)
                    isr.close();
            }
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final InputStream is, final boolean autoClose, String charset) throws IOException {
        try (final Json j = new Json() {
            private final InputStreamReader isr = new InputStreamReader(is, charset);

            @Override
            public int read(final char[] cbuf, final int off, final int len) throws IOException {
                return isr.read(cbuf, off, len);
            }

            @Override
            public void close() throws IOException {
                if (autoClose)
                    isr.close();
            }
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final File file, final String charset) throws IOException {
        try (
                final FileInputStream is = new FileInputStream(file);
                final BufferedReader isr = new BufferedReader(new InputStreamReader(is, charset));
                final Json j = new Json() {
                        @Override
                        public int read(final char[] cbuf, final int off, final int len) throws IOException {
                            return isr.read(cbuf, off, len);
                        }

                        @Override
                        public void close() throws IOException {
                            isr.close();
                        }
        }) {
            return j.p();
        }
    }

    @Override
    public int read() throws IOException {
        if (p != -1) {
            final int p2 = p;
            p = -1;
            return p2;
        }
        final int p2 = super.read();
        if (p2 == -1)
            throw new IOException("-1");
        return p2;
    }

    JsonElement p() throws IOException {
        char cha;
        while (true)
            switch (cha = (char) read()) {
                case '\t': case ' ': case '\r': case '\n': break;
                case '{':
                    final JsonDict d = new JsonDict();
                    char ch;
                    while (true)
                        switch (ch = (char) read()) {
                            case '\t': case ' ': case '\r': case '\n': case ',': break;
                            case '}': return d;
                            case '"':
                                p = '"';
                                final String key = p().getAsString();
                                while ((ch = (char) read()) == ' ' || ch == '\t') {}
                                if (ch != ':')
                                    throw new IOException("Unexpected char " + ch + ", I need :");
                                d.put(key, p());
                                break;
                            default:
                                final StringBuilder k = new StringBuilder().append(ch);
                                StringBuilder s = null;
                                boolean nbs1 = true;
                                char ch2;
                                s1:
                                while (true)
                                    switch (ch2 = (char) read()) {
                                        case ' ': case '\t':
                                            if (s == null)
                                                s = new StringBuilder().append(ch2);
                                            else
                                                s.append(ch2);
                                            break;
                                        case '\\':
                                            nbs1 = !nbs1;
                                            if (nbs1)
                                                k.append('\\');
                                            break;
                                        case 'r':
                                            if (!nbs1) {
                                                nbs1 = true;
                                                k.append('\r');
                                                break;
                                            }
                                            k.append('r');
                                            break;
                                        case 'n':
                                            if (!nbs1) {
                                                nbs1 = true;
                                                k.append('\n');
                                                break;
                                            }
                                            k.append('n');
                                            break;
                                        case '/':
                                            if (!nbs1)
                                                nbs1 = true;
                                            k.append('/');
                                            break;
                                        case ':':
                                            if (nbs1)
                                                break s1;
                                            else
                                                nbs1 = true;
                                        default:
                                            if (s != null) {
                                                k.append(s).append(ch2);
                                                s = null;
                                            } else
                                                k.append(ch2);
                                            break;
                                    }
                                d.put(k.toString(), p());
                                break;
                        }
                case '[':
                    final JsonList l = new JsonList();
                    char ch1;
                    while (true)
                        switch (ch1 = (char) read()) {
                            case ' ': case '\t': case '\r': case '\n': case ',': break;
                            case ']': return l;
                            default:
                                p = ch1;
                                l.add(p());
                                break;
                        }
                case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                    final StringBuilder num = new StringBuilder().append(cha);
                    char ch2;
                    while (true)
                        switch (ch2 = (char) read()) {
                            case '.':
                                if (num.indexOf(".") != -1)
                                    throw new IOException("Unexpected " + ch2);
                            case '0': case '1': case '2': case '3': case '4': case '5': case '6': case '7': case '8': case '9':
                                num.append(ch2);
                                break;
                            case ',': case ']': case '}': case '{': case '[': case '"':
                                p = ch2;
                            case ' ': case '\t': case '\r': case '\n':
                                if (num.indexOf(".") == -1)
                                    return new JsonElement(Integer.parseInt(num.toString()));
                                return new JsonElement(Double.parseDouble(num.toString()));
                            case 'f': case 'F':
                                return new JsonElement(Float.parseFloat(num.toString()));
                            case 'b': case 'B':
                                return new JsonElement(num.toString().equals("1"));
                            default:
                                throw new IOException("Unexpected " + ch2);
                        }
                case '"':
                    final StringBuilder str = new StringBuilder();
                    char ch3;
                    boolean nbs = true;
                    while (true)
                        switch (ch3 = (char) read()) {
                            case '\\':
                                nbs = !nbs;
                                if (nbs)
                                    str.append('\\');
                                break;
                            case '"':
                                if (nbs)
                                    return new JsonElement(str.toString());
                                nbs = true;
                            case 'n':
                                if (!nbs) {
                                    str.append('\n');
                                    nbs = true;
                                } else
                                    str.append('n');
                                break;
                            case 'r':
                                if (!nbs) {
                                    str.append('\r');
                                    nbs = true;
                                } else
                                    str.append('r');
                                break;
                            case '/':
                                if (!nbs)
                                    nbs = true;
                                str.append('/');
                                break;
                            case 'u':
                                if (!nbs) {
                                    nbs = true;
                                    str.append((char) Core.fromHexInt("" + (char) read() + (char) read() + (char) read() + (char) read()));
                                } else
                                    str.append('u');
                                break;
                            default:
                                if (!nbs) {
                                    str.append('\\');
                                    System.out.println("Error: \\" + ch3);
                                    nbs = true;
                                }
                                str.append(ch3);
                                break;
                        }
                default:
                    final StringBuilder sb = new StringBuilder().append(cha);
                    char ch4;
                    boolean nbs1 = true;
                    int i = 1;
                    while (true)
                        switch (ch4 = (char) read()) {
                            case '\\':
                                nbs1 = !nbs1;
                                if (nbs1)
                                    sb.append('\\');
                                break;
                            case ',': case '}': case ']': case '{': case '[':
                                if (nbs1) {
                                    p = ch4;
                                    return new JsonElement(sb.toString());
                                }
                            default:
                                if (!nbs1) nbs1 = true;
                                sb.append(ch4);
                                if (i < 5) {
                                    if (i == 3 && sb.toString().equals("true"))
                                        return new JsonElement(true);
                                    if (i == 4 && sb.toString().equals("false"))
                                        return new JsonElement(false);
                                    i++;
                                }
                                break;
                        }
            }
    }
}