import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

// from http://introcs.cs.princeton.edu/java/22library/IFS.java.html
/*************************************************************************
 *  Compilation:  javac IFS.java
 *  Execution:    java IFS N < input.txt
 *  Dependencies: StdDraw.java
 *
 *  Here are some sample data files:
 *  
 *  http://www.cs.princeton.edu/introcs/22library/barnsley.txt
 *  http://www.cs.princeton.edu/introcs/22library/binary.txt
 *  http://www.cs.princeton.edu/introcs/22library/culcita.txt
 *  http://www.cs.princeton.edu/introcs/22library/cyclosorus.txt
 *  http://www.cs.princeton.edu/introcs/22library/dragon.txt
 *  http://www.cs.princeton.edu/introcs/22library/fern-sedgewick.txt
 *  http://www.cs.princeton.edu/introcs/22library/fishbone.txt
 *  http://www.cs.princeton.edu/introcs/22library/floor.txt
 *  http://www.cs.princeton.edu/introcs/22library/koch.txt
 *  http://www.cs.princeton.edu/introcs/22library/sierpinski.txt
 *  http://www.cs.princeton.edu/introcs/22library/spiral.txt
 *  http://www.cs.princeton.edu/introcs/22library/swirl.txt
 *  http://www.cs.princeton.edu/introcs/22library/tree.txt
 *  http://www.cs.princeton.edu/introcs/22library/zigzag.txt
 *
 *************************************************************************/

public class IFS {
    public static void main(String[] args) {

        // number of iterations
        int T = Integer.parseInt(args[0]);

        // probability distribution for choosing each rule
        double[] dist = 
      		  StdArrayIO.readDouble1D();
//      		  getDist();

        // update matrices
        double[][] cx = 
      		  StdArrayIO.readDouble2D();
//      		  getCx();
        double[][] cy = 
      		  StdArrayIO.readDouble2D();
//      		  getCy();

        // current value of (x, y)
        double x = 0.0, y = 0.0;

        // do T iterations of the chaos game, ensure everything gets drawn
        long start = System.currentTimeMillis();
        sequential(T, dist, cx, cy, x, y); StdDraw.show(0);
//        parallel(T, dist, cx, cy, x, y); StdDraw.show(0);
        
//        Drawer drawer = new Drawer(); parallel2(T, dist, cx, cy, x, y, drawer); drawer.show(0);

        long end = System.currentTimeMillis();
        float time = (end-start)/1000f;
        System.out.println("\n\ntime:"+time+"s");
        
//        System.exit(0);
    }
    
    private static void sequential(int T, double[] dist, double[][] cx, double[][] cy, double x, double y) {
   	 // do T iterations of the chaos game
       for (int t = 0; t < T; t++) { 
           // pick a random rule according to the probability distribution
           int r = StdRandom.discrete(dist); 

           // do the update
           double x0 = cx[r][0]*x + cx[r][1]*y + cx[r][2]; 
           double y0 = cy[r][0]*x + cy[r][1]*y + cy[r][2]; 
           x = x0;
           y = y0;

           // draw the resulting point
           StdDraw.point(x, y); 

           // for efficiency, display only every 100 iterations
           if (t % 250 == 0) StdDraw.show(10);
           
           if (t%(T/5)==0)
         	  System.out.print(t+" ");
       }
    }
    
    private static void parallel(int T, double[] dist, double[][] cx, double[][] cy, double x, double y) {
   	 
//   	 CountDownLatch latch = new CountDownLatch(T);
   	 AtomicReference<Integer> tRef = new AtomicReference<>(0);
   	 AtomicReference<Double> xRef = new AtomicReference<>(x);
   	 AtomicReference<Double> yRef = new AtomicReference<>(y);
   	 
   	 Runnable runnable = new Runnable() {
			@Override
			public void run() {
				if (tRef.get() < T) {
					System.out.println("t: "+tRef.get() );
					// pick a random rule according to the probability distribution
					int r = StdRandom.discrete(dist);
		
					// do the update
					double x0 = cx[r][0] * xRef.get() + cx[r][1] * yRef.get() + cx[r][2];
					double y0 = cy[r][0] * xRef.get() + cy[r][1] * yRef.get() + cy[r][2];
					// x = x0;
					xRef.set(x0);
					// y = y0;
					yRef.set(y0);
		
					// draw the resulting point
					StdDraw.point(xRef.get(), yRef.get());
		
					// for efficiency, display only every 100 iterations
					if (tRef.get() % 100 == 0) {
						System.out.println("showing");
						StdDraw.show(10);
					}
					
					tRef.set( tRef.get()+1 );
					System.out.println(tRef.get());
				}
			}
   	 }; // end runnable
   	 
//   	 for (int i=0; i<T; i++) {
//   		 new Thread(runnable).run();
//   	 }
   	 ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
   	 fixedThreadPool.execute(runnable);
    }
    
    private static void parallel2(int T, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer) {
   	 
   	 AtomicReference<Integer> tRef = new AtomicReference<>(0);
       AtomicReference<Double> xRef = new AtomicReference<>(0.0);
       AtomicReference<Double> yRef = new AtomicReference<>(0.0);
   	 
		List<IFSCallable> ifsCallables = new ArrayList<>();
		System.out.println("adding callables");
		for (int i = 0; i < T; i++) {
			ifsCallables.add(new IFSCallable(tRef, T, dist, cx, cy, x, y, xRef, yRef, drawer));
		}
		System.out.println("added all " + T + " callables");
		ExecutorService threadPool = Executors
//			 .newFixedThreadPool(8);
//				.newCachedThreadPool();
//			.newSingleThreadExecutor();
			.newWorkStealingPool();
		try {
			threadPool.invokeAll(ifsCallables);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    
    } // end parallel2
    
	static class IFSWorker extends SwingWorker<List<Double>, Double>{
   	 
   	 private AtomicReference<Integer> tRef;
		private int T;
		private double[] dist;
		private double[][] cx;
		private double[][] cy;
		private AtomicReference<Double> xRef;
		private AtomicReference<Double> yRef;
		private Drawer drawer;

		public IFSWorker(AtomicReference<Integer> tRef, int T, double[] dist, double[][] cx, double[][] cy, double x, double y, AtomicReference<Double> xRef, AtomicReference<Double> yRef, Drawer drawer) {
			this.tRef = tRef;
			this.dist = dist;
			this.T = T;
			this.cx = cx;
			this.cy = cy;
			this.xRef = xRef;
			this.yRef = yRef;
			this.drawer = drawer;
		}

		@Override
		protected List<Double> doInBackground() throws Exception {
			// System.out.println("doInBackground()");
			if (tRef.get() < T) {
				// System.out.println("t: "+tRef.get() );
				// pick a random rule according to the probability distribution
				int r = StdRandom.discrete(dist);
	
				// do the update
				double x0 = cx[r][0] * xRef.get() + cx[r][1] * yRef.get() + cx[r][2];
				double y0 = cy[r][0] * xRef.get() + cy[r][1] * yRef.get() + cy[r][2];
				// x = x0;
				xRef.set(x0);
				// y = y0;
				yRef.set(y0);
	
				List<Double> resultList = new ArrayList<>();
				resultList.add(x0);
				resultList.add(y0);
				// System.out.println(tRef.get()+": list!");
				setProgress(tRef.get());
				publish(resultList.toArray(new Double[2]));
	
				return resultList;
			} else {
				System.out.println(tRef.get() + ": no list!");
				return Collections.emptyList();
			}
		}

		@Override
		protected void process(List<Double> chunks) {
			// System.out.println("process: "+chunks.size());
			if (chunks.size() != 0) {
			drawer.point(chunks.get(0), chunks.get(1));

			// for efficiency, display only every 100 iterations
			if (tRef.get() % 100 == 0) {
				// System.out.println("showing");
				drawer.show(10);
			}

			tRef.set(tRef.get() + 1);
			System.out.print(tRef.get() + " ");
			}
		} // end process
	} // end IFSWorker
    
	static class IFSCallable implements Callable<Void> {
		private IFSWorker ifsWorker;

		public IFSCallable(AtomicReference<Integer> tRef, int T, double[] dist, double[][] cx, double[][] cy, double x,
			double y, AtomicReference<Double> xRef, AtomicReference<Double> yRef, Drawer drawer) {
			ifsWorker = new IFSWorker(tRef, T, dist, cx, cy, x, y, xRef, yRef, drawer);
		}

		@Override
		public Void call() {
			ifsWorker.execute();
			return null;
		}
	};
    
    
    static class Drawer {
		private final BufferedImage offscreenImage, onscreenImage;
		private final Graphics2D offscreen, onscreen;
		private final Color penColor;
		private final double penRadius;
		private final JFrame frame;

		private static final double DEFAULT_XMIN = 0.0;
		private static final double DEFAULT_XMAX = 1.0;
		private static final double DEFAULT_YMIN = 0.0;
		private static final double DEFAULT_YMAX = 1.0;
		private static final double BORDER = 0.00;
		private final int DEFAULT_SIZE = 512;
		private final int width = DEFAULT_SIZE;
		private final int height = DEFAULT_SIZE;
		private final double DEFAULT_PEN_RADIUS = 0.002;
		private final Color DEFAULT_PEN_COLOR = Color.BLACK;
		private final Color DEFAULT_CLEAR_COLOR = Color.WHITE;
		private final Object mouseLock = new Object();
		
		private boolean defer = false;
		private double xmin, ymin, xmax, ymax;
   	 
   	 public Drawer() {
//   		 if (frame != null) { 
//   			 frame.setVisible(false); 
//   		 }
          frame = new JFrame();
   		 offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          onscreenImage  = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          offscreen = offscreenImage.createGraphics();
          onscreen  = onscreenImage.createGraphics();
          
          setXscale();
          setYscale();
          offscreen.setColor(DEFAULT_CLEAR_COLOR);
          offscreen.fillRect(0, 0, width, height);
          this.penColor = setPenColor();
          this.penRadius = setPenRadius();
//          setFont();
          clear();
          
       // add antialiasing
          RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
          hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
          offscreen.addRenderingHints(hints);
          
          ImageIcon icon = new ImageIcon(onscreenImage);
          JLabel draw = new JLabel(icon);

          frame.setContentPane(draw);
//          frame.addKeyListener(std);    // JLabel cannot get keyboard focus
          frame.setResizable(false);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);            // closes all windows
          frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);      // closes only current window
          frame.setTitle("Standard Draw");
//          frame.setJMenuBar(createMenuBar());
          frame.pack();
          frame.requestFocusInWindow();
          frame.setVisible(true);
		}
   	 
   	 public void clear() { clear(DEFAULT_CLEAR_COLOR); }
   	 public void clear(Color color) {
          offscreen.setColor(color);
          offscreen.fillRect(0, 0, width, height);
          offscreen.setColor(penColor);
          draw();
      }
   	 
       private void point(double x, double y) {
          double xs = scaleX(x);
          double ys = scaleY(y);
          double r = penRadius;
          float scaledPenRadius = (float) (r * DEFAULT_SIZE);

          // double ws = factorX(2*r);
          // double hs = factorY(2*r);
          // if (ws <= 1 && hs <= 1) pixel(x, y);
          if (scaledPenRadius <= 1) { 
         	 pixel(x, y);
          } else {
         	 offscreen.fill(new Ellipse2D.Double(xs - scaledPenRadius/2, ys - scaledPenRadius/2, scaledPenRadius, scaledPenRadius));
          }
//          System.out.println("drawing");
          draw();
      }
       private void pixel(double x, double y) {
          offscreen.fillRect((int) Math.round(scaleX(x)), (int) Math.round(scaleY(y)), 1, 1);
      }
       private double  scaleX(double x) { return width  * (x - xmin) / (xmax - xmin); }
       private double  scaleY(double y) { return height * (ymax - y) / (ymax - ymin); }
       
       

		private void setXscale() {
			setXscale(DEFAULT_XMIN, DEFAULT_XMAX);
		}
		private void setXscale(double min, double max) {
			double size = max - min;
			synchronized (mouseLock) {
				xmin = min - BORDER * size;
				xmax = max + BORDER * size;
			}
		}
		private void setYscale() {
			setYscale(DEFAULT_YMIN, DEFAULT_YMAX);
		}

		private void setYscale(double min, double max) {
			double size = max - min;
			synchronized (mouseLock) {
				ymin = min - BORDER * size;
				ymax = max + BORDER * size;
			}
		}

      private Color setPenColor() { 
      	return setPenColor(DEFAULT_PEN_COLOR); 
      }
      private Color setPenColor(Color color) {
          offscreen.setColor(color);
          return color;
      }
      
      private double setPenRadius() { 
      	return setPenRadius(DEFAULT_PEN_RADIUS); 
   	}
      private double setPenRadius(double r) {
          if (r < 0) 
         	 throw new IllegalArgumentException("pen radius must be nonnegative");
          
//          penRadius = r;
          float scaledPenRadius = (float) (r * DEFAULT_SIZE);
          BasicStroke stroke = new BasicStroke(scaledPenRadius, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
          // BasicStroke stroke = new BasicStroke(scaledPenRadius);
          offscreen.setStroke(stroke);
          return r;
      }
      
      public void show(int t) {
         defer = false;
         draw();
         try { 
         	Thread.sleep(t); 
         } catch (InterruptedException e) { 
         	System.out.println("Error sleeping"); 
      	}
         defer = true;
     }
      
      private void draw() {
         if (defer) 
         	return;
         onscreen.drawImage(offscreenImage, 0, 0, null);
         frame.repaint();
     }
	} // end Drawer
    
    private static double[] getDist() {
   	 double[] dist = { 0.01, 0.85, 0.07, 0.07 };
   	 return dist;
    }
    private static double[][] getCx() {
   	 double[][] cx = {
   			 {0.00, 0.00, 0.500},
			   {0.85, 0.04, 0.075},
			   {0.20, -0.26, 0.400},
			  {-0.15, 0.28, 0.575},
   	 };
   	 return cx;
    }
    private static double[][] getCy() {
   	 double[][] cy = {
   			 {0.00, 0.16, 0.000}, {-0.04, 0.85, 0.180}, {0.23, 0.22, 0.045}, {0.26,0.24, -0.086}
   	 };
   	 return cy;
    }
}