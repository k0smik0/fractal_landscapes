package net.iubris.fractal_landscapes.ifs;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.ImageObserver;

import stdlibs.StdRandom;

public class IFSApplet extends Applet implements Runnable, ImageObserver {

	private static final long serialVersionUID = -7315365287365450617L;

	private double[] dist;
	private double[][] cx;
	private double[][] cy;
	double x,y;

	private int iterations;
	
	private Thread thread;

	private Image img;

	
	@Override
	public void run() {
//		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		sequential(iterations, dist, cx, cy, x, y);
	}
	
	@Override
	public void init() {
		iterations = 50000;
		
		int width = 800;
		int height = 600;
		
		img = createImage(width, height);
//		g = img.getGraphics();
		
		
	// probability distribution for choosing each rule
      dist = getDist();

      // update matrices
      cx = getCx();
      cy = getCy();

      // current value of (x, y)
      x = 0.0; y = 0.0;
	}
	
	@Override
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
		}
		thread.start();
	}
	
	@Override
	public void stop() {
		thread = null;
	}
	
//	@Override
//	public void paint(Graphics g) {
//		if (img != null) {
//			g.drawImage(img, 0, 0, Color.black, this);
//		}
//	}
	
	private void sequential(int iterations, double[] dist, double[][] cx, double[][] cy, double x, double y) {
		// do T iterations of the chaos game
  	 	int i;
      for (i = 0; i < iterations; i++) { 
          // pick a random rule according to the probability distribution
          int r = StdRandom.discrete(dist); 

          // do the update
          double x0 = cx[r][0]*x + cx[r][1]*y + cx[r][2]; 
          double y0 = cy[r][0]*x + cy[r][1]*y + cy[r][2]; 
          x = x0;
          y = y0;

          // draw the resulting point
//          if (Thread.currentThread() == thread) {
         	 if (img != null) {
         		 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
       		}
//    		}
//          StdDraw.point(x, y); 

          // for efficiency, display only every 100 iterations
          if (i % 250 == 0) { 
//         	 StdDraw.show(10);
         	 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
         	 try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
          }
          
          if (i%(iterations/5)==0)
        	  System.out.print(i+" ");
      }
      System.out.println(i);
   }
	
	private double[] getDist() {
  	 double[] dist = { 0.01, 0.85, 0.07, 0.07 };
  	 return dist;
   }
   private double[][] getCx() {
  	 double[][] cx = {
  			 {0.00, 0.00, 0.500},
			   {0.85, 0.04, 0.075},
			   {0.20, -0.26, 0.400},
			  {-0.15, 0.28, 0.575},
  	 };
  	 return cx;
   }
   private double[][] getCy() {
  	 double[][] cy = {
  			 {0.00, 0.16, 0.000}, {-0.04, 0.85, 0.180}, {0.23, 0.22, 0.045}, {0.26,0.24, -0.086}
  	 };
  	 return cy;
   }

}
