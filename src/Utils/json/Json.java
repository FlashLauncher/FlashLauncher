package Utils.json;

import java.io.IOException;
import java.io.InputStream;

public abstract class Json extends InputStream implements AutoCloseable {
    int p = -1;

    public static JsonElement parse(final String str) throws IOException {
        final char[] l = str.toCharArray();
        try (final Json j = new Json() {
            private int i = 0;

            @Override
            public int read() {
                if (p != -1) {
                    final int p2 = p;
                    p = -1;
                    return p2;
                }
                if (l.length == i)
                    throw new IndexOutOfBoundsException("String size = " + l.length + ", index = " + i);
                return l[i++];
            }
        }) {
            return j.p();
        }
    }

    public static JsonElement parse(final InputStream is, final boolean autoClose) throws IOException {
        try (final Json j = new Json() {
            public int read() throws IOException {
                if (p != -1) {
                    final int p2 = p;
                    p = -1;
                    return p2;
                }
                final int p2 = is.read();
                if (p2 == -1)
                    throw new IOException("-1");
                return p2;
            }

            @Override
            public void close() throws IOException {
                if (autoClose)
                    is.close();
                super.close();
            }
        }) {
            return j.p();
        }
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
                                final StringBuilder key = new StringBuilder();
                                char ch1;
                                boolean nbs = true;
                                s1:
                                while (true)
                                    switch (ch1 = (char) read()) {
                                        case '\\':
                                            nbs = !nbs;
                                            if (nbs)
                                                key.append('\\');
                                            break;
                                        case '"':
                                            if (nbs)
                                                break s1;
                                            key.append(ch1);
                                            nbs = true;
                                            break;
                                        case 'n':
                                            if (!nbs) {
                                                key.append('\n');
                                                nbs = true;
                                            } else
                                                key.append('n');
                                            break;
                                        case 'r':
                                            if (!nbs) {
                                                key.append('\r');
                                                nbs = true;
                                            } else
                                                key.append('r');
                                            break;
                                        default:
                                            if (!nbs)
                                                nbs = true;
                                            key.append(ch1);
                                            break;
                                    }
                                while ((ch = (char) read()) == ' ' || ch == '\t') {}
                                if (ch != ':')
                                    throw new IOException("Unexpected char " + ch + ", I need :");
                                d.put(key.toString(), p());
                                break;
                            default:
                                final StringBuilder k = new StringBuilder().append(ch);
                                StringBuilder s = null;
                                boolean nbs1 = false;
                                char ch2;
                                s1:
                                while (true)
                                    switch (ch2 = (char) read()) {
                                        case ' ': case '\t':
                                            if (s == null)
                                                s = new StringBuilder().append(ch2);
                                            else s.append(ch2);
                                            break;
                                        case '\\':
                                            nbs1 = !nbs1;
                                            if (nbs1)
                                                k.append('\\');
                                            break;
                                        case ':':
                                            if (nbs1)
                                                break s1;
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
                                return new JsonElement(num.indexOf(".") != -1 ? Double.parseDouble(num.toString()) : Integer.parseInt(num.toString()));
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
                            default:
                                if (!nbs)
                                    System.out.println("Error: \\" + ch3);
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