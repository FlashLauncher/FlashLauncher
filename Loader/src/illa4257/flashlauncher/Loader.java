package illa4257.flashlauncher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class Loader {
    public static class ResourceClassLoader extends ClassLoader {
        private final byte[] data;

        private final ConcurrentLinkedQueue<ResourceClassLoader> resourceClassLoaders = new ConcurrentLinkedQueue<>();

        public ResourceClassLoader(final ClassLoader parent, final byte[] jar) {
            super(parent);
            data = jar;
        }

        public ResourceClassLoader(final ClassLoader parent, final ClassLoader loader, final String name) throws IOException {
            super(parent);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            final byte[] buff = new byte[1024];
            int l;
            try (final InputStream is = loader.getResourceAsStream(name)) {
                if (is == null)
                    throw new IOException("No resource called " + name);
                while ((l = is.read(buff)) != -1)
                    bos.write(buff, 0, l);
            }
            data = bos.toByteArray();
        }

        public ResourceClassLoader(final ClassLoader parent, final String name) throws IOException {
            this(parent, parent, name);
        }

        private InputStream internalOpenStream(final String name) {
            try {
                final JarInputStream jis = new JarInputStream(new ByteArrayInputStream(data));
                JarEntry e;
                while ((e = jis.getNextJarEntry()) != null)
                    if (name.equals(e.getName()) && !e.isDirectory())
                        return jis;
                jis.close();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        public InputStream getResourceAsStream(final String name) {
            final InputStream is = internalOpenStream(name);
            return is == null ? super.getResourceAsStream(name) : is;
        }

        protected Class<?> internalLoadClass(final String name) {
            Class<?> c = findLoadedClass(name);
            if (c != null)
                return c;
            try (final InputStream is = internalOpenStream(name.replaceAll("\\.", "/") + ".class")) {
                if (is != null) {
                    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buff = new byte[1024];
                    int l;
                    while ((l = is.read(buff)) != -1)
                        bos.write(buff, 0, l);
                    buff = bos.toByteArray();
                    return defineClass(name, buff, 0, buff.length);
                }
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> c = internalLoadClass(name);
            if (c != null)
                return c;
            for (final ResourceClassLoader cl : resourceClassLoaders) {
                c = cl.internalLoadClass(name);
                if (c != null)
                    return c;
            }
            return super.loadClass(name, resolve);
        }

        public void add(final ResourceClassLoader resourceClassLoader) {
            resourceClassLoaders.add(resourceClassLoader);
        }
    }

    public static ConcurrentHashMap<String, ClassLoader> loaders = new ConcurrentHashMap<>();

    public static void main(final String... args) throws Exception {
        final ResourceClassLoader cl = new ResourceClassLoader(ClassLoader.getSystemClassLoader(), "FLCore.jar");
        loaders.put("FLCore", cl);
        cl.loadClass("illa4257.flashlauncher.FLInit").getMethod("main", String[].class).invoke(null, (Object) args);
    }
}