package net.iubris.fractal_landscapes.xmountains.applet;

import java.applet.Applet;
// import Artist;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import net.iubris.fractal_landscapes.xmountains.common.core.Mountain;
import net.iubris.fractal_landscapes.xmountains.common.drawer.Artist;

//
// A simple wrapper applet for the javmountains code.
// Thanks to "Cain, Robert G." <rcain@ciena.com> for
// adding the mousehandler.

public class XMountainsApplet extends Applet implements Runnable, MouseListener {
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
			{ "fdim", "0.5-1.0", "Fractal dimension" },
			{ "seaLevel", "0+", "Sea level"},
			{ "sleep", "miliseconds", "Sleep time before scrolling" },
			{ "snooze", "miliseconds", "Sleep time between columns" } };
	private Double seaLevel;

	public String[][] getParameterInfo() {
		return (pinfo);
	}
	

	public final void setLevels(int levels) {
		this.levels = levels;
		m.set_size(levels, stop);
	}
	public void setStop(int stop) {
		this.stop = stop;
		m.set_size(levels, stop);
	}
	public void setFdim(double fdim) {
		this.fdim = fdim;
		m.set_fdim(this.fdim);
	}
	public void setSealevel(double seaLevel) {
		this.art.setSealevel(this.seaLevel);
	}
	public final void setSleep(long sleep) {
		this.long_sleep = sleep;
	}
	public final void setSnooze(long snooze) {
		this.little_sleep = snooze;
	}
	

	
	@Override
	public void init() {
		Graphics g;
		String s;
		int i;
		double tmp;
		Double dble;

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
		s = getParameter("seaLevel");
		if (s != null) {
			Double sl = Double.parseDouble(s);
			this.seaLevel = sl;
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
	public void mouseClicked(MouseEvent e) {
		pauseOrContinue();
	}
	public void pauseOrContinue() {
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
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}

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
			
//			System.out.println(little_sleep);
//			if (snooze>100) {
//			System.out.println(art.getColumnsPlotted());
			if (art.getColumnsPlotted() % getSize().getWidth() ==0) {
				try {
					Thread.sleep(2*long_sleep);
				} catch (InterruptedException e) {}
			}
//			System.out.println(snooze);
			if (snooze >= 98) {
				Thread.yield();
			}
		}
	}
}
