package paths2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import paths.IPath;
import paths.Point;
import processing.core.PApplet;

/**
 * 
 * @author James Morrow
 *
 */
public class GranularPath implements IPath2 {
	private List<Point> vertices;
	private List<Float> segAmts;
	private float cenx, ceny, width, height, perimeter;
	
	/**************************
	 ***** Initialization *****
	 **************************/
	
	public GranularPath(Point[] vertices) {
		this(listify(vertices));
	}
	
	public GranularPath(List<Point> vertices) {
		this.vertices = vertices;
		update();
	}
	
	/**
	 * Construct a GranularPath by taking a snapshot of a Traceable.
	 * 
	 * @param pathDef The Traceable which will be read and then discarded.
	 * @param numVertices The resolution of the snapshot.
	 */
	public GranularPath(IPath pathDef, int numVertices) {
		initVertices(pathDef, numVertices);
	}
	
	private GranularPath(GranularPath path) {
		this.cenx = path.getCenx();
		this.ceny = path.getCeny();
		this.width = path.getWidth();
		this.height = path.getHeight();
		this.perimeter = path.getPerimeter();
		this.segAmts = new ArrayList<Float>(path.segAmts.size());
		for (Float f : path.segAmts) {
			this.segAmts.add(f.floatValue());
		}
		this.vertices = new ArrayList<Point>(path.vertices.size());
		for (Point vtx : vertices) {
			this.vertices.add(new Point(vtx));
		}
		update();
	}
	
	private void initVertices(IPath def, int numVertices) {
		if (vertices == null || vertices.size() != numVertices) {
			vertices = new ArrayList<Point>(numVertices);
		}
		float dAmt = 1f / (numVertices-1);
		float amt = 0;
		for (int i=0; i<numVertices; i++) {
			if (vertices.get(i) == null) {
				vertices.set(i, def.trace(amt));
			}
			else {
				def.trace(vertices.get(i), amt);
			}
			amt += dAmt;
		}
	}
	
	private void computeDimensions() {
		if (vertices.size() > 0) {
			float minX = vertices.get(0).x, maxX = vertices.get(0).x;
			float minY = vertices.get(0).y, maxY = vertices.get(0).y;
			for (int i=1; i<vertices.size(); i++) {
				if (vertices.get(i).x < minX) {
					minX = vertices.get(i).x;
				}
				else if (vertices.get(i).x > maxX) {
					maxX = vertices.get(i).x;
				}
				if (vertices.get(i).y < minY) {
					minY = vertices.get(i).y;
				}
				else if (vertices.get(i).y > maxY) {
					maxY = vertices.get(i).y;
				}
			}
			this.width = maxX - minX;
			this.height = maxY - minY;
			this.cenx = minX + this.width/2f;
			this.ceny = minY + this.height/2f;
		}
		else {
			this.cenx = 0;
			this.ceny = 0;
			this.width = 0;
			this.height = 0;
		}
	}
	
	private void computePerimeter() {
		float[] segLengths = new float[vertices.size()];
		
		this.perimeter = 0;
		
		//compute the total length of the perimeter and lengths of line segments comprising the perimeter
		if (vertices.size() > 0) {
			float ax = vertices.get(0).x;
			float ay = vertices.get(0).y;
			for (int i=1; i<vertices.size(); i++) {
				float bx = vertices.get(i).x;
				float by = vertices.get(i).y;
				segLengths[i] += Line.getLength(ax, ay, bx, by);
				this.perimeter += segLengths[i];
				ax = bx;
				ay = by;
			}
		}
		
		//compute segAmts
		segAmts = new ArrayList<Float>(segLengths.length);
		if (segAmts.size() > 0) {
			float sum = 0;
			for (int i=0; i<segAmts.size()-1; i++) {
				sum += segLengths[i];
				segAmts.set(i, sum / perimeter);
			}
			segAmts.set(segAmts.size()-1, 1f);
		}
	}
	
	/*************************
	 ***** Functionality *****
	 *************************/
	
	@Override
	public void display(PApplet pa) {
		pa.beginShape();
		for (int i=0; i<vertices.size(); i++) {
			pa.vertex(vertices.get(i).x, vertices.get(i).y);
		}
		pa.endShape();
	}

	@Override
	public void trace(Point pt, float amt) {
		amt = IPath2.remainder(amt, 1);
		for (int i=1; i<segAmts.size(); i++) {
			if (amt < segAmts.get(i)) {
				amt = PApplet.map(amt, segAmts.get(i-1), segAmts.get(i), 0, 1);
				Point a = vertices.get(i-1);
				Point b = (i != vertices.size()) ? vertices.get(i) : vertices.get(0);
				Line.trace(pt, a.x, a.y, b.x, b.y, amt);
				break;
			}
		}
	}

	@Override
	public boolean inside(float x, float y) {
		// TODO Implement https://en.wikipedia.org/wiki/Point_in_polygon.
		return false;
	}

	@Override
	public void translate(float dx, float dy) {
		for (Point pt : vertices) {
			pt.x += dx;
			pt.y += dy;
		}
		if (vertices.size() != 0) {
			cenx += dx;
			ceny += dy;
		}
	}

	@Override
	public GranularPath clone() {
		return new GranularPath(this);
	}
	
	/*******************************
	 ***** Getters and Setters *****
	 *******************************/
	
	public void reverse() {
		reverse(segAmts);
		reverse(vertices);
	}
	
	public void update() {
		computeDimensions();
		computePerimeter();
	}
	
	public void setTraceable(IPath pathDef, int numVertices) {
		initVertices(pathDef, numVertices);
		update();
	}
	
	@Override
	public float getPerimeter() {
		return perimeter;
	}

	@Override
	public float getCenx() {
		return cenx;
	}
	
	@Override
	public float getCeny() {
		return ceny;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public float getHeight() {
		return height;
	}
	
	/****************************
	 ***** Static Functions *****
	 ****************************/
	
	private static List<Point> listify(Point[] xs) {
		List<Point> list = new ArrayList<Point>();
		for (int i=0; i<xs.length; i++) {
			list.add(xs[i]);
		}
		return list;
	}
	
	private static List reverse(List in) {
		List out = new ArrayList();
		int i = in.size() - 1;
		while (i >= 0) {
			out.add(in.get(i));
		}
		return out;
	}
}