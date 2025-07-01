package illa4257.flashlauncher.components;

import illa4257.i4Framework.base.Context;
import illa4257.i4Framework.base.components.Component;
import illa4257.i4Framework.base.graphics.Color;

public class CubeLoader extends Component {
    private float i = 0;

    public CubeLoader() {
        onTick(this::repaint);
    }

    @Override
    public void paint(final Context c) {
        super.paint(c);

        final int size = 10;
        final float w = width.calcFloat(), s = w / size,

                e = (size - 1) * s;

        final Color color = getColor("color");
        c.setColor(i < 7 ? color : color.withAlpha(8 - i));

        if (i < 1)
            c.drawLine(s, e, s, Math.max(e - i * size * s, s));
        else if (i < 2) {
            c.drawLine(s, e, s, s);
            c.drawLine(s, s, Math.min(s + (i - 1) * size * s, e), s);
        } else if (i < 3) {
            c.drawLine(s, e, s, s);
            c.drawLine(s, s, e, s);
            c.drawLine(e, s, e, Math.min(s + (i - 2) * size * s, e));
        } else if (i < 4) {
            c.drawLine(s, e, s, s);
            c.drawLine(s, s, e, s);
            c.drawLine(e, s, e, e);
            c.drawLine(e, e, Math.max(e - (i - 3) * size * s, s), e);
        } else if (i < 5) {
            final float a1 = (i - 4) * s, a2 = (i - 4) * 4 * s;
            c.drawLine(s, e - a1, s, s + a1);
            c.drawLine(s, s + a1, e - a2, s + a1 + a1);
            c.drawLine(e - a2, s + a1 + a1, e - a2, e);
            c.drawLine(s, e - a1, e - a2, e);
        } else if (i < 6) {
            final float s2 = s + s, s3 = s2 + s, s4 = s3 + s, a3 = (i - 5) * 4 * s, a4 = (i - 5) * s;
            c.drawLine(s, e - s, s, s2);
            c.drawLine(s, s2, e - s4, s3);
            c.drawLine(e - s4, s3, e - s4, e);
            c.drawLine(s, e - s, e - s4, e);

            c.drawLine(e - s4, s3, e - s4 + a3, s3 - a4);
        } else if (i < 7) {
            final float s2 = s + s, s3 = s2 + s, s4 = s3 + s, a3 = (i - 6) * 4 * s, a4 = (i - 6) * s, a5 = (i - 6) * 6 * s;
            c.drawLine(s, e - s, s, s2);
            c.drawLine(s, s2, e - s4, s3);
            c.drawLine(e - s4, s3, e - s4, e);
            c.drawLine(s, e - s, e - s4, e);

            c.drawLine(s, s2, s + a3, s2 - a4);
            c.drawLine(e - s4, s3, e, s2);
            c.drawLine(e - s4, e, e - s4 + a3, e - a4);

            c.drawLine(e, s2, e - a3, s2 - a4);
            c.drawLine(e, s2, e, s2 + a5);
        } else {
            final float s2 = s + s, s3 = s2 + s, s4 = s3 + s;
            c.drawLine(s, e - s, s, s2);
            c.drawLine(s, s2, e - s4, s3);
            c.drawLine(e - s4, s3, e - s4, e);
            c.drawLine(s, e - s, e - s4, e);

            c.drawLine(s, s2, s + s4, s2 - s);
            c.drawLine(e - s4, s3, e, s2);
            c.drawLine(e - s4, e, e, e - s);

            c.drawLine(e, s2, e - s4, s2 - s);
            c.drawLine(e, s2, e, e - s);
        }

        i += 0.016f;
        if (i >= 8)
            i = 0;
    }
}