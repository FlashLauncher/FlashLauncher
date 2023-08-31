package UIL;

import UIL.base.*;
import Utils.*;
import Utils.fixed.FixedEntry;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class UI {
    public abstract boolean isRunnable();

    public enum FontStyle {
        PLAIN,
        BOLD,
        ITALIC
    }

    public abstract IColor newColor(final int r, final int g, final int b, final int a);
    public abstract IColor newColor(final int r, final int g, final int b);

    public abstract boolean isFontExists(final String font);
    public abstract float getFontHeight(final String fontName, final FontStyle style, final int fontSize);
    public abstract float getStringWidth(final IFont font, final String string);
    public abstract IFont newFont(final String name, final FontStyle style, final int size);

    // Components
    public abstract IFrame newFrame(final String title);
    public abstract IDialog newDialog(final IFrame owner, final String title);

    public abstract IComponent newLoader();
    public abstract IProgressBar newProgressBar();
    public abstract IText newText();
    public abstract IText newText(final Object text);
    public abstract ICheckBox newCheckBox(final Object text, final boolean checked);
    public abstract ITextField newTextField(final String text);
    public abstract IButton newButton();
    public abstract IButton newButton(final LangItem text);
    public abstract IButton newButton(final String text);
    public abstract IButton newButton(final IImage img);
    public abstract IButton newButton(final Object text, final IImage img);
    public abstract IToggleButton newToggleButton(final Object text, final IImage img, final boolean checked);
    public abstract IComboBox newComboBox();
    public abstract IImageView newImageView(final ImagePosMode posMode, final ImageSizeMode sizeMode);
    public abstract IContainer newPanel();
    public abstract IScrollPane newScrollPane();
    public abstract IMenuBar newMenuBar();

    public abstract void invoke(final Runnable action);
    public abstract void invokeAndWait(final Runnable action) throws InterruptedException;

    public abstract void dispose();


    public static UI UI;

    public static boolean check() { return UI.isRunnable(); }

    public static IColor color(final int r, final int g, final int b, final int a) { return UI.newColor(r, g, b, a); }
    public static IColor color(final int r, final int g, final int b) { return UI.newColor(r, g, b); }
    public static IColor color(String col) {
        if (col.startsWith("0x")) {
            col = col.substring(2);
            if (col.length() == 3 || col.length() == 4) {
                final int r = Core.fromHex1as2(col.charAt(0)),
                        g = Core.fromHex1as2(col.charAt(1)),
                        b = Core.fromHex1as2(col.charAt(2));
                if (col.length() == 4)
                    return color(r, g, b, Core.fromHex1as2(col.charAt(3)));
                return color(r, g, b);
            }
            final int r = Core.fromHex2(col.substring(0, 2)),
                    g = Core.fromHex2(col.substring(2, 4)),
                    b = Core.fromHex2(col.substring(4, 6));
            if (col.length() == 8)
                return color(r, g, b, Core.fromHex2(col.substring(6, 8)));
            return color(r, g, b);
        }
        throw new RuntimeException("IDK code " + col);
    }

    public abstract IImage newImage(final byte[] data) throws IOException;

    public static boolean fontExists(final String font) { return UI.isFontExists(font); }
    public static IFont fontByHeight(final String fontName, final FontStyle fontStyle, final float height) { return UI.newFont(fontName, fontStyle, (int) Math.ceil(height / UI.getFontHeight(fontName, fontStyle, Math.round(height)) * height)); }
    public static float stringWidth(final IFont font, final String string) { return UI.getStringWidth(font, string); }
    public static IFont font(final String name, final FontStyle style, final int size) { return UI.newFont(name, style, size); }

    public static IImage image(final byte[] data) throws IOException { return UI.newImage(data); }
    public static IImage image(final InputStream is) throws IOException, InterruptedException { return UI.newImage(IO.readFully(is)); }
    public static IImage image(final InputStream is, final boolean close) throws IOException, InterruptedException { return UI.newImage(IO.readFully(is, close)); }
    public static IImage image(final File file) throws IOException, InterruptedException { return UI.newImage(IO.readFully(file)); }
    public static IImage image(final String path) throws IOException, InterruptedException { return UI.newImage(FS.ROOT.readFully(path)); }

    // Components
    public static IFrame frame(final String title) { return UI.newFrame(title); }
    public static IDialog dialog(final IFrame owner, final String title) { return UI.newDialog(owner, title); }

    public static IComponent loader() { return UI.newLoader(); }
    public static IProgressBar progressBar() { return UI.newProgressBar(); }
    public static IText text() { return UI.newText(); }
    public static IText text(final Object text) { return UI.newText(text); }
    public static ICheckBox checkBox(final Object text, final boolean checked) { return UI.newCheckBox(text, checked); }
    public static ITextField textField(final String text) { return UI.newTextField(text); }
    public static IButton button() { return UI.newButton(); }
    public static IButton button(final LangItem text) { return UI.newButton(text); }
    public static IButton button(final String text) { return UI.newButton(text); }
    public static IButton button(final IImage img) { return UI.newButton(img); }
    public static IButton button(final Object text, final IImage img) { return UI.newButton(text, img); }
    public static IToggleButton toggleButton(final Object text, final IImage img, final boolean checked) { return UI.newToggleButton(text, img, checked); }
    public static IComboBox comboBox() { return UI.newComboBox(); }
    public static IImageView imageView(final ImagePosMode posMode, final ImageSizeMode sizeMode) { return UI.newImageView(posMode, sizeMode); }
    public static IContainer panel() { return UI.newPanel(); }
    public static IScrollPane scrollPane() { return UI.newScrollPane(); }
    public static IMenuBar menuBar() { return UI.newMenuBar(); }
    public static FSChooser fsChooser(final IFrame owner, final String title) { return new FSChooser(owner, title); }
    public static FSChooser fsChooser(final IFrame owner) { return new FSChooser(owner, "File Selection"); }

    public static void run(final Runnable action) { UI.invoke(action); }
    public static void runAndWait(final Runnable action) throws InterruptedException { UI.invokeAndWait(action); }
    public static void after(final int sleep, final Runnable action) {
        final Thread t = new Thread(() -> {
            try {
                Thread.sleep(sleep);
                action.run();
            } catch (final Exception ex) {
                ex.printStackTrace();
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }


    public static IColor TRANSPARENT, WHITE, BLACK, RED, GREEN, BLUE, YELLOW, PURPLE;

    public static void initColors() {
        TRANSPARENT = color(0, 0, 0, 0);
        WHITE = color(255, 255, 255);
        BLACK = color(0, 0, 0);
        RED = color(255, 0, 0);
        GREEN = color(0, 255, 0);
        BLUE = color(0, 0, 255);
        YELLOW = color(255, 255, 0);
        PURPLE = color(128, 0, 128);
    }

    public static final RRunnable<Integer> ZERO = () -> 0;
}
