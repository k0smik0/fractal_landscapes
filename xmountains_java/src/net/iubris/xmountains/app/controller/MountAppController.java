package net.iubris.xmountains.app.controller;

import net.iubris.xmountains.app.view.MountAppFrame;
import uk.ac.ed.epcc.mountain.drawer.Artist;
import uk.ac.ed.epcc.mountain.model.Mountain;

public class MountAppController implements Runnable {

//	private MountAppFrame mountAppFrame;
//	private BasicMouseListener basicMouseListener;
	private final Artist artist;
	private final MountAppFrame mountAppFrame;
	private BasicMouseListener basicMouseListener;
//	private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(16);
	private Thread t;
	
	long little_sleep = 100;
	long long_sleep = 5000;

	public MountAppController(Artist artist, MountAppFrame mountAppFrame/*, BasicMouseListener basicMouseListener*/) {
		this.artist = artist;
		this.mountAppFrame = mountAppFrame;
	}
	
	@Override
	public void run() {
	// System.out.println("run called");
			long snooze = 0;
			long target_time;

			// Just to be nice, lower this thread's priority
			// so it can't interfere with other processing going on.
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			
			while (Thread.currentThread() == t) {
//			while (true) {
//				System.out.println("here");
				target_time = System.currentTimeMillis();
				artist.plot_column();
				mountAppFrame.repaint();
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
	
	
//	@Override
	public void start() {
		System.out.println("start called");
		if (basicMouseListener.isFrozen()) {
			// Do nothing motion is stopped
		} else {
			// start animating

			if (t == null) {
//				MountAppRunnable mountAppRunnable = new MountAppRunnable(artist, mountAppFrame/*, long_sleep, little_sleep*/, Thread.currentThread());
//				t = new Thread( mountAppRunnable );
				t = new Thread(this);
			}
			t.start();
//			MountAppRunnable mountAppRunnable = new MountAppRunnable(artist, mountAppFrame);
//			fixedThreadPool.execute(mountAppRunnable);
		}
	}
	
//	@Override
	public void stop() {
		 System.out.println("stop called");
//		fixedThreadPool.shutdown();
		t = null;
	}
	
	public void setMouseListener(BasicMouseListener basicMouseListener) {
		this.basicMouseListener = basicMouseListener;
	}
	
	public static void main(String[] args) {
		
		String pinfo[][] = {
			{ "levels", "1+", "levels of recursion (depth)" },
			{ "stop", "1-Levels", "number of non fractal recusions" },
			{ "fdim", "0.5-1.0", "Fractal dimansion" },
			{ "sleep", "miliseconds", "Sleep time before scrolling" },
			{ "snooze", "miliseconds", "Sleep time between columns" } 
		};
		
		if (args.length==1 && args[0].equals("-h")) {
			System.out.println( pinfo );
		}
			
		String levels = "9";
		String sleep = "1000";
		int width = 512;
		int height = 384;
		
		Parameter parameter = new Parameter(levels, sleep, ""+width, ""+height);
		
		/*EventQueue.invokeLater(()-> {
			try {
				MountAppFrame mountAppFrame = new MountAppFrame(parameter, width, height);
				
				mountAppFrame.setVisible(true);
				mountAppFrame.init();
				mountAppFrame.start();
				mountAppFrame.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});*/
		
		MountAppFrame mountAppFrame = 
				//MountAppFrame.getInstance(width, height);
				new MountAppFrame(width, height);
		mountAppFrame.init();
		Mountain mountain = new MountainBuilder(parameter).build();
		Artist artist = new Artist(mountAppFrame.getWidth()-10, mountAppFrame.getHeight()-10, mountAppFrame.getGraphics(), mountain);
		
		MountAppController mountAppController  = new MountAppController(artist, mountAppFrame);
		
		BasicMouseListener basicMouseListener = new BasicMouseListener(mountAppFrame);
		mountAppController.setMouseListener(basicMouseListener);
		basicMouseListener.setController(mountAppController);
		mountAppFrame.addMouseListener(basicMouseListener);
		
		mountAppController.start();
		
//		mountAppFrame.setVisible(true);
//		mountAppFrame.init();
//		mountAppFrame.start();
//		mountAppFrame.run();
		
	}
	
	public static class Parameter {
		String levels;
		String sleep;
		String width;
		String height;

		public Parameter(String levels, String sleep, String width, String height) {
			this.levels = levels;
			this.sleep = sleep;
			this.width = width;
			this.height = height;
		}
	}

}
