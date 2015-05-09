package net.iubris.fractal_landscapes.ifs.parallel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingWorker;

import net.iubris.fractal_landscapes.ifs.Drawer;
import stdlibs.StdRandom;

class IFSWorker extends SwingWorker<List<Double>, Double>{
	 
	 private AtomicReference<Integer> tRef;
	private int T;
	private double[] dist;
	private double[][] cx;
	private double[][] cy;
	private AtomicReference<Double> xRef;
	private AtomicReference<Double> yRef;
	private Drawer drawer;
	
	private static final Object lock = new Object();

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
		synchronized(lock)  {
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
		}
	} // end process
} // end IFSWorker