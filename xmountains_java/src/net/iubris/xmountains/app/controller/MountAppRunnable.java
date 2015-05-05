package net.iubris.xmountains.app.controller;

import javax.swing.JFrame;

import uk.ac.ed.epcc.mountain.drawer.Artist;

public class MountAppRunnable implements Runnable {

//	private Artist art;
	private final JFrame jFrame;
	
//	int levels = 10;
//	int stop = 2;
//	double fdim = 0.65;
//	boolelan frozen = false;
//	long little_sleep = 100;
//	long long_sleep = 5000;

//	private final long long_sleep;
//	private final long little_sleep;
	
//	private final Parameter parameter;
//	private final Mountain mountain;
	private final Artist artist;
	
	long little_sleep = 100;
	long long_sleep = 5000;

	private Thread thread;
	
	public MountAppRunnable(/*Mountain mountain,*/ /*Parameter parameter,*/ Artist artist, JFrame jFrame/*, long long_sleep, long little_sleep*/, Thread thread) {
//		this.parameter = parameter;
		this.artist = artist;
		this.jFrame = jFrame;
//		this.mountain = mountain;
//		this.long_sleep = long_sleep;
//		this.little_sleep = little_sleep;
		this.thread = thread;
	}

	@Override
	public void run() {
	// System.out.println("run called");
			long snooze = 0;
			long target_time;

			// Just to be nice, lower this thread's priority
			// so it can't interfere with other processing going on.
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			
//			while (Thread.currentThread() == thread) {
				
			while (true) {
//				System.out.println("here");
				target_time = System.currentTimeMillis();
				artist.plot_column();
				jFrame.repaint();
				// attempt to subract the update time from the sleep time
				// to give a constant update rate. If the update takes too long
				// things will of course be slower.
				if (artist.scroll != 0) {
					// snooze=long_sleep;
					snooze = target_time + long_sleep - System.currentTimeMillis();
				} else {
					// snooze=little_sleep;
					snooze = target_time + little_sleep - System.currentTimeMillis();
				}
				if (snooze > 0) {
					// try{Thread.currentThread().sleep(snooze);}catch (InterruptedException e){}
					try {
						Thread.sleep(snooze);
					} catch (InterruptedException e) {}
				} else {
					// let other threads hava a go.
					// Thread.currentThread().yield();
					Thread.yield();
				}
			}
	}
}
