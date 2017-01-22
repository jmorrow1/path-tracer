package render;

import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;
import tracer.Point;

/**
 * 
 * Draws a list of Points as dots using Processing's point() function.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class RenderDots extends Render {
    // style
    protected LabelScheme labelScheme = new Enumerate();
    protected boolean drawLabels = false;
    protected float labelTextSize = 6;

    public RenderDots(List<Point> ts) {
        super(ts);
    }
    
    public RenderDots(Point[] ts) {
        super(ts);
    }

    @Override
    public void draw(PGraphics g) {
        // style
        g.strokeWeight(style.strokeWeight);
        g.stroke(style.strokeColor);
        g.strokeCap(style.strokeCap);

        // dots
        if (drawLabels) {
            g.textSize(labelTextSize);
            g.textAlign(CENTER, BOTTOM);
        }
        for (int i = 0; i < pts.size(); i++) {
            Point pt = pts.get(i);
            g.point(pt.x, pt.y);
            if (drawLabels) {
                g.text(labelScheme.nthLabel(i), pt.x, pt.y - 2);
            }
        }
    }

    public void setLabelScheme(LabelScheme labelScheme) {
        this.labelScheme = labelScheme;
    }

    public void setDrawLabels(boolean drawLabels) {
        this.drawLabels = drawLabels;
    }

    public void setLabelTextSize(float labelTextSize) {
        this.labelTextSize = labelTextSize;
    }

    public static interface LabelScheme {
        public String nthLabel(int n);
    }

    public final static class Enumerate implements LabelScheme {
        public String nthLabel(int n) {
            return "" + n;
        }
    }

    public final static class Alphabet implements LabelScheme {
        public String nthLabel(int n) {
            String[] ss = convertBase(n, 26).split(" ");
            String t = "";
            for (int i = 0; i < ss.length; i++) {
                t += (char) (((int) 'a') + Integer.valueOf(ss[i]));
            }
            return t;
        }
    }

    private static String convertBase(int n, int base) {
        int q = n / base;
        int r = n % base;

        if (q == 0) {
            return Integer.toString(r);
        } else {
            return convertBase(q, base) + " " + Integer.toString(r);
        }
    }
}
