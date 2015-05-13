/*
 * MtFractal.java
 *
 * Fractal Mountains - rectangular subdivision
 *
 * Copyright (c) 1998, 2000 Bruce Wilson.
 *
 * Bruce Wilson, 4/8/98
 * 
 * http://www536.pair.com/bgw/applets/1.02/MtFractal/MtFractal.java
 */
package net.iubris.fractal_landscapes.mtfractal.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Random;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class MtFractalApp extends JFrame implements Runnable, DummyApplet {
	
	private static final long serialVersionUID = -5028369297336173588L;
	private Parameters parameters;
	private int width;
	private int height;
	private final BufferedImage image;
//	private final JLabel jLabel;
	
	static int _H;

	public MtFractalApp(Parameters parameters, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		setSize(512, 384);
		setResizable(false);
		this.parameters = parameters;
		
		setTitle("MtFractal");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		margin = 20;
		cornerOffset = 50;

		delay = 500;
		iterations = 4;
		
		seaLevel = 20;
		
		debug = false;
		
		_H = 50;

//		setBackground( Color.white );
		
		image = new BufferedImage(512-10, 384-10, BufferedImage.TYPE_INT_ARGB);
		getContentPane().add( new JLabel(new ImageIcon( image )) );
//		jLabel = new JLabel("aa");
//		getContentPane().add(jLabel);
		
		setSize(width, height);
		setResizable(true);
		setVisible(true);
		
		pack();
		repaint();
	}
	
	@Override
	public void init() {
		String param;
		
		param = getParameter( "margin" );
		if ( param != null )
			margin = Integer.parseInt( param );
		
		param = getParameter( "cornerOffset" );
		if ( param != null ) {
			cornerOffset = Integer.parseInt( param );
//			System.out.println(cornerOffset);
		}
		
		param = getParameter( "delay" );
		if ( param != null )
			delay = Integer.parseInt( param );
		
		param = getParameter( "iterations" );
		if ( param != null ) {
			iterations = Integer.parseInt( param );
//			System.out.println(iterations);
		}
		
		param = getParameter( "seaLevel" );
		if ( param != null ) {
			seaLevel = Integer.parseInt( param );
//			System.out.println(seaLevel);
		}
		
		param = getParameter( "H" );
		if ( param != null ) {
			setH( Integer.parseInt( param ));
		}
		
		param = getParameter( "debug" );
		if ( (param != null) && param.equals("true") ) {
			debug = true;
			System.out.println("Iteration\t# Rectangles\tMsec\tMemory Used\tFree Memory\tTotal Memory");
		}
		
		
	}
	public void setH(int H) {
		_H = H;
		if (_H < 10)
			cornerOffset = 200;
		else 
			cornerOffset = 50;
	}
	
	private void createStartRectangles() {
		rectangles = new FRectangleList();
		
//		System.out.println(rectangles.size());

		int x = margin;
		int y = margin + cornerOffset;
		
//		Rectangle b = bounds();  // applet size
		
		width = width - 2 * margin;
		height = height - 2 * margin - cornerOffset;

		FVertex v1 = new FVertex( x, (y + cornerOffset) );
		FVertex v2 = new FVertex( (x + width - cornerOffset), y );
		FVertex v3 = new FVertex( (x + width), (y + height - cornerOffset) );
		FVertex v4 = new FVertex( (x + cornerOffset), (y + height) );
/* -----
		v1.perturbHeight( v1, v3 );
		v2.perturbHeight( v2, v4 );
		v3.perturbHeight( v3, v1 );
		v4.perturbHeight( v4, v2 );
----- */
		rectangles.addElement( new FRectangle(v1, v2, v3, v4) );
//		System.out.println(rectangles.size());
	}

	@Override
	public void start() {
		if ( thread == null ) {
			thread = new Thread( this );
			thread.start();
		}
	}
	
	@Override
	public void stop() {
//		if ( (thread != null) && thread.isAlive() ) {
//			thread.stop();
//		}
		thread = null;
	}

	@Override
	public void run() {
		int  iterations;
		long tstart;

		while ( true ) {
			iterations = this.iterations;
			_curr_iter = 0;
			_tdelta = 0;

			createStartRectangles();

			while ( iterations-- > 0  ) {
				++_curr_iter;

				tstart = System.currentTimeMillis();

				try {
					rectangles = subdivide();
				} catch ( OutOfMemoryError e ) {
					System.out.println( e + " run(), iteration " +
						_curr_iter );
				}

				_tdelta = System.currentTimeMillis() - tstart;

				repaint();

				try {
					Thread.sleep( delay );
				} catch( InterruptedException e ) { }
			}

			// extra wait before starting new display
			try {
				Thread.sleep( 4 * delay );
			} catch( InterruptedException e ) { }
		}
	}

	private long _tdelta;     // time a subdivision took to complete
	private int  _curr_iter;  // current iteration
	
	// Subdivide each rectangle currently in the _rectangles list;
	// return a new list of containing the subdivided rectangles
	FRectangleList subdivide() {
		FRectangleList rectangles = new FRectangleList();

		Enumeration<FRectangle> e = rectangles.elements();
		FRectangle r;
		
		while ( e.hasMoreElements() ) {
			r = (FRectangle)e.nextElement();
			r.subdivide( rectangles );
		}

		return rectangles;
	}

	@Override
	public void paint( Graphics g ) {
//		Rectangle b = bounds();
//		g.drawRect( b.x, b.y, b.width - 1, b.height - 1 );
		
		Graphics graphics = image.getGraphics();
		Dimension size = getSize();
//		g.drawRect( size.width, size.height, size.width - 1, size.height - 1 );
		graphics.drawRect( size.width, size.height, size.width - 1, size.height - 1 );

		if ( rectangles == null ) {
			return;
		}

		Enumeration<FRectangle> e = rectangles.elements();
		FRectangle r;
		
		while ( e.hasMoreElements() ) {
			r = e.nextElement();
//			r.draw( g, seaLevel );
			r.draw( graphics, seaLevel );
			repaint();
		}
		
		if ( debug )
			printStatistics( g, System.out );
	}

	private void printStatistics( Graphics g, PrintStream os ) {
		Runtime rt = Runtime.getRuntime();

		long total_memory = rt.totalMemory();
		long free_memory = rt.freeMemory();
		long memory_used = total_memory - free_memory;

		String msg = "Iteration " + _curr_iter +": " + _tdelta +
	    			 " msec...total memory=" + total_memory +
	   				 ", free memory=" + free_memory;

		g.drawString( msg, 10, 15 );

		if ( _curr_iter == 1 ) {
			String os_name = System.getProperty( "os.name" );
			String os_version = System.getProperty( "os.version" );
			String os_arch = System.getProperty( "os.arch" );

			String java_version = System.getProperty( "java.version" );
			String java_vendor = System.getProperty( "java.vendor" );

			os.println();
			os.println( os_name + " " + os_version + " (" + os_arch +
			            "), JDK " + java_version + " (" + java_vendor + ")" );

			os.println( "Iteration\t\t# Rectangles\t\tMsec\tMemory Used\tFree Memory\tTotal Memory" );
		}
		
		os.println( _curr_iter + "\t\t" + rectangles.size() + "\t\t" + _tdelta + "\t" +
					 memory_used/1024/1024 + "\t\t" + free_memory/1024/1024 + "\t\t" + total_memory/1024/1024);
	}

	private Thread thread;

	private FRectangleList rectangles;
	
//	private int width;
//	private int height;
	
	private int margin;
	private int cornerOffset;

	private int iterations;
	private int delay;
	
	private int seaLevel;

	private boolean debug;

	@Override
	public void showStatus(String status) {
		System.out.println(status);
	}

	@Override
	public String getParameter(String parameterName) {
		try {
			Field field = parameters.getClass().getDeclaredField(parameterName);
			field.setAccessible(true);
			return (String) field.get(parameters);
		} catch(NoSuchFieldException e) {
//			e.printStackTrace();
			System.err.println("no field \'"+parameterName+"\'");
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) {
		
//		String pinfo[][] = {
//			{ "levels", "1+", "levels of recursion (depth)" },
//			{ "stop", "1-Levels", "number of non fractal recusions" },
//			{ "fdim", "0.5-1.0", "Fractal dimension" },
//			{ "sleep", "miliseconds", "Sleep time before scrolling" },
//			{ "snooze", "miliseconds", "Sleep time between columns" } 
//		};
//		
//		if (args.length==1 && args[0].equals("-h")) {
//			System.out.println( pinfo );
//			System.exit(0);
//		}
		
		int width = 512;
		int height = 384;
		
		Parameters parameters = null;
		
		if (args.length==0) {
			parameters = new Parameters();
		}
		
		if (args.length==1 && !args[0].equals("-h")) {
			Scanner scanner = new Scanner(System.in);
			
			int margin = scanner.nextInt();
			int cornerOffset = scanner.nextInt();
			int delay = scanner.nextInt();
			int iterations = scanner.nextInt();
			
			int seaLevel = scanner.nextInt();
			int debug = scanner.nextInt();
			
			scanner.close();
			
			parameters = new Parameters(""+margin, ""+cornerOffset, ""+delay, ""+iterations, ""+seaLevel, ""+debug);
		}
		
		// from http://www536.pair.com/bgw/applets/1.02/MtFractal/
		parameters = new Parameters(null, ""+100, null, ""+6, ""+50, ""+true);
	
		MtFractalApp mtFractalApp = new MtFractalApp(parameters, width, height);
		mtFractalApp.setSize(width, height);
		mtFractalApp.init();
		mtFractalApp.start();
		mtFractalApp.run();
	}

	public static class Parameters {
		String margin;
		String cornerOffset;
		String delay;
		String iterations;
		
		String seaLevel;
		String debug;
		
		public Parameters(String margin, String cornerOffset, String delay, String iterations, String seaLevel, String debug) {
			this.margin = margin;
			this.cornerOffset = cornerOffset;
			this.delay = delay;
			this.iterations = iterations;
			this.seaLevel = seaLevel;
			this.debug = debug;
		}

		public Parameters() {}
	}

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

		// original
		_h = _h + (int)(factor * ((xdiff + ydiff) / 4));
		
		// new
		double pow = Math.pow(0.5, (factor) * MtFractalApp._H/ 2);
		double h = _h + (xdiff + ydiff) *(pow);
		System.out.println(xdiff + ydiff+", "+h);
		_h = (int) h;

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
		FRectangleListEnumeration fRectangleListEnumeration = new FRectangleListEnumeration( this );
		return fRectangleListEnumeration;
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