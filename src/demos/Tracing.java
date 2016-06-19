package demos;

import java.util.ArrayList;

import functions.Polygonize;
import paths.Arc;
import paths.Bezier;
import paths.Blender;
import paths.Composite;
import paths.Flower;
import paths.IPath;
import paths.InfinitySymbol;
import paths.Point;
import paths.Superellipse;
import paths.Supershape;
import paths2.Circle;
import paths2.Ellipse;
import paths2.Line;
import processing.core.PApplet;

/**
 * 
 * @author James Morrow
 *
 */
public class Tracing extends PApplet {
	ArrayList<IPath> ts;
	Blender<InfinitySymbol, Superellipse> blender;
	
	Point pt = new Point(0, 0);
	float amt = 0;
	int cellSize = 100;
	
	public static void main(String[] args) {
		PApplet.main("demos.Tracing");
	}
	
	public void settings() {
		size(600, 600, P2D);
	}
	
	public void setup() {	
		ts = initList();
		reposition(ts);
	}
	
	private ArrayList<IPath> initList() {
		float r = 0.4f * cellSize;
		ArrayList<IPath> ts = new ArrayList<IPath>();
		ts.add(new Line(r*cos(0.25f*PI), r*sin(0.25f*PI), r*cos(1.25f*PI), r*sin(1.25f*PI)));
		ts.add(new Circle(0, 0, r));
		ts.add(new Ellipse(0, 0, 2*r, r, CENTER));
		ts.add(Polygonize.makeRegularPolygon(0, 0, r, 6, 0));
		ts.add(Polygonize.makeRegularPolygon(0, 0, 25, 4, QUARTER_PI));
		ts.add(Polygonize.makePolygon(0, 0, r/2, r, 4, QUARTER_PI));
		ts.add(new Arc(0, 0, r, r/2, 0, 1.5f*PI, RADIUS, 50));
		ts.add(new Flower(0, 0, r, 4, 3, 100));
		ts.add(new InfinitySymbol(0, 0, r, 0.75f*r, 50));
		ts.add(new Bezier(random(-r, r), random(-r, r), random(-r, r), random(-r, r),
                          random(-r, r), random(-r, r), random(-r, r), random(-r, r)));		
		blender = new Blender(new InfinitySymbol(0, 0, r, 0.75f*r, 50), new Superellipse(0, 0, r, r, 0.5f, 50), 0.5f, 100);
		ts.add(blender);
		ts.add(new Superellipse(0, 0, r, r, 0.5f, 50));
		Arc a = new Arc(0, 0, r, r, 0, PI, RADIUS, 50);
		a.reverse();
		Arc b = new Arc(cellSize, 0, r, r, PI, TWO_PI, RADIUS, 50);
		ts.add(new Composite(a, b));
		ts.add(new Supershape(0, 0, r, r, 5, 100));
		
		return ts;
	}
	
	private void reposition(ArrayList<IPath> ts) {
		int x = cellSize/2;
		int y = cellSize/2;
		
		for (IPath t : ts) {
			t.translate(x, y);
			
			x += (t instanceof Composite) ? 2*cellSize : cellSize;
			if (x >= width) {
				x = cellSize/2;
				y += cellSize;
			}
		}
	}
	
	public void draw() {
		background(255);

		for (IPath p : ts) {
			drawPath(p);
		}
		
		amt = (amt + 0.005f) % 1f;
	}
	
	private void drawPath(IPath t) {
		noFill();
		strokeWeight(2);
		t.display(this);
	
		strokeWeight(6);
		t.trace(pt, amt);
		pt.display(this);
	}
	
	public void mouseMoved() {
		float blendAmt = map(mouseX, 0, width, 0, 1);
		blender.setBlendAmt(blendAmt);
		Superellipse superellipse = blender.getB();
		float n = map(mouseY, 0, height, 0.25f, 2);
		superellipse.setN(n);
	}
}
