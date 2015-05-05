package uk.ac.ed.epcc.mountain;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.Scanner;

import javax.swing.JFrame;

import uk.ac.ed.epcc.mountain.drawer.Artist;
import uk.ac.ed.epcc.mountain.model.Mountain;

//
// A simple wrapper applet for the javmountains code.
// Thanks to "Cain, Robert G." <rcain@ciena.com> for
// adding the mousehandler.

public class MountappSuperFrame extends JFrame implements Runnable, MouseListener, DummyApplet {
	
	private static final long serialVersionUID = -1011762551672647298L;
	Artist art;
	Mountain m;
	Image img;
	int levels = 10;
	int stop = 2;
	double fdim = 0.65;
	boolean frozen = false;
	long little_sleep = 100;
	long long_sleep = 5000;
	int width;
	int height;
	Thread t;
//	public final String pinfo[][] = {
//			{ "levels", "1+", "levels of recursion (depth)" },
//			{ "stop", "1-Levels", "number of non fractal recusions" },
//			{ "fdim", "0.5-1.0", "Fractal dimansion" },
//			{ "sleep", "miliseconds", "Sleep time before scrolling" },
//			{ "snooze", "miliseconds", "Sleep time between columns" } };
	private Parameter parameter;

	public MountappSuperFrame(Parameter parameter) {
		super();
		setSize(512, 384);
		this.parameter = parameter;
		this.width = 
//				512;
				Integer.parseInt(
				parameter.width );
		this.height = 
//				384;
				Integer.parseInt(
				parameter.height );
		
		getContentPane().setLayout( new FlowLayout() );
		
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 2, 2));
		
		setTitle("xmountains");
		
//		setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
		
//		setSize(width, height);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
//		addMouseListener(mouseListener);
		
		setVisible(true);
		pack();
		repaint();
	}

//	public String[][] getParameterInfo() {
//		return (pinfo);
//	}

	public void init() {
		Graphics g;
		String s;
		int i;
		double tmp;
		Double dble;

		// d = getSize();
		s = getParameter("levels");
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			levels = i;
			}
		}
		s = getParameter("stop");
		if (s != null) {
			i = Integer.parseInt(s);
			if (i > 1) {
			stop = i;
			}
		}
		s = getParameter("fdim");
		if (s != null) {
			dble = new Double(s);
			tmp = dble.doubleValue();
			if (tmp > 0.5 && tmp <= 1.0) {
			fdim = tmp;
			}
		}
		s = getParameter("sleep");
		if (s != null) {
			i = Integer.parseInt(s);
			long_sleep = (long) i;
		}
		s = getParameter("snooze");
		if (s != null) {
			i = Integer.parseInt(s);
			little_sleep = (long) i;
			System.out.println(little_sleep);
		}
//		d = this.getSize();
		m = new Mountain();
		m.set_fdim(fdim);
		m.set_size(levels, stop);
		// m.init();
		img = createImage(width, height);
		g = img.getGraphics();
		art = new Artist(width, height, g, m);
		// art.init_artist_variables();
		addMouseListener(this);
	}
	@Override
	public String getParameter(String parameterName) {
		try {
			Field field = parameter.getClass().getDeclaredField(parameterName);
			field.setAccessible(true);
			return (String) field.get(parameter);
		} catch(NoSuchFieldException e) {
//			e.printStackTrace();
			System.err.println("no field «"+parameterName+"»");
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		// System.out.println("paint called");
		if (img != null) {
			g.drawImage(img, 0, 0, Color.black, this);
		}
	}

	@Override
	public void print(Graphics g) {
		if (img != null) {
			g.drawImage(img, 0, 0, Color.black, this);
		}
	}

	@Override
	public void update(Graphics g) {
		if (img != null) {
			g.drawImage(img, 0, 0, Color.black, this);
		}
	}

	@Override
	public void start() {
		// System.out.println("start called");
		if (frozen) {
			// Do nothing motion is stopped
		} else {
			// start animating
			if (t == null) {
				t = new Thread(this);
			}
			t.start();
		}
	}

	@Override
	public void stop() {
		// System.out.println("stop called");
		t = null;
	}
	
	@Override
	public void showStatus(String status) {
		System.out.println(status);
	}
	/*
	 * public boolean mouseDown(Event e, int x, int y) {
	 * if (frozen) {
	 * frozen = false;
	 * showStatus("mountapp restarted");
	 * start();
	 * } else {
	 * frozen = true;
	 * showStatus("mountapp paused");
	 * stop();
	 * }
	 * return true;
	 * }
	 */
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (frozen) {
			frozen = false;
			showStatus("mountapp restarted");
			start();
		} else {
			frozen = true;
			showStatus("mountapp paused");
			stop();
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void run() {
		// int i;
		long snooze = 0;
		long target_time;

		// Just to be nice, lower this thread's priority
		// so it can't interfere with other processing going on.
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		
		while (Thread.currentThread() == t) {
			target_time = System.currentTimeMillis();
			art.plot_column();
			repaint();
			// attempt to subract the update time from the sleep time
			// to give a constant update rate. If the update takes too long
			// things will of course be slower.
			if (art.scroll != 0) {
				// snooze=long_sleep;
				snooze = target_time + long_sleep - System.currentTimeMillis();
			} else {
				// snooze=little_sleep;
				snooze = target_time + little_sleep - System.currentTimeMillis();
			}
//			System.out.print(" "+snooze);
//			snooze = 10;
			if (snooze<0)
				try {
					Thread.sleep(little_sleep*10);
				} catch (InterruptedException e) {}
			if (snooze >= 100) {
				Thread.yield();
			}
			/*if (snooze > 0) {
				// try{Thread.currentThread().sleep(snooze);}catch (InterruptedException e){}
				try {
					Thread.sleep(snooze);
				} catch (InterruptedException e) {}
			} else {
				// let other threads hava a go.
				// Thread.currentThread().yield();
				Thread.yield();
			}*/
		}
	}
	
	public static void main(String[] args) {
			
			String pinfo[][] = {
				{ "levels", "1+", "levels of recursion (depth)" },
				{ "stop", "1-Levels", "number of non fractal recusions" },
				{ "fdim", "0.5-1.0", "Fractal dimension" },
				{ "sleep", "miliseconds", "Sleep time before scrolling" },
				{ "snooze", "miliseconds", "Sleep time between columns" } 
			};
			
			if (args.length==1 && args[0].equals("-h")) {
				System.out.println( pinfo );
				System.exit(0);
			}
			
			int width = 512;
			int height = 384;
			
			Parameter parameter = null;
			
			if (args.length==0) {
				String levels = "9";
				String sleep = "1000";
					sleep = "0";
				parameter = new Parameter(levels, sleep, ""+width, ""+height);
			}
			
			if (args.length==1 && !args[0].equals("-h")) {
				Scanner scanner = new Scanner(System.in);
				int levels = scanner.nextInt();
				int stop = scanner.nextInt();
				double fdim = scanner.nextDouble();
				int sleep = scanner.nextInt();
				int snooze = scanner.nextInt();
				snooze = 0;
				scanner.close();
				
				parameter = new Parameter(""+levels, ""+stop, ""+fdim, ""+sleep, ""+snooze, ""+width, ""+height);
			}
			
			MountappSuperFrame mountappSuperFrame = new MountappSuperFrame(parameter);
			mountappSuperFrame.setSize(width, height);
			mountappSuperFrame.init();
			mountappSuperFrame.start();
			mountappSuperFrame.run();
	}
	
	public static class Parameter {
		String levels;
		String sleep;
		String width;
		String height;
		
		String stop;
		String fdim;
		String snooze;

		public Parameter(String levels, String sleep, String width, String height) {
			this.levels = levels;
			this.sleep = sleep;
			this.width = width;
			this.height = height;
		}

		public Parameter(String levels, String stop, String fdim, String sleep, String snooze, String  width, String height) {
			this(levels, sleep, width, height);
			this.stop = stop;
			this.fdim = fdim;
			this.snooze = snooze;
		}
	}
}
