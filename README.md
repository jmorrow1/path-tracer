# Tracer
Geometry library for Processing, currently in alpha.

#Basics
##Paths
Paths are basically curves in 2-dimensional space.

The main power of Paths is that they map a 1-dimensional coordinate to a 2-dimensional coordinate using the trace() method. The method trace() takes a single normal value (a value between 0 and 1) and returns a Point in 2-dimensional space.

``` {.java}
float x = 0.5f;
Point pt = path.trace(x);
```

If you want to create an animation, for efficiency you may want to avoid allocating a new Point every time you trace a path. For this use case, there is another version of trace() that takes a Point and a 1-D value. Instead of returning a Point, this version of trace() stores the result of the computation in the given Point.

``` {.java}
float x = 0.5f;
Point pt = new Point(0, 0);
path.trace(pt, x);
```

##Tracers
A Tracer is like a Point that moves along a Path.

Tracers provide a way of creating animations with tracer.

##Easings
Easings are tweening curves that control the speed of an animation over time. For an introduction to easing functions, try [this] (http://gizma.com/easing/).

##Renders
Renders provide a way of creating complex forms and animations with tracer.

A Render stores a List of Tracers and draws them in some way.

``` {.java}
List<Tracer> ts = new ArrayList<Tracer>();
Render r = new RenderShape(ts);
PGraphics g = this.g;
r.draw(g); //draws a polygonal form with the List of Tracers as vertices using Processing's beginShape() and endShape()
```

# Documentation
[Javadoc](http://jamesmorrowdesign.com/tracer/doc/index.html)
