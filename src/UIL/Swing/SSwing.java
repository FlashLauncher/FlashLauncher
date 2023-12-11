package UIL.Swing;

import UIL.*;
import UIL.base.*;
import Utils.fixed.FixedMap;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static java.awt.RenderingHints.*;

public class SSwing extends UI {
    public static final float
            DELTA = 1000f / 60,
            ANIMATION = DELTA / 1000
    ;

    public static final int MULTIPLIER = 16;

    public static final RenderingHints RH = new RenderingHints(new FixedMap<>(
            new Key[]    { KEY_ANTIALIASING  , KEY_TEXT_ANTIALIASING  , KEY_RENDERING       , KEY_INTERPOLATION            },
            new Object[] { VALUE_ANTIALIAS_ON, VALUE_TEXT_ANTIALIAS_ON, VALUE_RENDER_QUALITY, VALUE_INTERPOLATION_BILINEAR }
    ));

    private static int callGCEvery = -1, fc = 0;

    private final Thread updater = new Thread(() -> {
        try {
            long l1 = System.currentTimeMillis(), l2;
            while (true) {
                synchronized (SFPSTimer.timers) {
                    if (SFPSTimer.timers.isEmpty()) {
                        System.gc();
                        fc = 0;
                        SFPSTimer.timers.wait();
                        continue;
                    }
                }
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        for (final SFPSTimer t : SFPSTimer.timers)
                            t.run();
                    } catch (final Exception ex) {
                        ex.printStackTrace();
                    }
                });
                l2 = System.currentTimeMillis();
                final float d = SSwing.DELTA - (l2 - l1);
                l1 = l2;
                if (d > 0)
                    Thread.sleep((int) Math.ceil(d));
            }
        } catch (final Throwable ex) {
            if (ex instanceof InterruptedException)
                return;
            ex.printStackTrace();
        }
    }) {{ start(); }};

    @Override public boolean isRunnable() { return !GraphicsEnvironment.isHeadless(); }

    @Override public IColor newColor(final int r, final int g, final int b, final int a) { return new SColor(r, g, b, a); }
    @Override public IColor newColor(final int r, final int g, final int b) { return new SColor(r, g, b); }

    @Override
    public boolean isFontExists(final String fontName) {
        for (final String fn : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            if (fn.equals(fontName))
                return true;
        return false;
    }

    @Override
    public final float getFontHeight(final String fontName, final FontStyle fontStyle, final int fontSize) {
        return new Canvas().getFontMetrics(new Font(fontName, fontStyle == FontStyle.BOLD ? Font.BOLD : fontStyle == FontStyle.ITALIC ? Font.ITALIC : Font.PLAIN, fontSize)).getHeight();
    }

    @Override
    public float getStringWidth(final IFont font, final String string) {
        return new Canvas().getFontMetrics((SFont) font.get()).stringWidth(string);
    }

    @Override
    public IFont newFont(final String name, final FontStyle style, final int size) {
        return new SFont(name, style == FontStyle.BOLD ? Font.BOLD : style == FontStyle.ITALIC ? Font.ITALIC : Font.PLAIN, size);
    }

    @Override public IImage newImage(final byte[] data) throws IOException { return new SImage(data); }
    @Override public IFrame newFrame(final String title) { return new SFrame(title); }
    @Override public IDialog newDialog(final IFrame owner, final String title) { return new SDialog(owner, title); }
    @Override public IComponent newLoader() { return new SLoader(); }
    @Override public IProgressBar newProgressBar() { return new SProgressBar(); }
    @Override public IText newText() { return new SText(); }
    @Override public IText newText(final Object text) { return new SText(text); }
    @Override public ICheckBox newCheckBox(final Object text, final boolean checked) { return new SCheckBox(text, checked); }
    @Override public ITextField newTextField() { return new STextField(); }
    @Override public ITextField newTextField(final String text) { return new STextField(text); }
    @Override public IButton newButton() { return new SButton(); }
    @Override public IButton newButton(final LangItem text) { return new SButton(text); }
    @Override public IButton newButton(final String text) { return new SButton(text); }
    @Override public IButton newButton(final IImage img) { return new SButton(img); }
    @Override public IButton newButton(final Object text, final IImage img) { return new SButton(text, img); }
    @Override public IToggleButton newToggleButton(final Object text, final IImage img, final boolean checked) { return new SToggleButton(text, img,checked); }
    @Override public IComboBox newComboBox() { return new SComboBox(); }
    @Override public IContainer newPanel() { return new SPanel(); }
    @Override public IScrollPane newScrollPane() { return new SScrollPane(); }
    @Override public IMenuBar newMenuBar() { return new SMenuBar(); }
    @Override public IImageView newImageView(final ImagePosMode posMode, final ImageSizeMode sizeMode) { return new SImageView(posMode, sizeMode); }
    @Override public void invoke(final Runnable action) { SwingUtilities.invokeLater(action); }

    @Override
    public void invokeAndWait(final Runnable action) throws InterruptedException {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override public void dispose() { updater.interrupt(); }
}