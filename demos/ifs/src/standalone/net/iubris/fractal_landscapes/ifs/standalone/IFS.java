package net.iubris.fractal_landscapes.ifs.standalone;
import net.iubris.fractal_landscapes.ifs.common.Drawer;
import net.iubris.fractal_landscapes.ifs.common.parallel.IFSExecutor;
import net.iubris.fractal_landscapes.ifs.common.parallel.IFSExecutor.Printer;

/*
 * this is a simplified/enhanced version of IFS demo from
 * http://introcs.cs.princeton.edu/java/22library/IFS.java.html
 * 
 * modified by: Massimiliano Leone - http://plus.google.com/+MassimilianoLeone
 * 2015, GPL license
 */

/**
 *  sample data files available at:
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
 */

public class IFS {
	
	private static final int DEFAULT_ITERATIONS = 200000;
	
	
    public static void main(String[] args) {
   	 
   	 /*String help = "You need at least specify 1 argument:\n"
				 +" 's' or 'p': sequential|parallel\n\n"
				 +"2^ argument (numeric, optional) will be treated as iterations number";
   	 
   	 if (args.length<1) {
   		 System.out.println(help);
   		 System.exit(0);
   	 }
   	 
   	 boolean useParallel = false;
   	 String a = args[0] ;
   	 if (a.equalsIgnoreCase("s"))
   		 useParallel = false;
   	 else if (a.equalsIgnoreCase("p"))
   		 useParallel = true;*/
   	 
   	 ConsolePrinter printer = new ConsolePrinter();
   	 
        // number of iterations
   	 int iterations;
//   	 if (args.length==2) {
//   	 	iterations = Integer.parseInt(args[1]);
   	 if (args.length==1) {
   		 iterations = Integer.parseInt(args[0]);
   	 } else {
   		 printer.println("You did not specify iterations as argument, so default number will be used");
   		 iterations = DEFAULT_ITERATIONS;
   	 }
   	 printer.println(iterations+" iterations");

        // probability distribution for choosing each rule
        double[] dist = getDist();

        // update matrices
        double[][] cx = getCx();
        double[][] cy = getCy();

        // current value of (x, y)
        double x = 0.0, y = 0.0;
        
        Drawer drawer = new JFrameDrawer();

        // do T iterations of the chaos game, ensure everything gets drawn
        long start = System.currentTimeMillis();
//        if (!useParallel) {
      	  printer.print("Sequential: ");
      	  IFSExecutor.sequential(iterations, dist, cx, cy, x, y, drawer, Thread.currentThread(), printer); drawer.show(0);
//        }
        long end = System.currentTimeMillis();
        float time = (end-start)/1000f;
        printer.println(" in: "+time+"s");
      		  
        printer.print("Relax! Restart in: ");
        try {
      	  for (int i=5; i>0; i--) {
      		  printer.print(i+" ");
      		  Thread.sleep(500);
      	  }
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        printer.println("");
        drawer.clear();
        
        start = System.currentTimeMillis();
//        if (!useParallel) {
      	  printer.print("Parallel: ");
      	  IFSExecutor.parallel(iterations, dist, cx, cy, x, y, drawer, Thread.currentThread(), printer);
//      	  IFSExecutor.parallelTask(iterations, dist, cx, cy, x, y, drawer, Thread.currentThread(), consolePrinter);
//        }
        drawer.show(0);
        
        end = System.currentTimeMillis();
        time = (end-start)/1000f;
        printer.println(" in: "+time+"s");
        
//        System.exit(0);
    }
    
    static class ConsolePrinter implements Printer {
   	 public void print(String string) {
   		 System.out.print(string);
   	 }
   	 public void println(String string) {
   		 System.out.println(string);
   	 }
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