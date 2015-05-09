package net.iubris.fractal_landscapes.ifs.common.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import net.iubris.fractal_landscapes.ifs.common.Drawer;
import stdlibs.StdRandom;

public class IFSExecutor {

	public static void sequential(int iterations, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer, Thread thread, Printer printer) {
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
          if (Thread.currentThread() == thread) {
//         	 if (img != null) {
//         		 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
//       		}
         	 drawer.point(x, y);
          }
//          StdDraw.point(x, y); 

          // for efficiency, display only every 100 iterations
          if (i % 250 == 0) { 
//         	 StdDraw.show(10);
//         	 super.getGraphics().drawImage(img, 0, 0, Color.black, this);
//         	 try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
         	 drawer.show(10);
			}

			if (i % (iterations / 5) == 0) {
				printer.print(i+" ");
			}
		}
		printer.print(""+i);
   }

	public static void parallel(int iterations, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer, Thread thread, Printer printer) {
		
		AtomicReference<Integer> tRef = new AtomicReference<>(0);
      AtomicReference<Double> xRef = new AtomicReference<>(x);
      AtomicReference<Double> yRef = new AtomicReference<>(y);
		
      Callable<Void> callable = new Callable<Void>() {
			@Override
			public Void call() {
//				if (tRef.get() < iterations) {
//					System.out.print(tRef.get()+" ");
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
//					if (Thread.currentThread() == thread) {
						drawer.point(xRef.get(), yRef.get());
//					}
		
					// for efficiency, display only every 100 iterations
					if (tRef.get() % 250 == 0) {
						drawer.show(10);
					}
					
					tRef.set( tRef.get()+1 );
					Integer t = tRef.get();
					if (t%(iterations/5) == 0) {
						printer.print(t+" ");
					}
//				}
				return null;
			} 
		}; // end Runnable
		
		
		List<Callable<Void>> ifsCallables = new ArrayList<>();
		for (int i = 0; i < (iterations); i++) {
			ifsCallables.add(callable);
		}
		
//		int availableProcessors = Runtime.getRuntime().availableProcessors();
//		ForkJoinPool pool = new ForkJoinPool(availableProcessors);
//		pool.invokeAll(ifsCallables);
		
		ifsCallables.stream().parallel().forEach(a->{
			try {
				a.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
//		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(16);
//		try {
//			fixedThreadPool.invokeAll(ifsCallables);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		
		printer.print(iterations+" ");
	}
	
	public static void parallelTask(int iterations, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer, Thread thread, Printer printer) {
		AtomicReference<Integer> tRef = new AtomicReference<>(0);
      AtomicReference<Double> xRef = new AtomicReference<>(x);
      AtomicReference<Double> yRef = new AtomicReference<>(y);
		
      Callable<Double[]> callable = new Callable<Double[]>() {
			@Override
			public Double[] call() {
//				if (tRef.get() < iterations) {
//					System.out.print(tRef.get()+" ");
					// pick a random rule according to the probability distribution
					int r = StdRandom.discrete(dist);
		
					// do the update
					double x0 = cx[r][0] * xRef.get() + cx[r][1] * yRef.get() + cx[r][2];
					double y0 = cy[r][0] * xRef.get() + cy[r][1] * yRef.get() + cy[r][2];
					// x = x0;
					xRef.set(x0);
					// y = y0;
					yRef.set(y0);
		
					/*// draw the resulting point
					if (Thread.currentThread() == thread) {
						drawer.point(xRef.get(), yRef.get());
					}
					// for efficiency, display only every 100 iterations
					if (tRef.get() % 100 == 0) {
						drawer.show(10);
					}*/
					
					tRef.set( tRef.get()+1 );
					Integer t = tRef.get();
					if (t%(iterations/5) == 0) {
						printer.print(t+" ");
					}
//				}
				return new Double[] {xRef.get(), yRef.get()};
			} 
		}; // end Runnable

//		new FutureTask<>(callable);
		
		List<Callable<Double[]>> ifsCallables = new ArrayList<>();
		for (int i = 0; i < (iterations); i++) {
			ifsCallables.add(callable);
		}
		
		ExecutorService fixedThreadPool = Executors.newFixedThreadPool(4);
		try {
			List<Future<Double[]>> invokeAll = fixedThreadPool.invokeAll(ifsCallables);
			invokeAll.stream().parallel().forEach(c->{
				if (c.isDone()) {
					try {
						Double[] doubles = c.get();
						double xx = doubles[0];
						double yy = doubles[1];
						
//						if (Thread.currentThread() == thread) {
							drawer.point(xx, yy);
//						}
						// for efficiency, display only every 100 iterations
						if (tRef.get() % 250 == 0) {
							drawer.show(10);
						}
							
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
	public interface Printer {
		void print(String string);
		void println(String string);
	}
	
}
