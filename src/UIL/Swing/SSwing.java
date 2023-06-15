package UIL.Swing;

import UIL.*;
import UIL.base.*;
import Utils.fixed.FixedMap;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SSwing extends UI {
    public static final float DELTA = 1000f / 60,
                              ANIMATION = DELTA / 1000;

    public static final RenderingHints RH = new RenderingHints(new FixedMap<>(
            new RenderingHints.Key[]{RenderingHints.KEY_ANTIALIASING, RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.KEY_RENDERING},
            new Object[]{RenderingHints.VALUE_ANTIALIAS_ON, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_RENDER_QUALITY}
    ));

    public static int MULTIPLIER = 16;

    @Override public IColor newColor(final int r, final int g, final int b, final int a) { return new SColor(r, g, b, a); }
    @Override public IColor newColor(final int r, final int g, final int b) { return new SColor(r, g, b); }

    @Override
    public boolean isFontExists(final String fontName) {
        for (String fn : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            if (fn.equals(fontName))
                return true;
        return false;
    }

    @Override
    public final float getFontHeight(String fontName, int fontSize) {
        final FontMetrics m = new Canvas().getFontMetrics(new Font(fontName, Font.PLAIN, fontSize));
        return m.getAscent();
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
    @Override public IText newText(final Object text) { return new SText(text); }
    @Override public ICheckBox newCheckBox(final Object text, final boolean checked) { return new SCheckBox(text, checked); }
    @Override public ITextField newTextField(final String text) { return new STextField(text); }
    @Override public IButton newButton() { return new SButton(); }
    @Override public IButton newButton(LangItem text) { return new SButton(text); }
    @Override public IButton newButton(String text) { return new SButton(text); }
    @Override public IButton newButton(IImage img) { return new SButton(img); }
    @Override public IButton newButton(Object text, IImage img) { return new SButton(text, img); }
    @Override public IComboBox newComboBox() { return new SComboBox(); }
    @Override public IContainer newPanel() { return new SPanel(); }
    @Override public IScrollPane newScrollPane() { return new SScrollPane(); }
    @Override public IMenuBar newMenuBar() { return new SMenuBar(); }
    @Override public IImageView newImageView(ImagePosMode posMode, ImageSizeMode sizeMode) { return new SImageView(posMode, sizeMode); }

    @Override public void invoke(Runnable action) { SwingUtilities.invokeLater(action); }

    @Override
    public void invokeAndWait(Runnable action) throws InterruptedException {
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }
}