package tracer.paths;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A subpath of a parent Path.
 * If its parent changes, the Segment changes accordingly.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class Segment extends Path {
    protected float u1, u2;
    protected Path parent;
    
    /**
     * 
     * @param parent The parent path
     * @param u1 The first 1D coordinate
     * @param u2 The second 1D coordinate
     */
    public Segment(Path parent, float u1, float u2) {
        this.parent = parent;
        setU1(u1);
        setU2(u2);
    }
    
    /**
     * Copy constructor.
     * 
     * @param segment
     */
    public Segment(Segment segment) {
        this(segment.parent.clone(), segment.u1, segment.u2);
    }
    
    /**
     * Easy constructor.
     * 
     * @param x The center x-coordinate
     * @param y The center y-coordinate
     * @param r The radius
     */
    public Segment(float x, float y, float r) {
        this(new Rect(0, 0, r, r, RADIUS), 0, 0.33f);
    }
    
    @Override
    public void trace(Point target, float u) {
        if (u < 0 || u >= 1) {
            throw new IllegalArgumentException(Segment.class.getName() + ".trace(pt, " + u + ") called where the second argument is outside the range 0 (inclusive) to 1 (exclusive).");
        }
        
        float v = reversed ? PApplet.map(u, 0, 1, u2, u1) : PApplet.map(u, 0, 1, u1, u2);
        parent.trace(target, v);
    }
    
    @Override
    public void draw(PGraphics g) {
        super.draw(g); //<-- placeholder
        
        //TODO
    }
    
    @Override
    public Path clone() {
        return new Segment(this);
    }
    
    @Override
    public void translate(float dx, float dy) {
        parent.translate(dx, dy);
    }
    
    @Override
    public int getGapCount() {
        int count = 0;
        
        if (u1 == u2) {
            count++;
        }
        
        for (int j=0; j<parent.getGapCount(); j++) {
            float gap = parent.getGap(j);
            if (u1 < gap && gap < u2) {
                count++;
            }
        } 
      
        return count;
    }
    
    @Override
    public float getGap(int i) {
        if (u1 == u2) {
            return 0;
        }
        
        if (!reversed) {
            for (int j=0; j<parent.getGapCount(); j++) {
                float gap = parent.getGap(j);
                if (u1 < gap && gap < u2) {
                    if (i == 0) {
                        return gap;
                    }
                    i--;
                }
            }
        }
        else {
            for (int j=parent.getGapCount()-1; j>=0; j--) {
                float gap = parent.getGap(j);
                if (u1 < gap && gap < u2) {
                    if (i == 0) {
                        gap = 1.0f - gap;
                        if (gap == 1.0f) {
                            gap = 0.0f;
                        }
                        return gap;
                    }
                    i--;
                }
            }
        }
      
        return -1;
    }

    /**
     * Translates the segment's location within its path by the given 1D value.
     * @param du The 1D value
     */
    public void translate(float du) {
        u1 = remainder(u1 + du, 1);
        u2 = remainder(u2 + du, 1);
    }
    
    /**
     * Gives the first 1D coordinate of the Segment.
     * @return The first 1D coordinate
     */
    public float getU1() {
        return u1;
    }

    /**
     * Sets the first 1D coordinate of the Segment.
     * @param u1 The second 1D coordinate
     */
    public void setU1(float u1) {
        this.u1 = remainder(u1, 1);
    }

    /**
     * Gives the second 1D coordinate of the Segment.
     * @return The second 1D coordinate
     */
    public float getU2() {
        return u2;
    }

    /**
     * Sets the second 1D coordinate of the Segment.
     * @param u2 The second 1D coordinate
     */
    public void setU2(float u2) {
        this.u2 = remainder(u2, 1);
    }
    
    /**
     * Gives the x-coordinate of the first 2D coordinate of the Segment.
     * @return The x-coordinate of the first 2D coordinate
     */
    public float getX1() {
        parent.trace(buffer, u1);
        return buffer.x;
    }
    
    /**
     * Gives the y-coordinate of the first 2D coordinate of the Segment.
     * @return The y-coordinate of the first 2D coordinate
     */
    public float getY1() {
        parent.trace(buffer, u1);
        return buffer.y;
    }
    
    /**
     * Gives the x-coordinate of the second 2D coordinate of the Segment.
     * @return The x-coordinate of the second 2D coordinate
     */
    public float getX2() {
        parent.trace(buffer, u2);
        return buffer.x;
    }
    
    /**
     * Gives the y-coordinate of the second 2D coordinate of the Segment.
     * @return The y-coordinate of the second 2D coordinate
     */
    public float getY2() {
        parent.trace(buffer, u2);
        return buffer.y;
    }

    @Override
    public String toString() {
        return "Segment [u1=" + u1 + ", u2=" + u2 + ", parent=" + parent + "]";
    }
}
