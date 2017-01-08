package paths;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;
import processing.core.PGraphics;
import tracer.Point;

/**
 * 
 * A sequence of vertices attached by lines, analogous to using Processing's beginShape(), vertex(), and endShape() functions.
 * 
 * @author James Morrow [jamesmorrowdesign.com]
 *
 */
public class Shape extends Path {
    protected List<Point> vertices2D = new ArrayList<Point>();
    protected List<Float> vertices1D = new ArrayList<Float>();
    
    /**
     * 
     * @param vertices The vertices of the Shape
     */
    public Shape(Point[] vertices) {
        this(listify(vertices));
    }
    
    /**
     * 
     * @param vertices The vertices of the Shape
     */
    public Shape(List<Point> vertices) {
        this.vertices2D = vertices;    
        computeVertices1D();
    }
    
    /**
     * Copy constructor
     * @param s The shape to copy
     */
    public Shape(Shape s) {
        super(s.sampleCount);
        for (int i=0; i<s.vertices2D.size(); i++) {
            this.vertices2D.add(s.vertices2D.get(i).clone());
            this.vertices1D.add(new Float(s.vertices1D.get(i)));
        } 
    }
    
    /**
     * Easy constructor
     * 
     * @param x The x-coordinate of the center of the Path
     * @param y The y-coordinate of the center of the Path
     * @param r The radius of the Path
     */
    public Shape(float x, float y, float r) {        
        int n = 5;
        float dTheta = TWO_PI / n;
        for (int i=0; i<n; i++) {
            float theta = dTheta * i;
            vertices2D.add(new Point(x + r*PApplet.cos(theta), y + r*PApplet.sin(theta)));
        }
        
        computeVertices1D();
    }
    
    private static ArrayList<Point> listify(Point[] xs) {
        ArrayList<Point> ys = new ArrayList<Point>();
        for (int i=0; i<xs.length; i++) {
            ys.add(xs[i]);
        }
        return ys;
    }
    
    protected void computeVertices1D() {
        vertices1D.clear();
        
        float totalDistance = getTotalDistance();
        
        Point a = vertices2D.get(0);
        float u1 = 0;
        vertices1D.add(0f);
        for (int i=1; i<vertices2D.size(); i++) {
            Point b = vertices2D.get(i);
            float du = Line.dist(a, b) / totalDistance;
            float u2 = u1 + du;
            vertices1D.add(u2);
            u1 = u2;
            a = b;
        }
    }
    
    @Override
    public void draw(PGraphics g) {
        style.apply(g);
        g.beginShape();
        for (int i=0; i<vertices2D.size(); i++) {
            Point pt = vertices2D.get(i);
            g.vertex(pt.x, pt.y);
        }
        g.endShape();
    }

    @Override
    public void trace(Point pt, float u) {
        for (int i=1; i<vertices1D.size(); i++) {
            float coord = vertices1D.get(i);
            if (u < coord) {
                float prevCoord = vertices1D.get(i-1);
                float v = PApplet.map(u, prevCoord, coord, 0, 1);
                
                Point a = vertices2D.get(i-1);
                Point b = vertices2D.get(i);
               
                Line.trace(pt, a, b, v);
                break;
            }
        }
    }
    
    @Override
    public void reverse() {
        ArrayList<Point> pts = new ArrayList<Point>();
        int i = vertices2D.size()-1;
        while (i >= 0) {
            pts.add(vertices2D.get(i));
            i--;
        }
        
        ArrayList<Float> fs = new ArrayList<Float>();
        int j = vertices1D.size()-1;
        while (j >= 0) {
            Float a = vertices1D.get(j);
            fs.add(1.0f - a);
            j--;
        }
    }

    @Override
    public Path clone() {
        ArrayList<Point> pts = new ArrayList<Point>();
        for (Point pt : pts) {
            pts.add(new Point(pt));
        }
        return new Shape(pts);
    }

    @Override
    public void translate(float dx, float dy) {
        for (Point pt : vertices2D) {
            pt.translate(dx, dy);
        }
    }

    @Override
    public int getGapCount() {
        if (isClosed()) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public float getGap(int i) {
        if (isClosed() || i != 0) {
            return -1;
        }
        else {
            return 1;
        }
    }
    
    @Override
    public float getTotalDistance() {
        float dist = 0;
        Point a = vertices2D.get(0);
        for (int i=1; i<vertices2D.size(); i++) {
            Point b = vertices2D.get(i);
            dist += Line.dist(a, b);
            a = b;
        }
        return dist;
    }
    
    /**
     * Adds a vertex to the Shape.
     * @param i The index
     * @param pt The Point to add
     */
    public void addVertex(int i, Point pt) {
        vertices2D.add(i, pt);
        computeVertices1D();
    }
    
    /**
     * Adds a vertex to the Shape.
     * @param pt The Point to add
     */
    public void addVertex(Point pt) {
        vertices2D.add(pt);
        computeVertices1D();
    }
    
    /**
     * Remove the vertex at index i.
     * @param i The index
     */
    public void removeVertex(int i) {
        vertices2D.remove(i);
        computeVertices1D();
    }
    
    /**
     * Sets the vertex at index i to the given Point.
     * @param i The index
     * @param pt The new vertex
     */
    public void setVertex(int i, Point pt) {
        vertices2D.set(i, pt);
        computeVertices1D();
    }
    
    /**
     * Returns the number of vertices in the Shape.
     * @return The number of vertices in the Shape
     */
    public int getVertexCount() {
        return vertices2D.size();
    }
    
    /**
     * Tells whether or not the Path is closed.
     * @return True if the path is closed, false otherwise
     */
    public boolean isClosed() {
        return vertices2D.size() == 0 || vertices2D.get(vertices2D.size()-1).equals(vertices2D.get(0));
    }
    
    /**
     * Converts an arbitrary type of Path into a Shape, using the sampleCount from the Path.
     * @param path The path
     * @return The Shape
     */
    public static Shape toShape(Path path) {
        return toShape(path, path.getSampleCount());
    }
    
    /**
     * Converts an arbitrary type of Path into a Shape, using a given sampleCount.
     * @param path The Path
     * @param sampleCount The number of sample points to use
     * @return The Shape
     */
    public static Shape toShape(Path path, int sampleCount) {
        ArrayList<Point> pts = new ArrayList<Point>();
        float u = 0;
        float du = 1f / sampleCount;
        for (int i=0; i<sampleCount; i++) {
            pts.add(path.trace(u));
            u += du;
        }
        return new Shape(pts);
    }
    
    /**
     * Blends two Paths and makes a Shape.
     * @param a The first Path
     * @param b The second Path
     * @param amt A value from 0 to 1, determining how much to weight Path b over Path a.
     * @param sampleCount The number of sample points to use
     * @return The Shape
     */
    public static Shape blend(Path a, Path b, float amt, int sampleCount) {
        ArrayList<Point> pts = new ArrayList<Point>();
        float u = 0;
        float du = 1f / sampleCount;
        Point ptA = new Point(0, 0);
        Point ptB = new Point(0, 0);
        for (int i=0; i<sampleCount; i++) {
            a.trace(ptA, amt);
            b.trace(ptB, amt);
            pts.add(Point.lerp(ptA, ptB, amt));
            u += du;
        }
        return new Shape(pts);
    }
}
