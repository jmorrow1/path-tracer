package tracer.paths;

import java.util.ArrayList;
import java.util.Collection;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PStyle;
import processing.data.JSONArray;
import processing.data.JSONObject;
import tracer.Drawable;
import tracer.Point;
import tracer.TStyle;

/**
 * 
 * A continuous sequence of points in 2D space.
 * 
 * <br>
 * <br>
 * 
 * Usage:<br>
 * To get a point on an Path p, use p.trace(u) or p.trace(pt, u) where u is a
 * floating point value between 0 (inclusive) and 1 (exclusive) and pt is a Point (a coordinate in 2D
 * space).
 * 
 * <br>
 * <br>
 * 
 * p.trace(0) returns a Point at the beginning of the path. p.trace(0.5) returns
 * a Point in the middle of the path. p.trace(1) returns the point at the end of
 * the path. And so on.
 * 
 * <br>
 * <br>
 * 
 * Alternatively, you can use p.trace(pt, u) which, instead of returning a
 * Point, stores the result of the computation in the given pt. This method is
 * more efficient because it doesn't require a new Point to be allocated.
 * 
 * <br>
 * <br>
 * 
 * To display an Path p, use p.display(g) where g is a PGraphics object.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public abstract class Path implements Drawable {
    protected final static Point pt = new Point(0, 0);
    protected boolean reversed;
    protected int sampleCount;
    protected TStyle style;
    public final static float ALMOST_ONE = 0.99999f; 
//    protected final static float DEFAULT_SAMPLES_PER_UNIT_LENGTH = 0.02f;

    public Path() {
        this(100);
    }

    /**
     * 
     * @param sampleCount the number of sample points
     */
    public Path(int sampleCount) {
        this.sampleCount = sampleCount;
        this.style = new TStyle();
    }
    
    /**
     * Derive a Path by connecting a sequence of points located on this Path with lines.
     * @param us 1-dimensional coordinates of the sequence of points
     * @return The derivative path
     */
    public final Shape derivePath(float[] us) {
        return Shape.derivePath(this, us);
    }
    
    /**
     * Derive a Path by connecting a sequence of points located on a source Path with lines.
     * @param src The source path
     * @param us 1-dimensional coordinates of the sequence of points
     * @return The derivative path
     */
    public static Shape derivePath(Path src, float[] us) {
        if (us.length == 0) {
            return new Shape(new Point[] {});
        }
        else {
            Point[] pts = new Point[us.length];
            for (int i=0; i<us.length; i++) {
                pts[i] = src.trace(us[i]);
            }
            return new Shape(pts);
        }
    }

    /**
     * Draws the path.
     * 
     * @param g A PGraphics object on which to draw the path
     */
    public void draw(PGraphics g) {
        if (sampleCount != -1) {
            style.apply(g);
            draw(g, sampleCount);
        }
    }

    /**
     * Creates a segment of the path starting at trace(u1) and ending at
     * trace(u2);
     * 
     * @param u1 The 1D coordinate of the segment's start, a value within [0, 1)
     * @param u2 The 1D coordinate of the segment's end, a value within [0, 1)
     * @return The segment
     */
    //TODO WORK IN PROGRESS
    public Shape createSegment(float u1, float u2) {
        boolean inRange = (0 <= u1 && u1 < 1 && 0 <= u2 && u2 < 1);
        if (!inRange) {
            throw new IllegalArgumentException(Path.class.getName() + ".draw(g, " + u1 + ", " + u2 + ") called with values outside in the range [0, 1).");
        }

        return new Shape(createSegmentPoints(u1, u2));
    }

    //TODO WORK IN PROGRESS
    private ArrayList<Point> createSegmentPoints(float u1, float u2) {
        if (u1 > u2) {
            ArrayList<Point> pts = new ArrayList<Point>();
            pts.addAll(createSegmentPoints(u1, 1));
            pts.addAll(createSegmentPoints(0, u2));
            return pts;
        } else {
            float length = PApplet.abs(u1 - u2);
            int n = (int) (sampleCount * length);
            float du = length / n;

            ArrayList<Point> pts = new ArrayList<Point>();

            float u = u1;
            for (int i = 0; i < n; i++) {
                trace(pt, u);
                pts.add(new Point(pt));
                u = (u + du) % 1f;
            }

            return pts;
        }
    }
    
    /**
     * 
     * @param u1
     * @param u2
     * @return
     */
    public static float compute1DSegmentLength(float u1, float u2) {
        if (u1 < 0 || u1 > 1 || u2 < 0 || u2 > 1) {
            //TODO do something
        }
        
        if (u1 < u2) {
            return u2 - u1;
        }
        else {
            return (1.0f - u1) + u2;
        }
    }
    
    
    public float compute2DSegmentLength(float u1, float u2) {
        if (u2 == 1) {
            u2 = ALMOST_ONE;
        }
        
        if (u1 == u2) {
            return 0;
        }
        
        if (u1 > u2) {
            float segmentLength = 0;
            float u12 = PApplet.max(ALMOST_ONE,  0.5f * (u1 + 1.0f));  
            if (u12 != 1) {
                segmentLength += compute2DSegmentLength(u1, u12); 
            } 
            segmentLength += compute2DSegmentLength(0.0f, u2);
            return segmentLength;
        }
        else {
            float segmentLength = 0;
            int gapCount = getGapCount();
            for (int i=0; i<gapCount; i++) {
                float gap = getGap(i);
                if (u1 < gap && gap < u2) {
                    float u12 = PApplet.max(gap - 0.00001f, 0.5f * (gap + u1)); //TODO This isn't the best solution
                    segmentLength += compute2DSegmentLength(u1, u12);
                    u1 = gap + 0.00001f; //TODO This isn't the best solution
                }
            }
            
            float length = PApplet.abs(u1 - u2);
            int n = (int) (sampleCount * length);
            float du = length / n;

            float u = u1;
            trace(pt, u);
            Point prevpt = new Point(pt);
            for (int i = 1; i < n; i++) {
                trace(pt, u);         
                segmentLength += PApplet.dist(prevpt.x, prevpt.y, pt.x, pt.y);
                u = (u + du) % 1f;
                prevpt.set(pt);
            }
            
            return segmentLength;
        }
    }

    /**
     * Draws a segment of the path starting at trace(u1) and ending at
     * trace(u2).
     * 
     * @param g A PGraphics object on which to draw the path
     * @param u1 The 1D coordinate of the segment's start
     * @param u2 The 1D coordinate of the segment's end
     */
    public void draw(PGraphics g, float u1, float u2) {        
        boolean inRange = (0 <= u1 && u1 <= 1 && 0 <= u2 && u2 <= 1);
        if (!inRange) {
            throw new IllegalArgumentException(Path.class.getName() + ".draw(g, " + u1 + ", " + u2 + ") called with values outside the range 0 to 1.");
        }
        style.apply(g);
        drawHelper(g, u1, u2);
    }
    
    private void drawHelper(PGraphics g, float u1, float u2) {
        if (u2 == 1) {
            u2 = ALMOST_ONE;
        }
        
        if (u1 == u2) {
            return;
        }
        
        if (u1 > u2) {
            float u12 = PApplet.max(ALMOST_ONE,  0.5f * (u1 + 1.0f));  
            if (u12 != 1) {
                drawHelper(g, u1, u12); 
            } 
            drawHelper(g, 0.0f, u2);
        }
        else {
            int gapCount = getGapCount();
            for (int i=0; i<gapCount; i++) {
                float gap = getGap(i);
                if (u1 < gap && gap < u2) {
                    float u12 = PApplet.max(gap - 0.00001f, 0.5f * (gap + u1)); //TODO This isn't the best solution
                    drawHelper(g, u1, u12);
                    u1 = gap + 0.00001f; //TODO This isn't the best solution
                }
            }
            
            float length = PApplet.abs(u1 - u2);
            int n = (int) (sampleCount * length);
            float du = length / n;
    
            g.beginShape();
            float u = u1;
            for (int i = 0; i <= n; i++) {
                trace(pt, u);
                g.vertex(pt.x, pt.y);
                u = (u + du) % 1f;
            }
            trace(pt, u2);
            g.vertex(pt.x, pt.y);
            g.endShape();
        }
    }

    /**
     * 
     * Given a 1D coordinate, a float, maps the coordinate to a 2D coordinate
     * and stores it in a given Point.
     * 
     * <br>
     * <br>
     * 
     * Maps a given floating point number within [0, 1) to a given Point along the
     * perimeter of the Path.
     * 
     * @param pt The Point in which the result is stored.
     * @param u A number within [0, 1)
     */
    public abstract void trace(Point pt, float u);

    /**
     * Tells whether or not the Path is reversed.
     * 
     * @return true, if the path is set to reversed and false otherwise
     */
    public boolean isReversed() {
        return reversed;
    }

    /**
     * Reverses the orientation of the Path.
     */
    public void reverse() {
        reversed = !reversed;
    }

    @Override
    public abstract Path clone();

    /**
     * A continuous function from real values (floats) to Points.
     * 
     * <br>
     * <br>
     * 
     * Maps a given floating point number within [0, 1) to a Point along the
     * perimeter of the Path.
     * 
     * @param u A number within [0, 1).
     * @return The resulting point.
     */
    public Point trace(float u) {
        Point pt = new Point(0, 0);
        this.trace(pt, u);
        return pt;
    }

    /**
     * Returns the length of the Path.
     * 
     * @return The length of the Path
     */    
    public float getLength() {
        trace(pt, 0);
        float prevx = pt.x;
        float prevy = pt.y;
        
        float du = 1.0f / sampleCount;
        float u = du;
        
        float total = 0;
        
        int gapIndex = 0;
        
        if (0 < getGapCount() && getGap(0) == 0) {
            gapIndex++;
        }
        
        int i=0;
        while (i < sampleCount && u < 1) {
            float gap = (gapIndex < getGapCount()) ? getGap(gapIndex) : -1;
            if (gapIndex < getGapCount() && gap != -1 && gap < u) {
                trace(pt, gap-0.00001f);
                total += PApplet.dist(prevx, prevy, pt.x, pt.y);
                trace(pt, gap+0.00001f);
                prevx = pt.x;
                prevy = pt.y;
                trace(pt, u);
                total += PApplet.dist(prevx, prevy, pt.x, pt.y);
                gapIndex++;
            }
            else {
                trace(pt, u);
                total += PApplet.dist(prevx, prevy, pt.x, pt.y);
            }
            
            prevx = pt.x;
            prevy = pt.y;
            u += du;
            i++;
        }
        
        return total;
    }

    /**
     * Draws the path by approximating it with a given number of sample points,
     * and then connecting those points with lines.
     * 
     * <br>
     * <br>
     * 
     * This is a useful shortcut for classes that implement IPath to use. It
     * allows an IPath to define its proper display method in terms of this
     * function.
     * 
     * @param pa The PApplet to which the path is drawn.
     * @param sampleCount The number of sample points.
     */
    public void draw(PGraphics g, int sampleCount) {
        style.apply(g);
        float amt = 0;
        float dAmt = 1f / sampleCount;
        g.beginShape();
        for (int i = 0; i < sampleCount + 1; i++) {
            trace(pt, amt);
            g.vertex(pt.x, pt.y);
            amt += dAmt;
        }
        g.endShape();
    }
    
    /**
     * Shifts this Path dx units in the x-direction and dy units in the
     * y-direction.
     * 
     * @param dx The number of pixels to shift the path right.
     * @param dy The number of pixels to shift the path down.
     */
    public abstract void translate(float dx, float dy);
    
    /**
     * Returns the slope of the Point on the Path at trace(u).
     * 
     * @param u The 1D coordinate of the Path
     * @return The slope at trace(u)
     */
    public float slope(float u) {
        if (u >= 0.00001f) {
            Point a = this.trace(u - 0.00001f);
            Point b = this.trace(u);
            return Point.slope(a, b);
        } else {
            Point a = this.trace(u + 0.00001f);
            Point b = this.trace(u);
            return Point.slope(a, b);
        }
    }
    
    /**
     * Sets the number of sample points the Path uses for various computations
     * @param sampleCount The number of sample points
     */
    public void setSampleCount(int sampleCount) {
        this.sampleCount = sampleCount;
    }
    
    /**
     * Gives the number of sample points the Path uses for various computations.
     * @return The number of samples points.
     */
    public int getSampleCount() {
        return sampleCount;
    }

    /**
     * Returns the number of discontinuities in the Path.
     * 
     * @return The number of discontinuities in the Path
     */
    public abstract int getGapCount();

    /**
     * 
     * Gives the ith discontinuity in the Path as a 1D coordinate.
     * Gaps should be unique and indexed in ascending order.
     * In other words, this must hold true: gap(i) < gap(i+1) as i varies from 0 to getGapCount()-1.
     * 
     * Gives -1 if the index is valid.
     * 
     * @param i The index
     * @return The ith discontinuity as a value within [0, 1)
     */
    public abstract float getGap(int i);
    
    /**
     * Indicates whether or not there is a gap exactly at the 1D coordinate u.
     * @param u The 1D coordinate.
     * @return True, if there is a gap and false otherwise.
     */
    public boolean isGap(float u) {
        float u2 = (u == 0) ? 1
                            : (u == 1) ? 0 
                                       : u;
        for (int i=0; i<getGapCount(); i++) {
            if (u == getGap(i) || u2 == getGap(i)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Indicates whether or not there is a gap between the 1D coordinates u1 and u2
     * @param u1 The first 1D coordinate
     * @param u2 The second 1D coordinate
     * @return True, if there is a gap and false otherwise
     */
    public boolean isGap(float u1, float u2) {
        for (int i=0; i<getGapCount(); i++) {
            float gap = getGap(i);
            if (u1 < gap && gap < u2) {
                return true;
            }
        }
        return false;
    }
    
//    /**
//     * Gives the number of vertices in the Path.
//     * @return The number of vertices in the Path
//     */
//    public int getVertexCount() {
//        return sampleCount;
//    }
//    
//    /**
//     * Returns the 1D coordinate of the ith vertex in the Path.
//     * @param i The index
//     * @return The 1D coordinate of the ith vertex
//     */
//    public float getVertex(int i) {
//        
//    }
//    
//    /**
//     * Indicates whether or not there is a vertex between the 1D coordinates u1 and u2
//     * @param u1 The first 1D coordinate
//     * @param u2 The second 1D coordinate
//     * @return True, if there is a vertex and false otherwise
//     */
//    public boolean isVertex(float u) {
//        float u2 = (u == 0) ? 1
//                            : (u == 1) ? 0 
//                                       : u;
//        for (int i=0; i<getVertexCount(); i++) {
//            if (u == getVertex(i) || u2 == getVertex(i)) {
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * 
     * @return
     */
    public int getStrokeCap() {
        return style.strokeCap;
    }
    
    /**
     * 
     * @return
     */
    public int getStrokeJoin() {
        return style.strokeJoin;
    }
    
    /**
     * 
     * @return
     */
    public float getStrokeWeight() {
        return style.strokeWeight;
    }
    
    /**
     * 
     * @return
     */
    public int getFillColor() {
        return style.fillColor;
    }
    
    /**
     * 
     * @return
     */
    public int getStrokeColor() {
        return style.strokeColor;
    }
    
    /**
     * 
     * @return
     */
    public boolean getFill() {
        return style.fill;
    }
    
    /**
     * 
     * @return
     */
    public boolean getStroke() {
        return style.stroke;
    }
    
    /**
     * Sets the style of the Path.
     * @param style the style
     */
    public void setStyle(PStyle style) {
        this.style = new TStyle(style);
    }
    
    /**
     * Sets the style of the Path.
     * @param style the style
     */
    public void setStyle(TStyle style) {
        this.style = style;
    }
    
    /**
     * Sets the style of the Path to the current style of the PApplet.
     * @param pa the PApplet
     */
    public void setStyle(PApplet pa) {
        this.style = new TStyle(pa.getGraphics().getStyle());
    }
    
    /**
     * 
     * @param strokeCap
     */
    public void setStrokeCap(int strokeCap) {
        style.strokeCap = strokeCap;
    }
    
    /**
     * 
     * @param strokeJoin
     */
    public void setStrokeJoin(int strokeJoin) {
        style.strokeJoin = strokeJoin;
    }
    
    /**
     * 
     * @param strokeWeight
     */
    public void setStrokeWeight(float strokeWeight) {
        style.strokeWeight = strokeWeight;
    }
    
    /**
     * 
     * @param fillColor
     */
    public void setFillColor(int fillColor) {
        style.fillColor = fillColor;
    }
    
    /**
     * 
     * @param strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        style.strokeColor = strokeColor;
    }
    
    /**
     * 
     * @param stroke
     */
    public void setStroke(boolean stroke) {
        style.stroke = stroke;
    }
    
    /**
     * 
     * @param fill
     */
    public void setFill(boolean fill) {
        style.fill = fill;
    }   

    /**
     * Computes the remainder of num / denom.
     * 
     * @param num the numerator
     * @param denom the denominator
     * @return The remainder of num / denom
     */
    public static float remainder(float num, float denom) {
        if (0 <= num && num < denom) {
            return num;
        }
        else if (num > 0) {
            return num % denom;
        }
        else {
            float result = denom - ((-num) % denom);
            if (result == denom) {
                result = 0;
            }
            return result;
        }
    }
    
    /**
     * Returns one of each Path type in the tracer library.
     * Each Path is centered about (0, 0) and has the given radius, r.
     * 
     * @param r The radius
     * @return An array of Paths
     */
    public static Path[] getOneOfEachPathType(float r) {
        Path[] paths = new Path[] {
            new Arc(0, 0, r),
            new Blender(new Circle(0, 0, r), new Rect(0, 0, r, r, RADIUS), 0.5f, 100),
            new Circle(0, 0, r),
            new Composite(new Circle(0, 0, r), new Rect(0, 0, r, r, RADIUS)),
            new CubicBezier(0, 0, r),
            new Ellipse(0, 0, r),  
            new Gesture(),
            new InfinitySymbol(0, 0, r),
            new Line(0, 0, r),
            new Lissajous(0, 0, r),
            new Plot(0, 0, r),
            new Rect(0, 0, r, r, RADIUS),
            new Rose(0, 0, r),
            new Segment(0, 0, r),
            new Shape(0, 0, r),
            new Superellipse(0, 0, r),
            new Supershape(0, 0, r)
        };
        return paths;
    }
    
    /**
     * Adds one of each Path type in the tracer library to the given ArrayList.
     * Each Path is centered about (0, 0) and has the given radius, r.
     *
     * @param r The radius
     * @param paths The ArrayList of Paths
     */
    public static void addOneOfEachPathType(float r, ArrayList<Path> paths) {
        paths.add(new Arc(0, 0, r));
        paths.add(new Blender(new Circle(0, 0, r), new Rect(0, 0, r, r, RADIUS), 0.5f, 100));
        paths.add(new Circle(0, 0, r));
        paths.add(new Composite(new Circle(0, 0, r), new Rect(0, 0, r, r, RADIUS)));
        paths.add(new CubicBezier(0, 0, r));
        paths.add(new Ellipse(0, 0, r));
        paths.add(new Gesture(0, 0, r));
        paths.add(new InfinitySymbol(0, 0, r));      
        paths.add(new Line(0, 0, r));
        paths.add(new Lissajous(0, 0, r));
        paths.add(new Plot(0, 0, r));
        paths.add(new Rect(0, 0, r, r, RADIUS));
        paths.add(new Rose(0, 0, r));
        paths.add(new Segment(0, 0, r));
        paths.add(new Shape(0, 0, r));
        paths.add(new Superellipse(0, 0, r));
        paths.add(new Supershape(0, 0, r));
    }
    
    public static JSONObject toPath(Path path) {
        JSONObject json = new JSONObject();
        JSONArray vertices = new JSONArray();        
        float u = 0;
        float du = 1.0f / path.getSampleCount();
        for (int i=0; i<path.getSampleCount(); i++) {           
            path.trace(pt, u);
            vertices.setJSONObject(i, toJSON(pt));
            u = (u+du) % 1.0f;
        }       
        json.setJSONArray("vertices", vertices);
        return json;
    }
    
    public static JSONObject toJSON(Point pt) {
        JSONObject json = new JSONObject();
        json.setFloat("x", pt.x);
        json.setFloat("y", pt.y);
        return json;
    }
    
    //TODO Add exception handling if file is corrupted
    public static Shape toPath(JSONObject json) {
        return new Shape(toPoints(json.getJSONArray("vertices")));
    }
    
    //TODO Add exception handling if file is corrupted
    private static ArrayList<Point> toPoints(JSONArray json) {
        ArrayList<Point> pts = new ArrayList<Point>();
        for (int i=0; i<json.size(); i++) {
            pts.add(toPoint(json.getJSONObject(i)));
        }
        return pts;
    }
    
    //TODO Add exception handling if file is corrupted
    private static Point toPoint(JSONObject json) {
        return new Point(json.getFloat("x"), json.getFloat("y"));
    }
    
    /**
     * Draws every Path in the Collection.
     * @param paths The Collection of Paths
     * @param g The PGraphics instance
     */
    public static void draw(Collection<Path> paths, PGraphics g) {
        for (Path p : paths) {
            p.draw(g);
        }
    }
    
    /**
     * Draws every Path in the array.
     * @param paths The array of Paths
     * @param g The PGraphics instace
     */
    public static void draw(Path[] paths, PGraphics g) {
        for (Path p : paths) {
            p.draw(g);
        }
    }
}
