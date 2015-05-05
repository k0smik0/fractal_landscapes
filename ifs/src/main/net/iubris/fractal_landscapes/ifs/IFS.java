package net.iubris.fractal_landscapes.ifs;
import stdlibs.StdDraw;
import stdlibs.StdRandom;

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
   	 int T;
   	 if (args.length>1) {
   		 T = Integer.parseInt(args[1]);
   	 } else {
   		 T = 50000;
   	 }
   	 System.out.print(T+" iterations: ");

        // probability distribution for choosing each rule
        double[] dist = 
//       		  StdArrayIO.readDouble1D();
     		  getDist();

        // update matrices
        double[][] cx = 
//       		  StdArrayIO.readDouble2D();
     		  getCx();
        double[][] cy = 
//       		  StdArrayIO.readDouble2D();
     		  getCy();

        // current value of (x, y)
        double x = 0.0, y = 0.0;

        // do T iterations of the chaos game, ensure everything gets drawn
        long start = System.currentTimeMillis();
        sequential(T, dist, cx, cy, x, y); StdDraw.show(0);
//        parallel(T, dist, cx, cy, x, y); StdDraw.show(0);
        
//        Drawer drawer = new Drawer(); IFSParallel.parallel2(T, dist, cx, cy, x, y, drawer); drawer.show(0);

        long end = System.currentTimeMillis();
        float time = (end-start)/1000f;
        System.out.println("\ntime:"+time+"s");
        
//        System.exit(0);
    }
    
    private static void sequential(int T, double[] dist, double[][] cx, double[][] cy, double x, double y) {
   	 // do T iterations of the chaos game
   	 int t;
       for (t = 0; t < T; t++) { 
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
       System.out.println(t);
    }
    
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