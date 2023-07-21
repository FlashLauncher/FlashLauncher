package UIL;

import UIL.base.IColor;
import UIL.base.IFont;
import Utils.Core;
import Utils.IniGroup;
import Utils.RRunnable;

public class Theme {
    // DEFAULT
    private int borderRadius;
    private IColor
            backgroundColor,
            backgroundAccentColor,
            foregroundColor,
            foregroundAccentColor,
            textSelectionColor,
            textHintColor,
            authorForegroundColor,
            categoriesBackgroundColor,
            categoriesForegroundColor;
    private IFont font;

    // ProgressBar
    private int pbBorderRadius;

    // FSChooser
    private IColor fscForeground;

    public static Theme current = null;

    // DEFAULT
    public static final IColor
            BACKGROUND_COLOR = () -> current.backgroundColor.get(),
            BACKGROUND_ACCENT_COLOR = () -> current.backgroundAccentColor == null ? current.backgroundColor.get() : current.backgroundAccentColor.get(),
            FOREGROUND_COLOR = () -> current.foregroundColor.get(),
            FOREGROUND_ACCENT_COLOR = () -> current.foregroundAccentColor == null ? current.foregroundColor.get() : current.foregroundAccentColor.get(),
            TEXT_SELECTION_COLOR = () -> current.textSelectionColor.get(),
            TEXT_HINT_COLOR = () -> current.textHintColor.get(),
            AUTHOR_FOREGROUND_COLOR = () -> current.authorForegroundColor == null ? current.foregroundColor.get() : current.authorForegroundColor.get(),
            CATEGORIES_BACKGROUND_COLOR = () -> current.categoriesBackgroundColor == null ? current.backgroundColor.get() : current.categoriesBackgroundColor.get(),
            CATEGORIES_FOREGROUND_COLOR = () -> current.categoriesForegroundColor == null ? current.foregroundColor.get() : current.categoriesForegroundColor.get();

    public static final IFont FONT = new IFont() {
        @Override public String getName() { return current.font.getName(); }
        @Override public Object get() { return current.font.get(); }
    };
    public static final RRunnable<Integer> BORDER_RADIUS = () -> current.borderRadius;

    // ProgressBar
    public static final RRunnable<Integer> PB_BORDER_RADIUS = () -> current.pbBorderRadius > -1 ? current.pbBorderRadius : current.borderRadius;

    // FSChooser
    public static final IColor FSC_FOREGROUND = () -> current.fscForeground == null ? current.foregroundColor.get() : current.fscForeground.get();

    public static Theme parse(final IniGroup ini) {
        final Theme t = new Theme();
        t.borderRadius = ini.getAsInt("border-radius");
        t.backgroundColor = UI.color(ini.getAsString("background-color"));
        t.foregroundColor = UI.color(ini.getAsString("foreground-color"));
        t.textSelectionColor = UI.color(ini.getAsString("text-selection-color"));
        t.textHintColor = UI.color(ini.getAsString("text-hint-color"));
        t.backgroundAccentColor = ini.has("background-accent-color") ? UI.color(ini.getAsString("background-accent-color")) : null;
        t.foregroundAccentColor = ini.has("foreground-accent-color") ? UI.color(ini.getAsString("foreground-accent-color")) : null;
        {
            String font = "System";
            for (String f : ini.getAsString("font").split(",")) {
                f = Core.removeStart(f, " ");
                if (UI.fontExists(f)) {
                    font = f;
                    break;
                }
            }

            if (ini.has("font-height"))
                t.font = UI.fontByHeight(font, UI.FontStyle.PLAIN, ini.getAsFloat("font-height"));
            else
                t.font = UI.font(font, UI.FontStyle.PLAIN, ini.getAsInt("font-size"));
        }

        try {
            final IniGroup pb = ini.getAsGroup("ProgressBar");
            t.pbBorderRadius = pb.has("border-radius") ? pb.getAsInt("border-radius") : -1;
        } catch (Exception ignored) {
            t.pbBorderRadius = -1;
        }
        try {
            final IniGroup fsc = ini.getAsGroup("FSChooser");
            t.fscForeground = fsc.has("foreground-color") ? UI.color(fsc.getAsString("foreground-color")) : null;
        } catch (Exception ignored) {
            t.fscForeground = null;
        }

        try {
            final IniGroup mg = ini.getAsGroup("market");
            if (mg == null) {
                t.authorForegroundColor = null;
                t.categoriesBackgroundColor = null;
                t.categoriesForegroundColor = null;
            } else {
                t.authorForegroundColor = mg.has("author-foreground-color") ? UI.color(mg.getAsString("author-foreground-color")) : null;
                t.categoriesBackgroundColor = mg.has("categories-background-color") ? UI.color(mg.getAsString("categories-background-color")) : null;
                t.categoriesForegroundColor = mg.has("categories-foreground-color") ? UI.color(mg.getAsString("categories-foreground-color")) : null;
            }
        } catch (final Exception ex) {
            t.authorForegroundColor = null;
            t.categoriesBackgroundColor = null;
            t.categoriesForegroundColor = null;
            ex.printStackTrace();
        }

        return t;
    }
}
