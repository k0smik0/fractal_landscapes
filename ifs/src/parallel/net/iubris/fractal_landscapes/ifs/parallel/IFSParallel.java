package net.iubris.fractal_landscapes.ifs.parallel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import net.iubris.fractal_landscapes.ifs.Drawer;
import stdlibs.StdDraw;
import stdlibs.StdRandom;

public class IFSParallel {

	public IFSParallel() {
	}

	public static void parallel(int T, double[] dist, double[][] cx, double[][] cy, double x, double y) {
	   	 
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

	static void parallel2(int T, double[] dist, double[][] cx, double[][] cy, double x, double y, Drawer drawer) {
	   	 
	   	 AtomicReference<Integer> tRef = new AtomicReference<>(0);
	       AtomicReference<Double> xRef = new AtomicReference<>(0.0);
	       AtomicReference<Double> yRef = new AtomicReference<>(0.0);
	   	 
			List<IFSCallable> ifsCallables = new ArrayList<>();
			System.out.println("adding callables");
			for (int i = 0; i < T; i++) {
				ifsCallables.add(new IFSCallable(tRef, T, dist, cx, cy, x, y, xRef, yRef, drawer));
			}
	//		System.out.println("added all " + T + " callables");
			ExecutorService threadPool = Executors
	//			 .newFixedThreadPool(8);
					.newCachedThreadPool();
	//			.newSingleThreadExecutor();
	//			.newWorkStealingPool();
	//		for (int i = 0; i < T; i++) {
	//			threadPool.submit(ifsCallables.get(i));
	////			if (i%1000==0)
	////				try {
	//////					Thread.sleep(500);
	////					continue;
	////				} catch (InterruptedException e) {
	////					e.printStackTrace();
	////				}
	//		}
			
			try {
				threadPool.invokeAll(ifsCallables);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    
	    } // end parallel2

}
