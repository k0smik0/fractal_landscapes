package uk.ac.ed.epcc.mountain.applet;

import java.applet.Applet;
// import Artist;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import uk.ac.ed.epcc.mountain.drawer.Artist;
import uk.ac.ed.epcc.mountain.model.Mountain;

//
// A simple wrapper applet for the javmountains code.
// Thanks to "Cain, Robert G." <rcain@ciena.com> for
// adding the mousehandler.

public class Mountapp extends Applet implements Runnable, MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1011762551672647298L;
	Artist art;
	Mountain m;
	Dimension d;
	Image img;
	int levels = 10;
	int stop = 2;
	double fdim = 0.65;
	boolean frozen = false;
	long little_sleep = 100;
	long long_sleep = 5000;
	Thread t;
	public final String pinfo[][] = {
			{ "levels", "1+", "levels of recursion (depth)" },
			{ "stop", "1-Levels", "number of non fractal recusions" },
			{ "fdim", "0.5-1.0", "Fractal dimansion" },
			{ "sleep", "miliseconds", "Sleep time before scrolling" },
			{ "snooze", "miliseconds", "Sleep time between columns" } };

	public String[][] getParameterInfo() {
		return (pinfo);
	}

	public void init() {
		Graphics g;
		String s;
		int i;
		double tmp;
		Double dble;

		// System.out.println("init called");
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
		}
		d = this.getSize();
		m = new Mountain();
		m.set_fdim(fdim);
		m.set_size(levels, stop);
		// m.init();
		img = createImage(d.width, d.height);
		g = img.getGraphics();
		art = new Artist(d.width, d.height, g, m);
		// art.init_artist_variables();
		addMouseListener(this);
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

	public void stop() {
		// System.out.println("stop called");
		t = null;
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
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

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

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void run() {
		// int i;
		// System.out.println("run called");
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
			/*if (snooze > 0) {
				// try{Thread.currentThread().sleep(snooze);}catch (InterruptedException e){}
				try {
					Thread.sleep(snooze);
				} catch (InterruptedException e) {
				}
			} else {
				// let other threads hava a go.
				// Thread.currentThread().yield();
				Thread.yield();
			}*/
			
			if (snooze<0)
				try {
					Thread.sleep(little_sleep*10);
				} catch (InterruptedException e) {}
			if (snooze >= 100) {
				Thread.yield();
			}
		}
	}
}
