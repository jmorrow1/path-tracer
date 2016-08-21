package paths;

import paths2.IPath2;
import processing.core.PApplet;
import processing.core.PGraphics;

//TODO Could add an (optional) optimization where, when getting points from the T path and the U path,
	//the Blender has the T and U paths return their closest cached points which approximate the desired points
	//rather than interpolate between cached points.

/**
 * 
 * A path that interpolates between two aggregate paths.
 * 
 * @author James Morrow
 *
 * @param <T>
 * @param <U>
 */
public class Blender<T extends IPath, U extends IPath> extends Path {
	private Point ptA = new Point(0, 0), ptB = new Point(0, 0);	
	private float x, y;
	private T a;
	private U b;
	private int drawGranularity;
	private float blendAmt;
	
	/**************************
	 ***** Initialization *****
	 **************************/
	
	public Blender(Blender<T, U> blender) {
		this(blender.a, blender.b, blender.blendAmt, blender.drawGranularity);
	}
	
	public Blender(T a, U b, float blendAmt, int drawGranularity) {
		this.a = a;
		this.b = b;
		this.blendAmt = blendAmt;
		this.drawGranularity = drawGranularity;
	}
	
	/*************************
	 ***** Functionality *****
	 *************************/
	
	@Override
	public void trace(Point pt, float amt) {
		if (reversed) amt = PApplet.map(amt, 0, 1, 1, 0);
		a.trace(ptA, amt);
		b.trace(ptB, amt);
		pt.x = PApplet.lerp(ptA.x, ptB.x, blendAmt);
		pt.y = PApplet.lerp(ptA.y, ptB.y, blendAmt);
	}

	@Override
	public void display(PApplet pa) {
		display(pa, drawGranularity);
	}

	@Override
	public void translate(float dx, float dy) {
		a.translate(dx, dy);
		b.translate(dx, dy);
	}
	
	/*******************************
	 ***** Getters and Setters *****
	 *******************************/
	
	public T getA() {
		return a;
	}

	public void setA(T a) {
		this.a = a;
	}

	public U getB() {
		return b;
	}

	public void setB(U b) {
		this.b = b;
	}

	public int getDrawGranularity() {
		return drawGranularity;
	}

	public void setDrawGranularity(int drawGranularity) {
		this.drawGranularity = drawGranularity;
	}

	public float getBlendAmt() {
		return blendAmt;
	}

	public void setBlendAmt(float blendAmt) {
		this.blendAmt = blendAmt;
	}
	
	public void addToBlendAmt(float dAmt) {
		this.blendAmt = IPath2.remainder(this.blendAmt + dAmt, 1);
	}
	
	public Blender<T, U> clone() {
		return new Blender(this);
	}
}