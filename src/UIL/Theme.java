package UIL;

import UIL.base.IColor;
import UIL.base.IFont;
import Utils.Core;
import Utils.IniGroup;
import Utils.RRunnable;

import java.awt.*;

public class Theme {
    // DEFAULT
    private int borderRadius;
    private IColor
            background,
            foreground,
            textSelection;
    private IFont font;

    // ProgressBar
    private int pbBorderRadius;

    // FSChooser
    private IColor fscForeground;

    public static Theme current = null;

    // DEFAULT
    public static final IColor
            BACKGROUND = () -> current.background.get(),
            FOREGROUND = () -> current.foreground.get(),
            TEXT_SELECTION = () -> current.textSelection.get();

    public static final IFont FONT = () -> current.font.get();
    public static final RRunnable<Integer> BORDER_RADIUS = () -> current.borderRadius;

    // ProgressBar
    public static final RRunnable<Integer> PB_BORDER_RADIUS = () -> current.pbBorderRadius > -1 ? current.pbBorderRadius : current.borderRadius;

    // FSChooser
    public static final IColor FSC_FOREGROUND = () -> current.fscForeground == null ? current.foreground.get() : current.fscForeground.get();

    public static Theme parse(IniGroup ini) {
        final Theme t = new Theme();
        t.borderRadius = ini.getAsInt("border-radius");
        t.background = UI.color(ini.getAsString("background"));
        t.foreground = UI.color(ini.getAsString("foreground"));
        t.textSelection = UI.color(ini.getAsString("text-selection"));
        {
            String font = "System";
            for (String f : ini.getAsString("font").split(",")) {
                f = Core.removeStart(f, " ");
                if (UI.fontExists(f)) {
                    font = f;
                    break;
                }
            }

            if (ini.has("font-height")) {
                final float height = ini.getAsFloat("font-height");
                final float fh = UI.UI.getFontHeight(font, Math.round(height));
                t.font = UI.font(font, UI.FontStyle.PLAIN, (int) Math.ceil(height / fh * height));
            } else
                t.font = UI.font(font, UI.FontStyle.PLAIN, ini.getAsInt("font-size"));
        }

        try {
            final IniGroup pb = ini.getAsGroup("ProgressBar");
            if (pb.has("border-radius"))
                t.pbBorderRadius = pb.getAsInt("border-radius");
            else
                t.pbBorderRadius = -1;
        } catch (Exception ignored) {
            t.pbBorderRadius = -1;
        }
        try {
            final IniGroup fsc = ini.getAsGroup("FSChooser");
            if (fsc.has("foreground"))
                t.fscForeground = UI.color(fsc.getAsString("foreground"));
            else
                t.fscForeground = null;
        } catch (Exception ignored) {
            t.fscForeground = null;
        }
        return t;
    }
}
