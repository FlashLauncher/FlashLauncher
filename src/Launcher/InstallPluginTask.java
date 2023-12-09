package Launcher;

import UIL.UI;
import UIL.base.IImage;
import Utils.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class InstallPluginTask extends Task {
    private final ListMap<String, byte[]> files;
    private final AtomicBoolean success = new AtomicBoolean();

    public InstallPluginTask(final Map<String, byte[]> files) { this.files = new ListMap<>(files); }

    public InstallPluginTask(final byte[] data) throws IOException {
        files = new ListMap<>();
        try (final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(data))) {
            for (ZipEntry e = zis.getNextEntry(); e != null; e = zis.getNextEntry()) {
                files.put(e.getName(), e.getName().endsWith("/") ? null : IO.readFully(zis, false));
                zis.closeEntry();
            }
        }
    }

    public boolean isSuccess() { return success.get(); }

    @Override
    public void run() throws Throwable {
        InstalledPlugin im = null;
        try {
            final boolean isPlugin = files.containsKey("fl-plugin.ini");
            final IniGroup cfg = new IniGroup(new String(files.get(isPlugin ? "fl-plugin.ini" : "fl-info.ini"), StandardCharsets.UTF_8), false);
            final String id = cfg.getAsString("id"), name = cfg.getAsString("name"), verStr = cfg.getAsString("version"), author = cfg.getAsString("author"), sd = cfg.getAsString("shortDescription"),
                    main = cfg.getAsString("main");

            IImage icon = null;

            try {
                byte[] d = files.get("icon.png");
                if (d == null)
                    d = files.get("icon.jpg");
                if (d != null)
                    icon = UI.image(d);
            } catch (final Exception ex) {
                ex.printStackTrace();
            }

            synchronized (FLCore.installed) {
                for (final InstalledMeta me : FLCore.installed)
                    if (me instanceof InstalledPlugin && id.equals(me.getID())) {
                        im = (InstalledPlugin) me;
                        break;
                    }
                if (im == null)
                    FLCore.installed.add(im = new InstalledPlugin(id, name, new Version(verStr), author, sd) {
                        @Override
                        public String getMarket() {
                            return null;
                        }
                    });
            }

            synchronized (im.c) {
                if (im.locked)
                    throw new Exception("Locked!");
                im.locked = true;
                im.ver = new Version(verStr);
                im.n = name;
                im.author = author;
                im.sd = sd;
                im.icon = icon;
                im.main = main;
                final String m = im.getMarket();
                if (m != null && !m.isEmpty())
                    cfg.put("market", m);
                files.put(isPlugin ? "fl-plugin.ini" : "fl-info.ini", cfg.toString().getBytes(StandardCharsets.UTF_8));
            }

            if (im.file == null)
                im.file = new File(FLCore.LAUNCHER_DIR, "plugins/" + id + ".jar").getAbsoluteFile();
            if (im.file.exists())
                if (im.file.isFile())
                    new FileOutputStream(im.file).close();
                else
                    FS.clearDir(im.file);
            else
                if (im.file.getName().endsWith(".jar")) {
                    im.file.getParentFile().mkdirs();
                    im.file.createNewFile();
                } else
                    im.file.mkdirs();
            if (im.file.isFile())
                try (final ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(im.file.toPath()))) {
                    for (final Map.Entry<String, byte[]> e : files.entrySet())
                        if (e.getValue() == null)
                            zos.putNextEntry(new ZipEntry(e.getKey() + "/"));
                        else {
                            zos.putNextEntry(new ZipEntry(e.getKey()));
                            zos.write(e.getValue(), 0, e.getValue().length);
                            zos.closeEntry();
                        }
                }
            else
                for (final Map.Entry<String, byte[]> e : files.entrySet())
                    if (e.getValue() == null)
                        new File(im.file, e.getKey()).mkdirs();
                    else {
                        final File f = new File(im.file, e.getKey()), p = f.getParentFile();
                        if (!p.exists())
                            p.mkdirs();
                        try (final FileOutputStream fos = new FileOutputStream(f)) {
                            fos.write(e.getValue(), 0, e.getValue().length);
                        }
                    }

            if (im.root != null)
                FS.removeRoot(im.root);
            FS.addRoot(im.root = FS.newFS(im.file));

            if (!id.equals(FlashLauncher.ID))
                synchronized (im.c) {
                    if (im.enabled)
                        im.disable();
                    im.enable();
                }

            success.set(true);
        } catch (final Throwable ex) {
            ex.printStackTrace();
        } finally {
            if (im != null)
                synchronized (im.c) {
                    im.locked = false;
                }
        }
    }
}
