package net.iubris.fractal_landscapes.ifs.parallel;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import net.iubris.fractal_landscapes.ifs.Drawer;

public class IFSCallable implements Callable<Void> {
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
}