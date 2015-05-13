package net.iubris.fractal_landscapes.mtfractal.applet;
/**
 * --
 * original code:
 * Fractal Mountains - rectangular subdivision
 * MtFractal.java
 *
 * Copyright (c) 1998, 2000 Bruce Wilson.
 *
 * Bruce Wilson, 4/8/98
 * 
 * http://www536.pair.com/bgw/applets/1.02/MtFractal/MtFractal.java
 * --
 * 
 * enhanced by:
 * Massimiliano Leone - http://plus.google.com/+MassimilianoLeone
 * search for "k0z" comment to find code editing
 * above all, a new equation is provided for perturbation: 
 * see line 479 ("perturbHeight" method in FVertex class)  
 *  
 * 2015, GPL license
 */

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Random;

public class MtFractalApplet extends Applet implements Runnable {

	private static final long serialVersionUID = 5908678461919852939L;
//	private boolean start;
	private boolean _loop;

	public MtFractalApplet() {
		_margin = 120;
		_corner_offset = 150;

		_delay = 2000;
		_iterations = 4;
		_H = 4;
		
		_sea_level = 0;
		
		_debug = false;
	
		// k0z
		_loop = true;

		setBackground( Color.white );
	}
	
	public void init() {
		String param;
		
		param = getParameter( "margin" );
		if ( param != null )
			_margin = Integer.parseInt( param );
		
		param = getParameter( "corner_offset" );
		if ( param != null )
			_corner_offset = Integer.parseInt( param );
		
		param = getParameter( "delay" );
		if ( param != null ) {
			_delay = Integer.parseInt( param );
		}
		
		param = getParameter( "iterations" );
		if ( param != null ) {
			_iterations = Integer.parseInt( param );
		}
		
		param = getParameter( "H" );
		if ( param != null ) {
			_H = Integer.parseInt( param );
			// k0z
			setH(_H);
		}
		
		param = getParameter( "seaLevel" );
		if ( param != null ) {
			_sea_level = Integer.parseInt( param );
		}
		
		param = getParameter( "debug" );
		if ( (param != null) && param.equals("true") ) {
			_debug = true;
		}
	}

	// k0z
	public void setIterations(int iterations) {
		_iterations = iterations;
	}
	public void setSeaLevel(int seaLevel) {
		_sea_level = seaLevel;
	}
	public void setH(int H) {
		_H = H;
		if (_H < 10)
			_corner_offset = 200;
		else 
			_corner_offset = 50;
	}
	public void setDelay(int delay) {
		_delay = delay;
	}
	public void setLoop(boolean loop) {
		_loop = loop;
	}
	// end k0z
	
	void createStartRectangles() {
		_rectangles = new FRectangleList();

		int x = _margin;
		int y = _margin + 2*_corner_offset;
		
		Rectangle b = 
//				bounds();  // applet size
			new Rectangle(450, 450);

		// k0z: -4 *
		_width = b.width - 4 * _margin;
		_height = b.height - 4 * (_margin); // + _corner_offset;

		FVertex v1 = new FVertex( x, (y + _corner_offset) );
		FVertex v2 = new FVertex( (x + _width - _corner_offset), y );
		FVertex v3 = new FVertex( (x + _width), (y + _height - _corner_offset) );
		FVertex v4 = new FVertex( (x + _corner_offset), (y + _height) );
/* -----
		v1.perturbHeight( v1, v3 );
		v2.perturbHeight( v2, v4 );
		v3.perturbHeight( v3, v1 );
		v4.perturbHeight( v4, v2 );
----- */
		_rectangles.addElement( new FRectangle(v1, v2, v3, v4) );
	}

	public void start() {
		if ( _thread == null ) {
			_thread = new Thread( this );
			_thread.start();
		}
	}
	
	public void stop() {
//		if ( (_thread != null) && _thread.isAlive() )
//			_thread.stop();

		_thread = null;
	}

	public void run() {
		int  iterations;
		long tstart;

		
		while ( _loop ) {
			iterations = _iterations;
			_curr_iter = 0;
			_tdelta = 0;

			createStartRectangles();

			while ( iterations-- > 0  ) {
				++_curr_iter;

				tstart = System.currentTimeMillis();

				try {
					_rectangles = subdivide();
				} catch ( OutOfMemoryError e ) {
					System.out.println( e + " run(), iteration " + _curr_iter );
				}

				_tdelta = System.currentTimeMillis() - tstart;

				repaint();

				try {
					Thread.sleep( _delay );
				} catch( InterruptedException e ) { }
			}

			// extra wait before starting new display
			try {
				Thread.sleep( 8 * _delay );
			} catch( InterruptedException e ) { }
		}
	}

	private long _tdelta;     // time a subdivision took to complete
	private int  _curr_iter;  // current iteration
	
	// Subdivide each rectangle currently in the _rectangles list;
	// return a new list of containing the subdivided rectangles
	FRectangleList subdivide() {
		FRectangleList rectangles = new FRectangleList();

		// k0z: added generics parameter
		Enumeration<FRectangle> e = _rectangles.elements();
		FRectangle r;
		
		while ( e.hasMoreElements() ) {
			r = (FRectangle)e.nextElement();
			r.subdivide( rectangles );
		}

		return rectangles;
	}

	public void paint( Graphics g ) {
		Rectangle b = 
				getBounds();

		g.drawRect( b.x, b.y, b.width - 1, b.height - 1 );

		if ( _rectangles == null )
			return;

		Enumeration<FRectangle> e = _rectangles.elements();
		FRectangle r;
		
		while ( e.hasMoreElements() ) {
			r = e.nextElement();
			r.draw( g, _sea_level );
		}
		
		if ( _debug )
			printStatistics( g, System.out );
	}

	public void printStatistics( Graphics g, PrintStream os ) {
//		g.drawString(""+_width+"x"+_height+"+"+_margin, 10, 25);
		// k0z
		g.drawString("sea_level:"+_sea_level + " loop:"+_loop + " delay:"+_delay + " smoothness:"+_H, 10, 15);
		
		Runtime rt = Runtime.getRuntime();

		long total_memory = rt.totalMemory();
		long free_memory = rt.freeMemory();
		long memory_used = total_memory - free_memory;

		String msg = "Iteration " + _curr_iter +": " + _tdelta +
	    			 " msec...total memory=" + total_memory +
	   				 ", free memory=" + free_memory;

		g.drawString( msg, 10, 35 );

		if ( _curr_iter == 1 ) {
			String os_name = System.getProperty( "os.name" );
			String os_version = System.getProperty( "os.version" );
			String os_arch = System.getProperty( "os.arch" );

			String java_version = System.getProperty( "java.version" );
			String java_vendor = System.getProperty( "java.vendor" );

			os.println();
			os.println( os_name + " " + os_version + " (" + os_arch +
			            "), JDK " + java_version + " (" + java_vendor + ")" );

			os.println( "Iteration\t# Rectangles\tMsec\tMemory Used\tFree Memory\tTotal Memory" );
		}
		
		os.println( _curr_iter + "\t" + _rectangles.size() + "\t" + _tdelta + "\t" +
					 memory_used + "\t" + free_memory + "\t" + total_memory);
	}

	private Thread _thread;

	private FRectangleList _rectangles;
	
	private int _width;
	private int _height;
	
	private int _margin;
	private int _corner_offset;

	private int _iterations;
	private int _delay;
	public static int _H;
	
	private int _sea_level;

	private boolean _debug;

}  // end class MtFractal


/**
 * One rectangle in the grid
 */
class FRectangle {

	FRectangle( FVertex v1, FVertex v2, FVertex v3, FVertex v4 ) {
		_edge1 = new FEdge( v1, v2 );
		_edge2 = new FEdge( v2, v3 );
		_edge3 = new FEdge( v3, v4 );
		_edge4 = new FEdge( v4, v1 );
		_next = null;
	}

	FRectangle( FEdge e1, FEdge e2, FEdge e3, FEdge e4 ) {
		_edge1 = e1;
		_edge2 = e2;
		_edge3 = e3;
		_edge4 = e4;
		_next = null;
	}

	final void draw( Graphics g, int floor ) {
		// Rudimentary hidden line removal: don't draw the
		// rectangle if its frontmost corner (vertex 4) is
		// higher than the others
/* -----
		int h4 = _edge4._v1._y + _edge4._v1._h;
		if ( (h4 <= (_edge1._v1._y + _edge1._v1._h)) &&
		     (h4 <= (_edge2._v1._y + _edge2._v1._h)) &&
		     (h4 <= (_edge3._v1._y + _edge3._v1._h)) )
			return;

		// or simply...
		if ( (_edge4._v1._y + _edge4._v1._h) < (_edge2._v1._y + _edge2._v1._h) )
			return;
----- */
		_edge1.draw( g, floor );
		_edge2.draw( g, floor );
		_edge3.draw( g, floor );
		_edge4.draw( g, floor );
	}

	// Subdivide one FRectangle into four; add to the given list
	final void subdivide( FRectangleList rectangles ) {
		FVertex mid1 = _edge1.subdivide();
		FVertex mid2 = _edge2.subdivide();
		FVertex mid3 = _edge3.subdivide();
		FVertex mid4 = _edge4.subdivide();

		// Calculate the center of the four midpoints - the center of the rectangle
		int xc = (mid1._x + mid2._x + mid3._x + mid4._x) / 4;
		int yc = (mid1._y + mid2._y + mid3._y + mid4._y) / 4;
		int hc = (mid1._h + mid2._h + mid3._h + mid4._h) / 4;

		FVertex mid5 = new FVertex( xc, yc, hc );

		FEdge e5 = new FEdge( mid1, mid5 );
		FEdge e6 = new FEdge( mid2, mid5 );
		FEdge e7 = new FEdge( mid3, mid5 );
		FEdge e8 = new FEdge( mid4, mid5 );

		// pay attention to the sense (direction) of the edges when creating the
		// new rectangles from them
		rectangles.addElement( new FRectangle(_edge1._esub1, e5, e8.reverse(), _edge4._esub2) );
		rectangles.addElement( new FRectangle(_edge1._esub2, _edge2._esub1, e6, e5.reverse()) );
		rectangles.addElement( new FRectangle(e6.reverse(), _edge2._esub2, _edge3._esub1, e7) );
		rectangles.addElement( new FRectangle(e8, e7.reverse(), _edge3._esub2, _edge4._esub1) );
	}

	private FEdge _edge1;
	private FEdge _edge2;
	private FEdge _edge3;
	private FEdge _edge4;

	// use package access for efficiency
	FRectangle _next;

}  // end class FRectangle


/**
 * An edge of a rectangle
 */
class FEdge {

	FEdge( FVertex v1, FVertex v2 ) {
		_v1 = v1;
		_v2 = v2;
	}

	/**
	 * Return another FEdge with vertices in the reverse order
	 * and a reference to the same midpoint as this FEdge.
	 *
	 * The reference to a common midpoint is what keeps
	 * subdivided edges together.
	 */
	final FEdge reverse() {
		FEdge e = new FEdge( _v2, _v1 );
//		e._mid = _mid;
		
		_other = e;
		e._other = this;

		return e;
	}

	final void draw( Graphics g, int floor ) {
		// lower bound on height - current floor ("sea level")
		int h1 = (_v1._h < floor) ? floor : _v1._h;
		int h2 = (_v2._h < floor) ? floor : _v2._h;

//		h1 = h2 = 0;  // debugging

		// raise the y coordinate by the height to give a 3D effect
		g.drawLine( _v1._x, (_v1._y - h1), _v2._x, (_v2._y - h2) );
	}

	final FVertex subdivide() {
		// "Other" edge already subdivided ?
		if ( (_other != null) && (_other._esub1 != null) ) {
			_esub1 = _other._esub2.reverse();
			_esub2 = _other._esub1.reverse();
			_mid = _other._mid;
		} else {
			// Find the midpoint of the edge
			_mid = new FVertex( ((_v1._x + _v2._x) / 2),
		                    	 ((_v1._y + _v2._y) / 2),
		                        ((_v1._h + _v2._h) / 2) );

			// Perturb the height of the new midpoint
			_mid.perturbHeight( _v1, _v2 );

			_esub1 = new FEdge( _v1, _mid );
			_esub2 = new FEdge( _mid, _v2 );
		}

		return _mid;
	}

	// Keep data members with package protection for efficient access
	FVertex _v1;
	FVertex _v2;

	// Other edge that shares the same vertices in reverse sense
	FEdge _other;

	// Midpoint from subdivision
	FVertex _mid;

    // Replacement edges from subdivision
	FEdge _esub1;
	FEdge _esub2;

}  // end class FEdge


/**
 * A vertex with (x,y) position and height (altitude)
 */
class FVertex {

	FVertex( int x, int y, int h ) {
		_x = x;  _y = y;  _h = h;
	}

	FVertex( int x, int y ) {
		_x = x;  _y = y;  _h = 0;
	}

	/**
	 * Perturb the height by an amount proportionate to the distance
	 * between the two given vertices
    */
	final void perturbHeight( FVertex v1, FVertex v2 ) {
		float factor = _random.nextFloat();

		int xdiff = v1._x - v2._x;
		xdiff = (xdiff < 0) ? -xdiff : xdiff;

		int ydiff = v1._y - v2._y;
		ydiff = (ydiff < 0) ? -ydiff : ydiff;

		// original equation
//		_h = _h + (int)(factor * ((xdiff + ydiff) / MtFractal._H));
		
		// k0z new equation
		double pow = Math.pow(0.5, (factor) * MtFractalApplet._H/ 2);
		double h = _h + (xdiff + ydiff) *(pow);
		System.out.println(xdiff + ydiff+", "+h);
		_h = (int) h;
		
//		_h = _h + (int)(factor * ((xdiff + ydiff) / 4));
		
//		_h = _h + (int)((xdiff + ydiff)*( Math.pow(0.5, (factor) * MtFractal._H/ 2) ));
		
//		double pow = Math.pow(0.5, (_h)*MtFractal._H/2);
//		double h = (xdiff + ydiff)*factor*pow;
//		System.out.println(h);
//		_h = _h+ (int) h;

//		h = 0;  // debugging
	}

	// Keep data members with package protection for efficient access
	int _x;
	int _y;
	int _h;  // height

	private static Random _random = new Random();

}  // end class FVertex


/**
 * A linked list of Rectangles - more efficient than Vector
 * for the simple needs of this applet
 */
class FRectangleList {

	FRectangleList() { }

	final void addElement( FRectangle r ) {
		if ( _first == null ) {
			_first = r;
			_last = r;
		} else {
			_last._next = r;
			_last = r;
			r._next = null;
		}
		
		++_count;
	}

	FRectangle getFirst() { return _first; }

	int size() { return _count; }

	Enumeration<FRectangle> elements() {
		return new FRectangleListEnumeration( this );
	}

	private FRectangle _first = null;
	private FRectangle _last = null;

	private int  _count = 0;

}  // end class FRectangleList


class FRectangleListEnumeration implements Enumeration<FRectangle> {

	FRectangleListEnumeration( FRectangleList frlist ) {
		_frnext = frlist.getFirst();
	}

	public boolean hasMoreElements() {
		return _frnext != null;
	}

	public FRectangle nextElement() {
		FRectangle frcurr = _frnext;
		_frnext = _frnext._next;

		return frcurr;
	}
	
	private FRectangle _frnext;

}  // end class FRectangleListEnumeration